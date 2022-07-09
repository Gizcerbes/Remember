package com.uogames.remembercards.ui.bookFragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uogames.dto.Phrase
import com.uogames.remembercards.utils.ifNull
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

	private val _listID: MutableStateFlow<List<Int>> = MutableStateFlow(listOf())

	@ExperimentalCoroutinesApi
	private val likeSize = like.flatMapLatest { provider.phrase.countFlow(it) }.apply {
		onEach { _size.value = it }.launchIn(viewModelScope)
	}

	@ExperimentalCoroutinesApi
	private val likeListId = like.flatMapLatest { provider.phrase.getListId(it) }.apply {
		onEach { _listID.value = it }.launchIn(viewModelScope)
	}

	inner class BookModel(val phrase: Phrase) {
		val pronounce by lazy { provider.pronounce.getByIdAsync { phrase.idPronounce } }
		val image by lazy { provider.images.getByIdAsync { phrase.idImage } }
		val lang by lazy { getDisplayLang() }

		private fun getDisplayLang(): String {
			return safely{
				val data = phrase.lang.split("-")
				Locale(data[0]).displayLanguage
			}.ifNull { "" }
		}
	}

	fun reset() {
		like.value = ""
	}

	fun get(position: Int) = provider.phrase.getFlow(like.value, position).map { it?.let { BookModel(it) } }

}