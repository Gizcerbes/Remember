package com.uogames.remembercards.ui.bookFragment

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uogames.dto.global.Image
import com.uogames.dto.global.Phrase
import com.uogames.remembercards.utils.MediaBytesSource
import com.uogames.remembercards.utils.observeWhile
import com.uogames.repository.DataProvider
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.*
import javax.inject.Inject

class NetworkBookViewModel @Inject constructor(
	private val provider: DataProvider
) : ViewModel() {

	inner class PhraseModel(val phrase: Phrase) {
		val image by lazy { viewModelScope.async(Dispatchers.IO) { phrase.idImage?.let { getImageById(it) } } }
		val pronounceData by lazy { viewModelScope.async(Dispatchers.IO) { phrase.idPronounce?.let { getPronounceData(it) } } }
		val lang by lazy { getDisplayLang() }

		private fun getDisplayLang(): String {
			return phrase.lang.runCatching {
				val data = split("-")
				Locale(data[0]).displayLanguage
			}.getOrDefault("")
		}

	}

	val like = MutableStateFlow("")

	private val _size = MutableStateFlow(0L)
	val size = _size.asStateFlow()

	private var searchJob: Job? = null

	init {
		like.observeWhile(viewModelScope, Dispatchers.IO) {
			searchJob?.cancel()
			searchJob = viewModelScope.launch(Dispatchers.IO) {
				delay(1000)
				runCatching {
					_size.value = provider.phrase.countGlobal(it)
				}.onFailure {
					_size.value = 0
				}
			}
		}
	}

	suspend fun getByPosition(position: Long): PhraseModel? {
		runCatching { return PhraseModel(provider.phrase.getGlobal(like.value, position)) }
		return null
	}

	private suspend fun getImageById(id: Long): Image? {
		runCatching { return provider.images.getByGlobalId(id) }
		return null
	}

	private suspend fun getPronounceData(id: Long): ByteArray? {
		runCatching { return provider.pronounce.downloadData(id) }
		return null
	}

	fun getPicasso(context: Context) = provider.images.getPicasso(context)

}