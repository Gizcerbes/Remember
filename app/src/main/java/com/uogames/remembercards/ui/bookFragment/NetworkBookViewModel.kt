package com.uogames.remembercards.ui.bookFragment

import android.content.Context
import androidx.lifecycle.ViewModel
import com.uogames.dto.global.GlobalImage
import com.uogames.dto.global.GlobalPhrase
import com.uogames.map.PhraseMap.update
import com.uogames.remembercards.utils.ifNull
import com.uogames.remembercards.utils.observeWhile
import com.uogames.repository.DataProvider
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.*
import javax.inject.Inject
import kotlin.collections.HashMap

class NetworkBookViewModel @Inject constructor(
	private val provider: DataProvider
) : ViewModel() {

	private val viewModelScope = CoroutineScope(Dispatchers.IO)

	inner class PhraseModel(val phrase: GlobalPhrase) {
		val image by lazy { viewModelScope.async { phrase.idImage?.let { getImageById(it) } } }
		val pronounceData by lazy { viewModelScope.async { phrase.idPronounce?.let { getPronounceData(it) } } }
		val lang by lazy { Locale.forLanguageTag(phrase.lang).displayLanguage }
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
					_size.value = provider.phrase.countGlobal(it)
				}.onFailure {
					_size.value = 0
				}
			}
		}
	}

	suspend fun getByGlobalId(uuid: UUID) = viewModelScope.async { provider.phrase.getByGlobalId(uuid) }.await()

	suspend fun getByPosition(position: Long): PhraseModel? {
		runCatching { return PhraseModel(provider.phrase.getGlobal(like.value, position)) }
		return null
	}

	private suspend fun getImageById(id: UUID): GlobalImage? {
		runCatching { return provider.images.getGlobalById(id) }
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

	fun save(phraseModel: PhraseModel, loading: (String) -> Unit) {
		val job = viewModelScope.launch{
			runCatching {
				val image = phraseModel.phrase.idImage?.let {
					provider.images.getByGlobalId(it).ifNull { provider.images.download(it) }
				}
				val pronounce = phraseModel.phrase.idPronounce?.let {
					provider.pronounce.getByGlobalId(it).ifNull { provider.pronounce.download(it) }
				}
				provider.phrase.getByGlobalId(phraseModel.phrase.globalId)?.let {
					provider.phrase.update(it.update(phraseModel.phrase, pronounce?.id, image?.id))
				}.ifNull {
					provider.phrase.add(com.uogames.dto.local.LocalPhrase().update(phraseModel.phrase, pronounce?.id, image?.id))
				}
			}.onSuccess {
				launch(Dispatchers.Main) {
					downloadAction[phraseModel.phrase.globalId]?.callback?.let { back -> back("Ok") }
					downloadAction.remove(phraseModel.phrase.globalId)
				}
			}.onFailure {
				launch(Dispatchers.Main) {
					downloadAction[phraseModel.phrase.globalId]?.callback?.let { back -> back(it.message ?: "Error") }
					downloadAction.remove(phraseModel.phrase.globalId)
				}
			}
		}
		downloadAction[phraseModel.phrase.globalId] = DownloadAction(job, loading)
	}

	fun getPicasso(context: Context) = provider.images.getPicasso(context)


}
