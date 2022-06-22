package com.uogames.remembercards.ui.cardFragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uogames.dto.Card
import com.uogames.repository.DataProvider
import kotlinx.coroutines.flow.*

class CardViewModel(val provider: DataProvider): ViewModel() {

	private val _size = MutableStateFlow(0)
	val size = _size.asStateFlow()


	val like = MutableStateFlow("")
	private val likeSize = like.flatMapLatest { provider.cards.getCountFlow(it) }

	init {
		likeSize.onEach { _size.value = it }.launchIn(viewModelScope)
	}

	fun get(number: Int): Flow<Card?> = provider.cards.getCardFlow(like.value, number)


}