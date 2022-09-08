package com.uogames.remembercards.ui.bookFragment

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uogames.dto.global.Image
import com.uogames.dto.global.Phrase
import com.uogames.remembercards.utils.Lang
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

class NetworkBookViewModel @Inject constructor(
	private val provider: DataProvider
) : ViewModel() {

	inner class PhraseModel(val phrase: Phrase) {
		val image by lazy { viewModelScope.async(Dispatchers.IO) { phrase.idImage?.let { getImageById(it) } } }
		val pronounceData by lazy { viewModelScope.async(Dispatchers.IO) { phrase.idPronounce?.let { getPronounceData(it) } } }
		val lang by lazy { Lang.parse(phrase.lang).locale.displayLanguage }
	}

	private class DownloadAction(val job: Job, var callback: (String) -> Unit)

	val like = MutableStateFlow("")

	private val _size = MutableStateFlow(0L)
	val size = _size.asStateFlow()

	private val downloadAction = HashMap<UUID, DownloadAction>()

	private var searchJob: Job? = null

	init {
		like.observeWhile(viewModelScope, Dispatchers.IO) {
			searchJob?.cancel()
			searchJob = viewModelScope.launch(Dispatchers.IO) {
				_size.value = 0
				it.ifNullOrEmpty { return@launch }
				delay(300)
				runCatching {
					_size.value = provider.phrase.countGlobal(it)
				}.onFailure {
					_size.value = 0
				}
			}
		}
	}

	suspend fun getByGlobalId(uuid: UUID) = viewModelScope.async(Dispatchers.IO) { provider.phrase.getByGlobalId(uuid)}.await()

	suspend fun getByPosition(position: Long): PhraseModel? {
		runCatching { return PhraseModel(provider.phrase.getGlobal(like.value, position)) }
		return null
	}

	private suspend fun getImageById(id: UUID): Image? {
		runCatching { return provider.images.getByGlobalId(id) }
		return null
	}

	private suspend fun getPronounceData(id: UUID): ByteArray? {
		runCatching { return provider.pronounce.downloadData(id) }
		return null
	}

	fun download(id: UUID, loading: (String) -> Unit) {
		val job = viewModelScope.launch(Dispatchers.IO) {
			runCatching {
				provider.phrase.download(id)
			}.onSuccess {
				launch(Dispatchers.Main) {
					downloadAction[id]?.callback?.let { back -> back("Ok") }
					downloadAction.remove(id)
				}
			}.onFailure {
				launch(Dispatchers.Main) {
					downloadAction[id]?.callback?.let { back -> back(it.message ?: "Error") }
					downloadAction.remove(id)
				}
			}
		}
		downloadAction[id] = DownloadAction(job, loading)
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

	fun getPicasso(context: Context) = provider.images.getPicasso(context)


}
