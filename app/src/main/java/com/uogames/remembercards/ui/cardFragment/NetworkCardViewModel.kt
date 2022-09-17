package com.uogames.remembercards.ui.cardFragment

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uogames.dto.global.Card
import com.uogames.dto.global.Image
import com.uogames.dto.global.Phrase
import com.uogames.dto.global.Pronunciation
import com.uogames.map.CardMap.update
import com.uogames.map.PhraseMap.update
import com.uogames.remembercards.utils.ifNull
import com.uogames.remembercards.utils.ifNullOrEmpty
import com.uogames.remembercards.utils.observeWhile
import com.uogames.repository.DataProvider
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.*
import javax.inject.Inject
import kotlin.collections.HashMap

class NetworkCardViewModel @Inject constructor(private val provider: DataProvider) : ViewModel() {

	private val viewModelScope = CoroutineScope(Dispatchers.IO)

	inner class CardModel(val card: Card) {
		val phrase by lazy { viewModelScope.async { getPhraseById(card.idPhrase) } }
		val translate by lazy { viewModelScope.async { getPhraseById(card.idTranslate) } }
		val image by lazy { viewModelScope.async { card.idImage?.let { getImageById(it) } } }
		val phrasePronounce by lazy { viewModelScope.async { phrase.await()?.idPronounce?.let { getPronunciationById(it) } } }
		val phrasePronounceData by lazy { viewModelScope.async { phrase.await()?.idPronounce?.let { getPronounceData(it) } } }
		val phraseImage by lazy { viewModelScope.async { phrase.await()?.idImage?.let { getImageById(it) } } }
		val translatePronounce by lazy { viewModelScope.async { translate.await()?.idPronounce?.let { getPronunciationById(it) } } }
		val translatePronounceData by lazy { viewModelScope.async { translate.await()?.idPronounce?.let { getPronounceData(it) } } }
		val translateImage by lazy { viewModelScope.async { translate.await()?.idImage?.let { getImageById(it) } } }
	}

	private class DownloadAction(val job: Job, var callback: (String) -> Unit)

	val like = MutableStateFlow("")

	private val _size = MutableStateFlow(0L)
	val size = _size.asStateFlow()

	private val downloadAction = HashMap<UUID, DownloadAction>()

	private var searchJob: Job? = null

	init {
		like.observeWhile(viewModelScope) {
			searchJob?.cancel()
			searchJob = viewModelScope.launch {
				_size.value = 0
				delay(300)
				runCatching {
					_size.value = provider.cards.countGlobal(like.value)
				}.onFailure {
					_size.value = 0
				}
			}
		}
	}

	suspend fun getByGlobalId(uuid: UUID) = viewModelScope.async { provider.cards.getByGlobalId(uuid) }.await()

	suspend fun getByPosition(position: Long): CardModel? {
		runCatching { return CardModel(provider.cards.getGlobal(like.value, position)) }
		return null
	}

	private suspend fun getPhraseById(id: UUID): Phrase? {
		runCatching { return provider.phrase.getGlobalById(id) }
		return null
	}

	private suspend fun getImageById(id: UUID): Image? {
		runCatching { return provider.images.getGlobalById(id) }
		return null
	}

	private suspend fun getPronunciationById(id: UUID): Pronunciation? {
		runCatching { return provider.pronounce.getGlobalById(id) }
		return null
	}

	private suspend fun getPronounceData(id: UUID): ByteArray? {
		runCatching { return provider.pronounce.downloadData(id) }
		return null
	}

	fun setDownloadAction(id: UUID, loading: (String) -> Unit): Boolean {
		downloadAction[id]?.callback = loading
		return downloadAction[id]?.job?.isActive.ifNull { false }
	}

	fun stopDownloading(id: UUID) {
		val action = downloadAction[id].ifNull { return }
		action.job.cancel()
		action.callback("Cancel")
		downloadAction.remove(id)
	}

	fun save(cardModel: CardModel, loading: (String) -> Unit) {
		val job = viewModelScope.launch {
			runCatching {
				val phraseImage = cardModel.phraseImage.await()?.globalId?.let {
					provider.images.getByGlobalId(it).ifNull { provider.images.download(it) }
				}
				val phrasePronounce = cardModel.phrasePronounce.await()?.globalId?.let {
					provider.pronounce.getByGlobalId(it).ifNull { provider.pronounce.download(it) }
				}
				val phrase = cardModel.phrase.await()?.globalId?.let { provider.phrase.getByGlobalId(it) }
				phrase?.let {
					provider.phrase.update(it.update(cardModel.phrase.await(), phrasePronounce?.id, phraseImage?.id))
				}.ifNull {
					provider.phrase.add(com.uogames.dto.local.Phrase().update(cardModel.phrase.await(), phrasePronounce?.id, phraseImage?.id))
				}
				val translateImage = cardModel.translateImage.await()?.globalId?.let {
					provider.images.getByGlobalId(it).ifNull { provider.images.download(it) }
				}
				val translatePronounce = cardModel.translatePronounce.await()?.globalId?.let {
					provider.pronounce.getByGlobalId(it).ifNull { provider.pronounce.download(it) }
				}
				val translate = cardModel.translate.await()?.globalId?.let { provider.phrase.getByGlobalId(it) }
				translate?.let {
					provider.phrase.update(it.update(cardModel.translate.await(), translatePronounce?.id, translateImage?.id))
				}.ifNull {
					provider.phrase.add(
						com.uogames.dto.local.Phrase().update(cardModel.translate.await(), translatePronounce?.id, translateImage?.id)
					)
				}

				val phraseID = cardModel.phrase.await()?.globalId?.let { provider.phrase.getByGlobalId(it) }?.id.ifNull { throw Exception("Error") }
				val translateID =
					cardModel.translate.await()?.globalId?.let { provider.phrase.getByGlobalId(it) }?.id.ifNull { throw Exception("Error") }
				val cardImage = cardModel.image.await()?.globalId?.let {
					provider.images.getByGlobalId(it).ifNull { provider.images.download(it) }
				}

				provider.cards.getByGlobalId(cardModel.card.globalId)?.let {
					provider.cards.update(it.update(cardModel.card, phraseID, translateID, cardImage?.id))
				}.ifNull {
					provider.cards.add(com.uogames.dto.local.Card().update(cardModel.card, phraseID, translateID, cardImage?.id))
				}
			}.onSuccess {
				launch(Dispatchers.Main) {
					downloadAction[cardModel.card.globalId]?.callback?.let { back -> back("Ok") }
					downloadAction.remove(cardModel.card.globalId)
				}
			}.onFailure {
				launch(Dispatchers.Main) {
					downloadAction[cardModel.card.globalId]?.callback?.let { back -> back(it.message ?: "Error") }
					downloadAction.remove(cardModel.card.globalId)
				}
			}
		}
		downloadAction[cardModel.card.globalId] = DownloadAction(job, loading)
	}


	fun getPicasso(context: Context) = provider.images.getPicasso(context)
}