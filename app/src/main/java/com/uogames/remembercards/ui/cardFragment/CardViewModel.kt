package com.uogames.remembercards.ui.cardFragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uogames.dto.local.Card
import com.uogames.dto.local.Phrase
import com.uogames.remembercards.utils.flatMapLatest
import com.uogames.remembercards.utils.ifNull
import com.uogames.repository.DataProvider
import com.uogames.repository.DataProvider.Companion.toImage
import com.uogames.repository.DataProvider.Companion.toPronounce
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.util.*
import kotlin.collections.HashMap

class CardViewModel(val provider: DataProvider) : ViewModel() {

	inner class CardModel(val card: Card) {
		val phrase by lazy { viewModelScope.async(Dispatchers.IO) { provider.phrase.getById(card.idPhrase) } }
		val translate by lazy { viewModelScope.async(Dispatchers.IO) { provider.phrase.getById(card.idTranslate) } }
		val image by lazy { viewModelScope.async(Dispatchers.IO) { card.toImage() } }
		val phrasePronounce by lazy { viewModelScope.async(Dispatchers.IO) { phrase.await()?.toPronounce() } }
		val phraseImage by lazy { viewModelScope.async(Dispatchers.IO) { phrase.await()?.toImage() } }
		val translatePronounce by lazy { viewModelScope.async(Dispatchers.IO) { translate.await()?.toPronounce() } }
		val translateImage by lazy { viewModelScope.async(Dispatchers.IO) { translate.await()?.toImage() } }
	}

	private class ShareAction(val job: Job, var callback: (String) -> Unit)

	val like = MutableStateFlow("")

	val size = like.flatMapLatest(viewModelScope, Dispatchers.IO, 0) { provider.cards.getCountFlow(it) }

	private val shareActions = HashMap<Int, ShareAction>()

	fun reset() {
		like.value = ""
	}

	fun get(number: Int) = provider.cards.getCardFlow(like.value, number).map { it?.let { CardModel(it) } }

	suspend fun get2(number: Int) = provider.cards.getCard(like.value, number)?.let { CardModel(it) }

	fun share(card: Card, result: (String) -> Unit) {
		val job = viewModelScope.launch(Dispatchers.IO) {
			runCatching {
				provider.cards.share(card.id)
			}.onSuccess {
				launch(Dispatchers.Main) { shareActions[card.id]?.callback?.let { back -> back("Ok") } }
			}.onFailure {
				launch(Dispatchers.Main) { shareActions[card.id]?.callback?.let { back -> back(it.message ?: "Error") } }
			}
			shareActions.remove(card.id)
		}
		shareActions[card.id] = ShareAction(job, result)
	}

	fun setShareAction(card: Card, loading: (String) -> Unit): Boolean {
		shareActions[card.id]?.callback = loading
		return shareActions[card.id]?.job?.isActive.ifNull { false }
	}

	fun stopSharing(card: Card) {
		val action = shareActions[card.id].ifNull { return }
		action.job.cancel()
		action.callback("Cancel")
		shareActions.remove(card.id)
	}
}
