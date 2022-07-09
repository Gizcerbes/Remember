package com.uogames.remembercards.ui.cardFragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uogames.dto.Card
import com.uogames.dto.Phrase
import com.uogames.remembercards.utils.ifNull
import com.uogames.remembercards.utils.safely
import com.uogames.repository.DataProvider
import com.uogames.repository.DataProvider.Companion.phraseToImageDeferred
import com.uogames.repository.DataProvider.Companion.toPronounceDeferred
import kotlinx.coroutines.flow.*
import java.util.*

class CardViewModel(val provider: DataProvider) : ViewModel() {

	private val _size = MutableStateFlow(0)
	val size = _size.asStateFlow()

	val like = MutableStateFlow("")

	private val likeSize = like.flatMapLatest { provider.cards.getCountFlow(it) }

	inner class CardModel(val card: Card) {
		val phrase by lazy { provider.phrase.getByIdAsync { card.idPhrase } }
		val translate by lazy { provider.phrase.getByIdAsync { card.idTranslate } }
		val image by lazy { provider.images.getByIdAsync { card.idImage } }
		val phrasePronounce by lazy { phrase.toPronounceDeferred() }
		val phraseImage by lazy { phrase.phraseToImageDeferred() }
		val translatePronounce by lazy { translate.toPronounceDeferred() }
		val translateImage by lazy { translate.phraseToImageDeferred() }
	}

	init {
		likeSize.onEach { _size.value = it }.launchIn(viewModelScope)
	}

	fun get(number: Int) = provider.cards.getCardFlow(like.value, number).map { it?.let { CardModel(it) } }

	suspend fun getImage(phrase: Phrase) = provider.images.getByPhrase(phrase).first()

	suspend fun getPhrase(id: Int) = provider.phrase.getByIdFlow(id).first()

	suspend fun getPronounce(phrase: Phrase) = provider.pronounce.getByPhrase(phrase).first()

	fun getDisplayLang(phrase: Phrase): String {
		return safely{
			val data = phrase.lang.split("-")
			Locale(data[0]).displayLanguage
		}.ifNull { "" }
	}

}