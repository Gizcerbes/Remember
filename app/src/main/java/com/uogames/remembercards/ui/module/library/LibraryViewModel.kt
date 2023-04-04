package com.uogames.remembercards.ui.module.library

import com.uogames.dto.User
import com.uogames.dto.global.GlobalModule
import com.uogames.dto.global.GlobalModuleView
import com.uogames.dto.local.LocalModule
import com.uogames.remembercards.viewmodel.GlobalViewModel
import com.uogames.remembercards.utils.UserGlobalName
import com.uogames.remembercards.utils.ifNull
import com.uogames.remembercards.utils.observe
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.*
import java.util.concurrent.LinkedBlockingQueue
import javax.inject.Inject
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class LibraryViewModel @Inject constructor(
    private val globalViewModel: GlobalViewModel
) {

    private val provider = globalViewModel.provider
    private val viewModelScope = CoroutineScope(Dispatchers.IO)

    inner class LocalModuleModel(val module: LocalModule) {
        val count = viewModelScope.async { getCountByModule(module) }
        val owner = viewModelScope.async { module.globalOwner?.let { getUserName(it) } ?: UserGlobalName(module.owner) }
    }

    inner class GlobalModuleModel(val module: GlobalModuleView) {
        val count = viewModelScope.async { getModuleCardCount(module) }
        val owner = viewModelScope.async { getGlobalUsername(module.user.globalOwner) ?: UserGlobalName("") }
    }

    private class ShareAction(val job: Job, var callback: (String) -> Unit)
    private class DownloadAction(val job: Job, var callback: (String) -> Unit)

    private val shareActions = HashMap<Int, ShareAction>()
    private val downloadAction = HashMap<UUID, DownloadAction>()

    val shareNotice get() = globalViewModel.shareNotice

    private val _size = MutableStateFlow(0)
    val size = _size.asStateFlow()

    val like = MutableStateFlow<String?>(null)
    val search = MutableStateFlow(false)
    val cloud = MutableStateFlow(false)

    val adapter = LibraryAdapter(
        model = this,
        reportCall = { gm -> reportCall.forEach { it(gm) } },
        selectCall = { module -> selectCall.forEach { it(module) } }
    )
    private val selectCall = ArrayList<(LocalModule) -> Unit>()
    private val reportCall = ArrayList<(GlobalModule) -> Unit>()

    private var searchJob: Job? = null

    init {
        like.observe(viewModelScope) { updateSize() }
        search.observe(viewModelScope) { updateSize() }
        cloud.observe(viewModelScope) {
            _size.value = 0
            updateSize()
        }
    }

    private fun updateSize() {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            runCatching {
                delay(100)
                _size.value = if (cloud.value) {
                    provider.module.countGlobal(like.value).toInt()
                } else {
                    provider.module.count(like.value)
                }
            }
        }
    }

    fun reset() {
        like.value = null
        search.value = false
        cloud.value = false
        selectCall.clear()
        reportCall.clear()
    }

    fun update() {
        updateSize()
    }

    fun addSelectCall(call: (LocalModule) -> Unit) = selectCall.add(call)

    fun removeSelectCall(call: (LocalModule) -> Unit) = selectCall.remove(call)

    fun addReportCall(call: (GlobalModule) -> Unit) = reportCall.add(call)

    fun removeReportCall(call: (GlobalModule) -> Unit) = reportCall.remove(call)

    fun createModule(name: String, call: (Int) -> Unit) = viewModelScope.launch {
        val res = provider.module.add(LocalModule(name = name))
        launch(Dispatchers.Main) { call(res.toInt()) }
    }

    suspend fun get(position: Int) = provider.module.get(like.value, position)?.let { LocalModuleModel(it) }

    suspend fun getCountByModule(id: LocalModule) = provider.moduleCard.getCountByModule(id)

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


    fun share(module: LocalModule, loading: (String) -> Unit) {
        val job = viewModelScope.launch {
            runCatching {
                provider.module.share(module.id)
                val size = provider.moduleCard.getCountByModule(module)
                val shareBuffer = LinkedBlockingQueue<Job>(16)
                for (i in 0 until size) {
                    val mc = provider.moduleCard.getByPositionOfModule(module.id, i).ifNull { return@launch }
                    val job = launch {
                        runCatching {
                            provider.moduleCard.share(mc.id)
                        }.onFailure {
                            stopSharing(module, it.message ?: "Error")
                        }
                    }
                    launch {
                        job.join()
                        shareBuffer.remove(job)
                    }
                    shareBuffer.put(job)
                }
                shareBuffer.forEach { it.join() }
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

    fun setShareAction(module: LocalModule, loading: (String) -> Unit): Boolean {
        shareActions[module.id]?.callback = loading
        return shareActions[module.id]?.job?.isActive.ifNull { false }
    }

    fun stopSharing(module: LocalModule, message: String = "Cancel") {
        val action = shareActions[module.id].ifNull { return }
        action.job.cancel()
        viewModelScope.launch(Dispatchers.Main) { action.callback(message) }
        shareActions.remove(module.id)
    }

    suspend fun getByPosition(position: Int): GlobalModuleModel? {
        runCatching {
            return GlobalModuleModel(
                provider.module.getGlobalView(
                    text = like.value.orEmpty(),
                    number = position.toLong()
                )
            )
        }
        return null
    }

    suspend fun getModuleCardCount(module: GlobalModuleView): Long {
        runCatching { return provider.moduleCard.getGlobalCount(module.globalId) }
        return 0
    }

    fun download(view: GlobalModuleView, loading: (String) -> Unit) {
        val job = viewModelScope.launch {
            runCatching {
                provider.module.save(view)
            }.onSuccess {
                launch(Dispatchers.Main) {
                    downloadAction[view.globalId]?.callback?.let { back -> back("Ok") }
                    downloadAction.remove(view.globalId)
                }
            }.onFailure {
                launch(Dispatchers.Main) {
                    downloadAction[view.globalId]?.callback?.let { back -> back(it.message ?: "Error") }
                    downloadAction.remove(view.globalId)
                }
            }
        }
        downloadAction[view.globalId] = DownloadAction(job, loading)
    }

    fun setDownloadAction(id: UUID, loading: (String) -> Unit): Boolean {
        downloadAction[id]?.callback = loading
        return downloadAction[id]?.job?.isActive.ifNull { false }
    }

    fun stopDownloading(id: UUID) {
        val action = downloadAction[id].ifNull { return }
        action.job.cancel()
        action.callback("Cancel")
        downloadAction.remove(id)
    }

    fun showShareNotice(b: Boolean) = globalViewModel.showShareNotice(b)

}