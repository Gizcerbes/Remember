package com.uogames.remembercards.ui.libraryFragment

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uogames.dto.local.Module
import com.uogames.remembercards.utils.ifNull
import com.uogames.repository.DataProvider
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.util.concurrent.LinkedBlockingQueue
import javax.inject.Inject
import kotlin.collections.HashMap

class LibraryViewModel @Inject constructor(val provider: DataProvider) : ViewModel() {

	private val viewModelScope = CoroutineScope(Dispatchers.IO)

	val like = MutableStateFlow("")

	val size = like.flatMapLatest { provider.module.getCountLike(it) }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0)

	fun reset() {
		like.value = ""
	}

	fun createModule(name: String, call: (Int) -> Unit) = viewModelScope.launch {
		val res = provider.module.add(Module(name = name))
		launch(Dispatchers.Main) { call(res.toInt()) }
	}

	suspend fun getCountByModule(id: Module) = provider.moduleCard.getCountByModule(id)

	suspend fun getModuleByPosition(position: Int) = provider.module.getByPosition(like.value, position)

	private class ShareAction(val job: Job, var callback: (String) -> Unit)

	private val shareActions = HashMap<Int, ShareAction>()

	fun share(module: Module, loading: (String) -> Unit) {
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

	fun setShareAction(module: Module, loading: (String) -> Unit): Boolean {
		shareActions[module.id]?.callback = loading
		return shareActions[module.id]?.job?.isActive.ifNull { false }
	}

	fun stopSharing(module: Module, message: String = "Cancel") {
		val action = shareActions[module.id].ifNull { return }
		action.job.cancel()
		action.callback(message)
		shareActions.remove(module.id)
	}
}
