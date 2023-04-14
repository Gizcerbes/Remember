package com.uogames.remembercards.viewmodel

import android.content.Context
import android.graphics.drawable.AnimationDrawable
import androidx.lifecycle.ViewModel
import com.uogames.dto.global.GlobalPhraseView
import com.uogames.dto.local.LocalPhraseView
import com.uogames.remembercards.ui.phrase.phrasesFragment.PhraseViewModel
import com.uogames.remembercards.utils.MediaBytesSource
import com.uogames.remembercards.utils.ObservableMediaPlayer
import com.uogames.remembercards.utils.ifNull
import kotlinx.coroutines.*
import java.util.*

class PViewModel(
    private val globalViewModel: GlobalViewModel,
    private val player: ObservableMediaPlayer
): ViewModel() {

    private val provider = globalViewModel.provider
    private val viewModelScope = CoroutineScope(Dispatchers.IO)

    inner class LocalBookModel(val phrase: LocalPhraseView) {
        private val audioData by lazy { phrase.pronounce?.let { provider.pronounce.load(it) } }
        suspend fun play(anim: AnimationDrawable) = player.play(MediaBytesSource(audioData), anim)
    }

    inner class GlobalPhraseModel(val phraseView: GlobalPhraseView) {
        private val audioData by lazy { viewModelScope.async { phraseView.pronounce?.let { getPronounceData(it.globalId) } } }
        suspend fun play(anim: AnimationDrawable) = player.play(MediaBytesSource(audioData.await()), anim)
    }

    private suspend fun getPronounceData(id: UUID): ByteArray? {
        runCatching { return provider.pronounce.downloadData(id) }
        return null
    }

    private class ShareAction(val job: Job, var callback: (String) -> Unit)
    private class DownloadAction(val job: Job, var callback: (String) -> Unit)

    private val shareActions = HashMap<Int, ShareAction>()
    private val downloadActions = HashMap<UUID, DownloadAction>()

    fun share(phrase: LocalPhraseView, loading: (String) -> Unit) {
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

    fun setDownloadAction(id: UUID, loading: (String) -> Unit): Boolean {
        downloadActions[id]?.callback = loading
        return downloadActions[id]?.job?.isActive.ifNull { false }
    }

    fun stopDownloading(id: UUID) {
        val action = downloadActions[id].ifNull { return }
        action.job.cancel()
        action.callback("Cancel")
        downloadActions.remove(id)
    }

    fun save(phraseModel: PhraseViewModel.GlobalPhraseModel, loading: (String) -> Unit) {
        val job = viewModelScope.launch {
            runCatching {
                provider.phrase.save(phraseModel.phraseView)
            }.onSuccess {
                launch(Dispatchers.Main) {
                    downloadActions[phraseModel.phraseView.globalId]?.callback?.let { back -> back("Ok") }
                    downloadActions.remove(phraseModel.phraseView.globalId)
                }
            }.onFailure {
                launch(Dispatchers.Main) {
                    downloadActions[phraseModel.phraseView.globalId]?.callback?.let { back -> back(it.message ?: "Error") }
                    downloadActions.remove(phraseModel.phraseView.globalId)
                }
            }
        }
        downloadActions[phraseModel.phraseView.globalId] = DownloadAction(job, loading)
    }

    fun showShareNotice(b: Boolean) = globalViewModel.showShareNotice(b)

    fun getPicasso(context: Context) = globalViewModel.getPicasso(context)

}