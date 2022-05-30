package com.uogames.remembercards.ui.bookFragment

import androidx.lifecycle.ViewModel
import com.uogames.dto.Card
import com.uogames.repository.DataProvider
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class BookViewModel @Inject constructor(
	private val provider: DataProvider
) : ViewModel() {
	private val ioScope = CoroutineScope(Dispatchers.IO)


	private val _like = MutableStateFlow("")
	val like = _like.asStateFlow()

	private val _size = MutableStateFlow(0)
	val size = _size.asStateFlow()

	init {
		_like.flatMapLatest { provider.cards.getCardCountFlow(it) }.onEach {
			_size.value = it
		}.launchIn(ioScope)
	}

	fun reset() {
		_like.value = ""
	}

	fun get(position: Int) = provider.cards.getCardFlow(position, like.value)

	fun delete(card: Card, result: (Boolean) -> Unit = {}) = ioScope.launch {
		val res = provider.cards.deleteCardAsync(card).await()
		launch(Dispatchers.Main) { result(res) }
	}

	fun updateCard(card: Card) = provider.cards.updateCard(card)

	fun add(card: Card, result: (Boolean) -> Unit = {}) = ioScope.launch {
		val res = provider.cards.addCardAsync(card).await()
		launch(Dispatchers.Main) { result(res) }
	}

	fun setLike(string: String) {
		_like.value = string
	}
}