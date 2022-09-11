package com.uogames.remembercards.ui.bookFragment

import android.os.Parcelable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uogames.dto.local.Phrase
import com.uogames.remembercards.utils.Lang
import com.uogames.remembercards.utils.ifNull
import com.uogames.repository.DataProvider
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import kotlin.collections.HashMap

class BookViewModel @Inject constructor(
	private val provider: DataProvider
) : ViewModel() {

	inner class BookModel(val phrase: Phrase) {
		val pronounce by lazy { viewModelScope.async(Dispatchers.IO) { phrase.idPronounce?.let { provider.pronounce.getById(it) } } }
		val image by lazy { viewModelScope.async(Dispatchers.IO) { phrase.idImage?.let { provider.images.getById(it) } } }
		val lang: String by lazy { Lang.parse(phrase.lang).locale.displayLanguage }
	}

	private class ShareAction(val job: Job, var callback: (String) -> Unit)

	val like = MutableStateFlow("")

	val size = like.flatMapLatest { provider.phrase.countFlow(it) }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0)

	private val shareActions = HashMap<Int, ShareAction>()

	var recyclerStat: Parcelable? = null

	fun reset() {
		like.value = ""
		recyclerStat = null
	}

	suspend fun getBookModel(position: Int) = provider.phrase.get(like.value, position)?.let { BookModel(it) }

	fun share(phrase: Phrase, loading: (String) -> Unit) {
		val job = viewModelScope.launch(Dispatchers.IO) {
			runCatching {
				provider.phrase.share(phrase.id)
			}.onSuccess {
				launch(Dispatchers.Main) {
					shareActions[phrase.id]?.callback?.let { back -> back("Ok") }
					shareActions.remove(phrase.id)
				}
			}.onFailure {
				launch(Dispatchers.Main) {
					shareActions[phrase.id]?.callback?.let { back -> back(it.message ?: "Error") }
					shareActions.remove(phrase.id)
				}
			}
		}
		shareActions[phrase.id] = ShareAction(job, loading)
	}

	fun setShareAction(phrase: Phrase, loading: (String) -> Unit): Boolean {
		shareActions[phrase.id]?.callback = loading
		return shareActions[phrase.id]?.job?.isActive.ifNull { false }
	}

	fun stopSharing(phrase: Phrase) {
		val action = shareActions[phrase.id].ifNull { return }
		action.job.cancel()
		action.callback("Cancel")
		shareActions.remove(phrase.id)
	}

}
