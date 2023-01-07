package com.uogames.remembercards.ui.libraryFragment

import com.uogames.dto.global.Module
import com.uogames.dto.local.LocalModule
import com.uogames.remembercards.utils.ifNull
import com.uogames.remembercards.utils.observe
import com.uogames.repository.DataProvider
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.*
import java.util.concurrent.LinkedBlockingQueue
import javax.inject.Inject
import kotlin.collections.HashMap

class LibraryViewModel @Inject constructor(
    private val provider: DataProvider
) {

    private val viewModelScope = CoroutineScope(Dispatchers.IO)

    private class ShareAction(val job: Job, var callback: (String) -> Unit)
    private class DownloadAction(val job: Job, var callback: (String) -> Unit)
    private val shareActions = HashMap<Int, ShareAction>()
    private val downloadAction = HashMap<UUID, DownloadAction>()

    private val _size = MutableStateFlow(0)
    val size = _size.asStateFlow()

    val like = MutableStateFlow<String?>(null)
    val search = MutableStateFlow(false)
    val cloud = MutableStateFlow(false)

    private var searchJob: Job? = null

    init {
        like.observe(viewModelScope){ updateSize() }
        search.observe(viewModelScope) { updateSize() }
        cloud.observe(viewModelScope) {
            _size.value = 0
            updateSize()
        }
    }

    private fun updateSize(){
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(50)
            _size.value = if (cloud.value){
                provider.module.countGlobal(like.value.orEmpty()).toInt()
            } else {
                provider.module.count(like.value)
            }
        }
    }

    fun reset() {
        like.value = null
        search.value = false
        cloud.value = false
    }

    fun update(){
        updateSize()
    }

    fun createModule(name: String, call: (Int) -> Unit) = viewModelScope.launch {
        val res = provider.module.add(LocalModule(name = name))
        launch(Dispatchers.Main) { call(res.toInt()) }
    }

    suspend fun get(position: Int) = provider.module.get(like.value, position)

    suspend fun getCountByModule(id: LocalModule) = provider.moduleCard.getCountByModule(id)

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

    suspend fun getByPosition(position: Long): Module? {
        runCatching { return provider.module.getGlobal(like.value.orEmpty(), position) }
        return null
    }

    suspend fun getModuleCardCount(module: Module): Long {
        runCatching { return provider.moduleCard.getGlobalCount(module.globalId) }
        return 0
    }

    fun download(id: UUID, loading: (String) -> Unit) {
        val job = viewModelScope.launch {
            runCatching {
                val module = provider.module.download(id).ifNull {
                    launch(Dispatchers.Main) {
                        downloadAction[id]?.callback?.let { back -> back("Error") }
                        downloadAction.remove(id)
                    }
                    return@launch
                }
                val count = provider.moduleCard.countGlobal(id)
                val downloadBuffer = LinkedBlockingQueue<Job>(16)
                for (i in 0 until count) {
                    val job = viewModelScope.launch { provider.moduleCard.download(module, i) }
                    viewModelScope.launch {
                        job.join()
                        downloadBuffer.remove(job)
                    }
                    downloadBuffer.put(job)
                }
                downloadBuffer.forEach { it.join() }
            }.onSuccess {
                launch(Dispatchers.Main) {
                    downloadAction[id]?.callback?.let { back -> back("Ok") }
                    downloadAction.remove(id)
                }
            }.onFailure {
                launch(Dispatchers.Main) {
                    downloadAction[id]?.callback?.let { back -> back(it.message ?: "Error") }
                    downloadAction.remove(id)
                }
            }
        }
        downloadAction[id] = DownloadAction(job, loading)
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

}