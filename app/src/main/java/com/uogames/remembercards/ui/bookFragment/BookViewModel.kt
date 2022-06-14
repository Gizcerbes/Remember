package com.uogames.remembercards.ui.bookFragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uogames.dto.Card
import com.uogames.dto.Phrase
import com.uogames.repository.DataProvider
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class BookViewModel @Inject constructor(
	private val provider: DataProvider
) : ViewModel() {

	val like = MutableStateFlow("")


	private val _size = MutableStateFlow(0)
	val size = _size.asStateFlow()


	private val likeSize = like.flatMapLatest { provider.phrase.countFlow(it) }
	init {
		likeSize.onEach { _size.value = it }.launchIn(viewModelScope)
	}

	fun reset() {
		like.value = ""
	}

	fun get(position: Int) = provider.phrase.getFlow(like.value, position)

	fun delete(phrase: Phrase, result: (Boolean) -> Unit = {}) = provider.phrase.delete(phrase, result)

	fun updateCard(phrase: Phrase, result: (Boolean) -> Unit = {}) = provider.phrase.update(phrase, result)




}