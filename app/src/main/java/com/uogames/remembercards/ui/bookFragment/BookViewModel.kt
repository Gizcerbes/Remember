package com.uogames.remembercards.ui.bookFragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uogames.dto.local.Phrase
import com.uogames.remembercards.utils.safely
import com.uogames.repository.DataProvider
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.util.*
import javax.inject.Inject

class BookViewModel @Inject constructor(
	private val provider: DataProvider,
) : ViewModel() {

	val like = MutableStateFlow("")


	private val _size = MutableStateFlow(0)
	val size = _size.asStateFlow()

	@ExperimentalCoroutinesApi
	private val likeSize = like.flatMapLatest { provider.phrase.countFlow(it) }

	inner class BookModel(val phrase: Phrase) {
		val pronounce by lazy { viewModelScope.async(Dispatchers.IO) { phrase.idPronounce?.let { provider.pronounce.getById(it) } } }
		val image by lazy { viewModelScope.async(Dispatchers.IO) { phrase.idImage?.let { provider.images.getById(it) } } }
		val lang by lazy { getDisplayLang() }

		private fun getDisplayLang(): String {
			return safely {
				val data = phrase.lang.split("-")
				Locale(data[0]).displayLanguage
			}.orEmpty()
		}
	}

	init {
		viewModelScope.launch(Dispatchers.IO) { likeSize.collect { _size.value = it } }
	}

	fun reset() {
		like.value = ""
	}

	fun get(position: Int) = provider.phrase.getFlow(like.value, position).map { it?.let { BookModel(it) } }

	fun share(id: Int, loading: (String) -> Unit) {
		viewModelScope.launch(Dispatchers.IO) {
			runCatching {
				provider.phrase.share(id)
			}.onSuccess {
				launch(Dispatchers.Main) { loading("Ok") }
			}.onFailure {
				launch(Dispatchers.Main) { loading(it.message?: "Error") }
			}
		}
	}

}