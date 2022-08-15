package com.uogames.remembercards.ui.cardFragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uogames.dto.local.Card
import com.uogames.dto.local.Phrase
import com.uogames.remembercards.utils.ifNull
import com.uogames.remembercards.utils.safely
import com.uogames.repository.DataProvider
import com.uogames.repository.DataProvider.Companion.toImage
import com.uogames.repository.DataProvider.Companion.toPronounce
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.util.*

class CardViewModel(val provider: DataProvider) : ViewModel() {


	private val _size = MutableStateFlow(0)
	val size = _size.asStateFlow()

	val like = MutableStateFlow("")

	@ExperimentalCoroutinesApi
	private val likeSize = like.flatMapLatest { provider.cards.getCountFlow(it) }

	inner class CardModel(val card: Card) {
		val phrase by lazy { viewModelScope.async { provider.phrase.getById(card.idPhrase) } }
		val translate by lazy { viewModelScope.async { provider.phrase.getById(card.idTranslate) } }
		val image by lazy { viewModelScope.async(Dispatchers.IO) { card.toImage() } }
		val phrasePronounce by lazy { viewModelScope.async(Dispatchers.IO) { phrase.await()?.toPronounce() } }
		val phraseImage by lazy { viewModelScope.async(Dispatchers.IO) { phrase.await()?.toImage() } }
		val translatePronounce by lazy { viewModelScope.async(Dispatchers.IO) { translate.await()?.toPronounce() } }
		val translateImage by lazy { viewModelScope.async(Dispatchers.IO) { translate.await()?.toImage() } }
	}

	init {
		viewModelScope.launch(Dispatchers.IO) { likeSize.collect { _size.value = it } }
	}

	fun reset() {
		like.value = ""
	}

	fun get(number: Int) = provider.cards.getCardFlow(like.value, number).map { it?.let { CardModel(it) } }

	fun getDisplayLang(phrase: Phrase): String {
		return safely {
			val data = phrase.lang.split("-")
			Locale(data[0]).displayLanguage
		}.ifNull { "" }
	}

}