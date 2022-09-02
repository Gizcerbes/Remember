package com.uogames.remembercards.ui.bookFragment

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uogames.dto.local.Phrase
import com.uogames.remembercards.utils.ifNull
import com.uogames.repository.DataProvider
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.util.*
import javax.inject.Inject
import kotlin.collections.HashMap
import kotlin.collections.HashSet

class BookViewModel @Inject constructor(
    private val provider: DataProvider
) : ViewModel() {

    val like = MutableStateFlow("")

    private val _size = MutableStateFlow(0)
    val size = _size.asStateFlow()

    @ExperimentalCoroutinesApi
    private val likeSize = like.flatMapLatest { provider.phrase.countFlow(it) }

    inner class BookModel(val phrase: Phrase) {
        val pronounce by lazy { viewModelScope.async(Dispatchers.IO) { phrase.idPronounce?.let { provider.pronounce.getById(it) } } }
        val image by lazy { viewModelScope.async(Dispatchers.IO) { phrase.idImage?.let { provider.images.getById(it) } } }
        val lang by lazy { getDisplayLang() }

        private fun getDisplayLang(): String {
            return phrase.lang.runCatching {
                val data = split("-")
                Locale(data[0]).displayLanguage
            }.getOrDefault("")
        }
    }

    private class ShareAction(val job: Job, var callback: (String) -> Unit)

    private val shareActions = HashMap<Int, ShareAction>()

    init {
        viewModelScope.launch(Dispatchers.IO) { likeSize.collect { _size.value = it } }
    }

    fun reset() {
        like.value = ""
    }

    suspend fun getBookModel(position: Int) = provider.phrase.get(like.value, position)?.let { BookModel(it) }

    fun share(phrase: Phrase, loading: (String) -> Unit) {
        val job = viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                provider.phrase.share(phrase.id)
            }.onSuccess {
                launch(Dispatchers.Main) { shareActions[phrase.id]?.callback?.let { back -> back("Ok") } }
            }.onFailure {
                launch(Dispatchers.Main) { shareActions[phrase.id]?.callback?.let { back -> back(it.message ?: "Error") } }
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
