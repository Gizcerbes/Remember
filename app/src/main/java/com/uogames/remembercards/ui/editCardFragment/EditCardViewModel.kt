package com.uogames.remembercards.ui.editCardFragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uogames.dto.Card
import com.uogames.dto.Phrase
import com.uogames.remembercards.utils.ifNull
import com.uogames.remembercards.utils.ifNullOrEmpty
import com.uogames.repository.DataProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

class EditCardViewModel @Inject constructor(val provider: DataProvider) : ViewModel() {

	private val _cardID = MutableStateFlow(0)

	private val _firstPhrase: MutableStateFlow<Phrase?> = MutableStateFlow(null)
	val firstPhrase = _firstPhrase.asStateFlow()

	private val _secondPhrase: MutableStateFlow<Phrase?> = MutableStateFlow(null)
	val secondPhrase = _secondPhrase.asStateFlow()

	private val _reason = MutableStateFlow("")
	val reason = _reason.asStateFlow()

	val listImageFlow = provider.images.getListFlow()

	fun reset() {
		_firstPhrase.value = null
		_secondPhrase.value = null
		_reason.value = ""
		_cardID.value = 0
	}

	fun load(id: Int) = viewModelScope.launch {
		reset()
		val card = provider.cards.getByIdFlow(id).first()
		card?.let {
			_cardID.value = card.id
			_firstPhrase.value = provider.phrase.getByIdFlow(card.idPhrase).first()
			_secondPhrase.value = provider.phrase.getByIdFlow(card.idTranslate).first()
			_reason.value = card.reason
		}
	}

	fun selectFirstPhrase(id: Int) = viewModelScope.launch {
		provider.phrase.getByIdFlow(id).first().let {
			_firstPhrase.value = it
		}
	}

	fun selectSecondPhrase(id: Int) = viewModelScope.launch {
		provider.phrase.getByIdFlow(id).first().let {
			_secondPhrase.value = it
		}
	}

	fun setReason(reason: String) {
		_reason.value = reason
	}


	fun save(call: (Boolean) -> Unit) = viewModelScope.launch {
		val card = build().ifNull { return@launch call(false) }
		val res = provider.cards.addAsync(card).await()
		call(res > 0)
	}


	fun update(call: (Boolean) -> Unit) = viewModelScope.launch {
		val card = build().ifNull { return@launch call(false) }
		val res = provider.cards.updateAsync(card).await()
		call(res)
	}

	fun delete(call: (Boolean) -> Unit) = viewModelScope.launch {
		val res = provider.cards.deleteAsync(Card(_cardID.value, 0, 0, null, "")).await()
		call(res)
	}

	private fun build(): Card? {
		val id = _cardID.value
		val firstID = _firstPhrase.value?.id.ifNull { return null }
		val secondID = _secondPhrase.value?.id.ifNull { return null }
		val reason = _reason.value.ifNullOrEmpty { return null }
		return Card(id, firstID, secondID, null, reason)
	}


}