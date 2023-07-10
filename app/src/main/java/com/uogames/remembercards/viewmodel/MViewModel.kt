package com.uogames.remembercards.viewmodel

import android.graphics.drawable.AnimationDrawable
import com.uogames.dto.User
import com.uogames.dto.global.GlobalModuleCardView
import com.uogames.dto.global.GlobalModuleView
import com.uogames.dto.local.LocalDownload
import com.uogames.dto.local.LocalModule
import com.uogames.dto.local.LocalModuleCardView
import com.uogames.dto.local.LocalModuleView
import com.uogames.remembercards.utils.MediaBytesSource
import com.uogames.remembercards.utils.ObservableMediaPlayer
import com.uogames.remembercards.utils.UserGlobalName
import com.uogames.remembercards.utils.ifNull
import kotlinx.coroutines.*
import java.util.*
import javax.inject.Inject
import kotlin.collections.HashMap

class MViewModel @Inject constructor(
	val globalViewModel: GlobalViewModel,
	val player: ObservableMediaPlayer
) {

	private val provider = globalViewModel.provider
	private val viewModelScope = CoroutineScope(Dispatchers.IO)

	inner class LocalModuleModel(val module: LocalModuleView) {
		val count by lazy { viewModelScope.async { getCountByModule(module.id) } }
		val owner by lazy { viewModelScope.async { module.globalOwner?.let { getUserName(it) } ?: UserGlobalName(module.owner) } }
	}

	inner class GlobalModuleModel(val module: GlobalModuleView) {
		val count by lazy { viewModelScope.async { getModuleCardCount(module) } }
		val owner by lazy { viewModelScope.async { getGlobalUsername(module.user.globalOwner) ?: UserGlobalName("") } }
	}

	inner class LocalModuleCardModel(val mc: LocalModuleCardView) {
		private val phraseAudioData by lazy { mc.card.phrase.pronounce?.let { provider.pronounce.load(it) } }
		private val translateAudioData by lazy { mc.card.translate.pronounce?.let { provider.pronounce.load(it) } }

		suspend fun playFirst(anim: AnimationDrawable) = player.play(MediaBytesSource(phraseAudioData), anim)
		suspend fun playSecond(anim: AnimationDrawable) = player.play(MediaBytesSource(translateAudioData), anim)
	}

	inner class GlobalModuleCardModel(val mc: GlobalModuleCardView) {
		private val phraseAudioData by lazy { viewModelScope.async { mc.card.phrase.pronounce?.let { provider.pronounce.downloadData(it.globalId) } } }
		private val translateAudioData by lazy { viewModelScope.async { mc.card.translate.pronounce?.let { provider.pronounce.downloadData(it.globalId) } } }

		suspend fun playFirst(anim: AnimationDrawable) = player.play(MediaBytesSource(phraseAudioData.await()), anim)
		suspend fun playSecond(anim: AnimationDrawable) = player.play(MediaBytesSource(translateAudioData.await()), anim)
	}

	private class ShareAction(val job: Job, var callback: (String) -> Unit)
	private class DownloadAction(val job: Job, var callback: (String) -> Unit)

	private val shareActions = HashMap<Int, ShareAction>()
	private val downloadAction = HashMap<UUID, DownloadAction>()


	fun createModule(name: String, call: (Int) -> Unit) = viewModelScope.launch {
		val res = provider.module.add(LocalModule(name = name))
		launch(Dispatchers.Main) { call(res.toInt()) }
	}

	suspend fun getCountByModule(id: Int) = provider.moduleCard.getCountByModuleId(id)

	suspend fun getUserName(uid: String): UserGlobalName? {
		val name = provider.user.getByUid(uid)
		return if (name != null) {
			UserGlobalName(name.name, uid)
		} else {
			getGlobalUsername(uid)
		}
	}

	suspend fun getGlobalUsername(uid: String): UserGlobalName? {
		return try {
			val n = provider.user.getGlobalByUid(uid)
			provider.user.insert(User(n.globalOwner, n.name))
			UserGlobalName(n.name, uid)
		} catch (e: Exception) {
			null
		}
	}

	suspend fun getModuleCardCount(module: GlobalModuleView): Long {
		runCatching { return provider.moduleCard.getGlobalCount(module.globalId) }
		return 0
	}

	suspend fun getGlobalCount(id: UUID): Long {
		runCatching { return provider.moduleCard.getGlobalCount(id) }
		return 0
	}

	suspend fun getLocalSize(
		text: String? = null,
		fLang: String? = null,
		sLang: String? = null,
		fCountry: String? = null,
		sCountry: String? = null
	): Int = provider.module.count(text, fLang, sLang, fCountry, sCountry)

	suspend fun getGlobalSize(
		text: String? = null,
		langFirst: String? = null,
		langSecond: String? = null,
		countryFirst: String? = null,
		countrySecond: String? = null
	): Int = provider.module.countGlobal(
		text = text,
		langFirst = langFirst,
		langSecond = langSecond,
		countryFirst = countryFirst,
		countrySecond = countrySecond
	).toInt()

	suspend fun getLocalModuleCardSize(
		id: Int
	): Int = provider.moduleCard.getCountByModuleId(id)

	suspend fun getGlobalModuleCardSize(
		id: UUID
	): Int = provider.moduleCard.getGlobalCount(id).toInt()

	suspend fun getLocalModel(
		text: String? = null,
		langFirst: String? = null,
		langSecond: String? = null,
		countryFirst: String? = null,
		countrySecond: String? = null,
		newest: Boolean = false,
		position: Int? = null
	) = provider.module.getView(
		text = text,
		fLang = langFirst,
		sLang = langSecond,
		fCountry = countryFirst,
		sCountry = countrySecond,
		newest = newest,
		position = position
	)?.let { LocalModuleModel(it) }

	suspend fun getLocalModel(
		id: Int
	) = provider.module.getViewById(id)?.let { LocalModuleModel(it) }

	suspend fun getGlobalModel(
		text: String? = null,
		langFirst: String? = null,
		langSecond: String? = null,
		countryFirst: String? = null,
		countrySecond: String? = null,
		position: Int
	) = try {
		provider.module.getGlobalView(
			text = text.orEmpty(),
			langFirst = langFirst,
			langSecond = langSecond,
			countryFirst = countryFirst,
			countrySecond = countrySecond,
			number = position.toLong()
		).let { GlobalModuleModel(it) }
	} catch (e: Exception) {
		null
	}

	suspend fun getGlobalModel(id: UUID) = try {
		provider.module.getGlobalView(id).let { GlobalModuleModel(it) }
	} catch (e: Exception) {
		null
	}

	suspend fun getLocalModuleCardModel(
		idModule: Int,
		position: Int
	) = provider.moduleCard.getView(idModule, position)?.let { LocalModuleCardModel(it) }

	suspend fun getGlobalModuleCardModel(
		idModule: UUID,
		position: Int
	) = try {
		provider.moduleCard.getGlobalView(idModule, position.toLong()).let { GlobalModuleCardModel(it) }
	} catch (e: Exception) {
		null
	}

	fun share(module: LocalModuleView, loading: (String) -> Unit) {
		val job = viewModelScope.launch {
			runCatching {
				provider.module.addToShare(module)
			}.onSuccess {
				launch(Dispatchers.Main) {
					shareActions[module.id]?.callback?.let { back -> back("Ok") }
					shareActions.remove(module.id)
				}
			}.onFailure {
				launch(Dispatchers.Main) {
					shareActions[module.id]?.callback?.let { back -> back(it.message ?: "Error") }
					shareActions.remove(module.id)
				}
			}
		}
		shareActions[module.id] = ShareAction(job, loading)
	}

	fun setShareAction(module: LocalModuleView, loading: (String) -> Unit): Boolean {
		shareActions[module.id]?.callback = loading
		return shareActions[module.id]?.job?.isActive.ifNull { false }
	}

	fun stopSharing(module: LocalModuleView, message: String = "Cancel") {
		val action = shareActions[module.id].ifNull { return }
		action.job.cancel()
		viewModelScope.launch(Dispatchers.Main) { action.callback(message) }
		shareActions.remove(module.id)
	}

	fun isChanged(module: LocalModuleView) = provider.module.isChanged(module.id)

	fun getShareAction(module: LocalModuleView) = provider.share.existsFlow(idModule = module.id)

	fun download(view: GlobalModuleView) {
		viewModelScope.launch {
			provider.download.insert(LocalDownload(globalModuleId = view.globalId))
		}
//        val job = viewModelScope.launch {
//            runCatching {
//                provider.module.save(view)
//            }.onSuccess {
//                launch(Dispatchers.Main) {
//                    downloadAction[view.globalId]?.callback?.let { back -> back("Ok") }
//                    downloadAction.remove(view.globalId)
//                }
//            }.onFailure {
//                launch(Dispatchers.Main) {
//                    downloadAction[view.globalId]?.callback?.let { back -> back(it.message ?: "Error") }
//                    downloadAction.remove(view.globalId)
//                }
//            }
//        }
//        downloadAction[view.globalId] = DownloadAction(job, loading)
	}

	fun setDownloadAction(id: UUID, loading: (String) -> Unit): Boolean {
		downloadAction[id]?.callback = loading
		return downloadAction[id]?.job?.isActive.ifNull { false }
	}

	suspend fun getDownloadFlow(uuid: UUID) = provider.download.existsFlow(moduleId = uuid)

	fun stopDownloading(id: UUID) {
		viewModelScope.launch { provider.download.clean() }
//		val action = downloadAction[id].ifNull { return }
//		action.job.cancel()
//		action.callback("Cancel")
//		downloadAction.remove(id)
	}
}