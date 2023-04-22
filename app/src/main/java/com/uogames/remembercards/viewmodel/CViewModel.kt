package com.uogames.remembercards.viewmodel

import android.content.Context
import android.graphics.drawable.AnimationDrawable
import androidx.lifecycle.ViewModel
import com.uogames.dto.global.GlobalCardView
import com.uogames.dto.local.LocalCard
import com.uogames.dto.local.LocalCardView
import com.uogames.remembercards.utils.MediaBytesSource
import com.uogames.remembercards.utils.ObservableMediaPlayer
import com.uogames.remembercards.utils.ifNull
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.UUID

class CViewModel(
    val globalViewModel: GlobalViewModel,
    private val player: ObservableMediaPlayer
) : ViewModel() {

    private val provider = globalViewModel.provider
    private val viewModelScope = CoroutineScope(Dispatchers.IO)

    inner class LocalCardModel(val card: LocalCardView) {
        private val pData by lazy { viewModelScope.async { card.phrase.pronounce?.let { provider.pronounce.load(it) } } }
        private val tData by lazy { viewModelScope.async { card.translate.pronounce?.let { provider.pronounce.load(it) } } }

        suspend fun playPhrase(anim: AnimationDrawable) = player.play(MediaBytesSource(pData.await()), anim)
        suspend fun playTranslate(anim: AnimationDrawable) = player.play(MediaBytesSource(tData.await()), anim)
    }

    inner class GlobalCardModel(val card: GlobalCardView) {
        private val pData by lazy { viewModelScope.async { card.phrase.pronounce?.let { provider.pronounce.downloadData(it.globalId) } } }
        private val tData by lazy { viewModelScope.async { card.translate.pronounce?.let { provider.pronounce.downloadData(it.globalId) } } }

        suspend fun playPhrase(anim: AnimationDrawable) = player.play(MediaBytesSource(pData.await()), anim)
        suspend fun playTranslate(anim: AnimationDrawable) = player.play(MediaBytesSource(tData.await()), anim)
    }

    private class ShareAction(val job: Job, var callback: (String) -> Unit)
    private class DownloadAction(val job: Job, var callback: (String, LocalCard?) -> Unit)

    private val shareActions = HashMap<Int, ShareAction>()
    private val downloadAction = HashMap<UUID, DownloadAction>()

    suspend fun getLocalModelView(
        like: String? = null,
        langFirst: String? = null,
        langSecond: String? = null,
        countryFirst: String? = null,
        countrySecond: String? = null,
        newest: Boolean = false,
        position: Int? = null
    ) = provider.cards.getView(
        like = like,
        langFirst = langFirst,
        langSecond = langSecond,
        countryFirst = countryFirst,
        countrySecond = countrySecond,
        newest = newest,
        position = position
    )?.let { LocalCardModel(it) }

    suspend fun getGlobalModelView(
        text: String? = null,
        langFirst: String? = null,
        langSecond: String? = null,
        countryFirst: String? = null,
        countrySecond: String? = null,
        number: Long
    ) = provider.cards.getGlobalView(
        text = text,
        langFirst = langFirst,
        langSecond = langSecond,
        countryFirst = countryFirst,
        countrySecond = countrySecond,
        number = number
    ).let { GlobalCardModel(it) }

    fun share(card: LocalCardView, result: (String) -> Unit) {
        val job = viewModelScope.launch {
            runCatching {
                provider.cards.share(card.id)
            }.onSuccess {
                launch(Dispatchers.Main) {
                    shareActions[card.id]?.callback?.let { back -> back("Ok") }
                    shareActions.remove(card.id)
                }
            }.onFailure {
                launch(Dispatchers.Main) {
                    shareActions[card.id]?.callback?.let { back -> back(it.message ?: "Error") }
                    shareActions.remove(card.id)
                }
            }
        }
        shareActions[card.id] = ShareAction(job, result)
    }

    fun setShareAction(card: LocalCardView, loading: (String) -> Unit): Boolean {
        shareActions[card.id]?.callback = loading
        return shareActions[card.id]?.job?.isActive.ifNull { false }
    }

    fun stopSharing(card: LocalCardView) {
        val action = shareActions[card.id].ifNull { return }
        action.job.cancel()
        action.callback("Cancel")
        shareActions.remove(card.id)
    }

    fun setDownloadAction(id: UUID, loading: (String, LocalCard?) -> Unit): Boolean {
        downloadAction[id]?.callback = loading
        return downloadAction[id]?.job?.isActive.ifNull { false }
    }

    fun stopDownloading(id: UUID) {
        val action = downloadAction[id].ifNull { return }
        action.job.cancel()
        action.callback("Cancel", null)
        downloadAction.remove(id)
    }

    fun save(card: GlobalCardView, loading: (String, LocalCard?) -> Unit) {
        val job = viewModelScope.launch {
            runCatching {
                provider.cards.save(card)
            }.onSuccess { lc ->
                launch(Dispatchers.Main) {
                    downloadAction[card.globalId]?.callback?.let { back -> back("Ok", lc) }
                    downloadAction.remove(card.globalId)
                }
            }.onFailure {
                launch(Dispatchers.Main) {
                    downloadAction[card.globalId]?.callback?.let { back -> back(it.message ?: "Error", null) }
                    downloadAction.remove(card.globalId)
                }
            }
        }
        downloadAction[card.globalId] = DownloadAction(job, loading)
    }

    fun showShareNotice(b: Boolean) = globalViewModel.showShareNotice(b)

    fun getPicasso(context: Context) = globalViewModel.getPicasso(context)

}