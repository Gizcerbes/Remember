package com.uogames.remembercards.ui.cardFragment

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uogames.dto.Card
import com.uogames.dto.Phrase
import com.uogames.remembercards.utils.observeWhile
import com.uogames.repository.DataProvider
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class CardViewModel(val provider: DataProvider): ViewModel() {

	private val _size = MutableStateFlow(0)
	val size = _size.asStateFlow()


	val like = MutableStateFlow("")

	private val likeSize = like.flatMapLatest { provider.cards.getCountFlow(it) }

	init {
		likeSize.onEach { _size.value = it }.launchIn(viewModelScope)
	}

	fun get(number: Int): Flow<Card?> = provider.cards.getCardFlow(like.value, number)

	suspend fun getImage(card: Card) = provider.images.getByCard(card).first()

	suspend fun getImage(phrase: Phrase) = provider.images.getByPhrase(phrase).first()

	suspend fun getPhrase(id: Int) = provider.phrase.getByIdFlow(id).first()

	suspend fun getPronounce(phrase: Phrase) = provider.pronounce.getByPhrase(phrase).first()

}