package com.uogames.remembercards.ui.bookFragment

import android.os.Parcelable
import androidx.lifecycle.ViewModel
import com.uogames.dto.local.LocalPhrase
import com.uogames.remembercards.utils.ifNull
import com.uogames.repository.DataProvider
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.util.Locale
import javax.inject.Inject
import kotlin.collections.HashMap

class BookViewModel @Inject constructor(
	private val provider: DataProvider
) : ViewModel() {

	private val viewModelScope = CoroutineScope(Dispatchers.IO)

	inner class BookModel(val phrase: LocalPhrase) {
		val pronounce by lazy { viewModelScope.async { phrase.idPronounce?.let { provider.pronounce.getById(it) } } }
		val image by lazy { viewModelScope.async { phrase.idImage?.let { provider.images.getById(it) } } }
		val lang: String by lazy { Locale.forLanguageTag(phrase.lang).displayLanguage }
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

	fun share(phrase: LocalPhrase, loading: (String) -> Unit) {
		val job = viewModelScope.launch {
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

	fun setShareAction(phrase: LocalPhrase, loading: (String) -> Unit): Boolean {
		shareActions[phrase.id]?.callback = loading
		return shareActions[phrase.id]?.job?.isActive.ifNull { false }
	}

	fun stopSharing(phrase: LocalPhrase) {
		val action = shareActions[phrase.id].ifNull { return }
		action.job.cancel()
		action.callback("Cancel")
		shareActions.remove(phrase.id)
	}

	override fun onCleared() {
		super.onCleared()
		viewModelScope.cancel()
	}

}
