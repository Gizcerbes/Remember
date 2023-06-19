package com.uogames.remembercards.viewmodel

import android.content.Context
import android.graphics.drawable.AnimationDrawable
import androidx.lifecycle.ViewModel
import com.uogames.dto.global.GlobalPhraseView
import com.uogames.dto.local.LocalPhrase
import com.uogames.dto.local.LocalPhraseView
import com.uogames.remembercards.utils.MediaBytesSource
import com.uogames.remembercards.utils.ObservableMediaPlayer
import com.uogames.remembercards.utils.ifNull
import kotlinx.coroutines.*
import java.util.*

class PViewModel(
    val globalViewModel: GlobalViewModel,
    private val player: ObservableMediaPlayer
) : ViewModel() {

    private val provider = globalViewModel.provider
    private val viewModelScope = CoroutineScope(Dispatchers.IO)

    inner class LocalPhraseModel(val phrase: LocalPhraseView) {
        private val pData by lazy { phrase.pronounce?.let { provider.pronounce.load(it) } }
        suspend fun play(anim: AnimationDrawable) = player.play(MediaBytesSource(pData), anim)
    }

    inner class GlobalPhraseModel(val phraseView: GlobalPhraseView) {
        private val pData by lazy { viewModelScope.async { phraseView.pronounce?.let { provider.pronounce.downloadData(it.globalId) } } }
        suspend fun play(anim: AnimationDrawable) = player.play(MediaBytesSource(pData.await()), anim)
    }

    private class ShareAction(val job: Job, var callback: (String) -> Unit)
    private class DownloadAction(val job: Job, var callback: (String, LocalPhrase?) -> Unit)

    private val shareActions = HashMap<Int, ShareAction>()
    private val downloadActions = HashMap<UUID, DownloadAction>()

    fun share(phrase: LocalPhraseView, loading: (String) -> Unit) {
        val job = viewModelScope.launch {
            runCatching {
                //provider.phrase.share(phrase.id)
                provider.phrase.addToShare(phrase)
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

    fun setShareAction(phrase: LocalPhraseView, loading: (String) -> Unit): Boolean {
        shareActions[phrase.id]?.callback = loading
        return shareActions[phrase.id]?.job?.isActive.ifNull { false }
    }

    fun stopSharing(phrase: LocalPhraseView) {
        val action = shareActions[phrase.id].ifNull { return }
        action.job.cancel()
        action.callback("Cancel")
        shareActions.remove(phrase.id)
    }

    fun isChanged(phrase: LocalPhraseView) = provider.phrase.isChanged(phrase.id)

    fun getShareAction(phrase: LocalPhraseView) = provider.share.existsFlow(idPhrase = phrase.id)

    fun setDownloadAction(id: UUID, loading: (String, LocalPhrase?) -> Unit): Boolean {
        downloadActions[id]?.callback = loading
        return downloadActions[id]?.job?.isActive.ifNull { false }
    }

    fun stopDownloading(id: UUID) {
        val action = downloadActions[id].ifNull { return }
        action.job.cancel()
        action.callback("Cancel", null)
        downloadActions.remove(id)
    }

    fun save(phraseView: GlobalPhraseView, loading: (String, LocalPhrase?) -> Unit) {
        val job = viewModelScope.launch {
            runCatching {
                provider.phrase.save(phraseView)
            }.onSuccess { pv ->
                launch(Dispatchers.Main) {
                    downloadActions[phraseView.globalId]?.callback?.let { back -> back("Ok", pv) }
                    downloadActions.remove(phraseView.globalId)
                }
            }.onFailure {
                launch(Dispatchers.Main) {
                    downloadActions[phraseView.globalId]?.callback?.let { back -> back(it.message ?: "Error", null) }
                    downloadActions.remove(phraseView.globalId)
                }
            }
        }
        downloadActions[phraseView.globalId] = DownloadAction(job, loading)
    }

    suspend fun getLocalModel(
        like: String?,
        lang: String?,
        country: String?,
        newest: Boolean = false,
        position: Int?
    ) = provider.phrase.getView(like, lang, country, newest, position)?.let { LocalPhraseModel(it) }

    suspend fun getGlobalModel(
        like: String?,
        lang: String?,
        country: String?,
        position: Int
    ) = provider.phrase.getGlobalView(like, lang, country, position.toLong()).let { GlobalPhraseModel(it) }

    fun showShareNotice(b: Boolean) = globalViewModel.showShareNotice(b)

    fun getPicasso(context: Context) = globalViewModel.getPicasso(context)

}