package com.uogames.remembercards.ui.libraryFragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uogames.dto.local.Module
import com.uogames.remembercards.utils.ifNull
import com.uogames.repository.DataProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.concurrent.LinkedBlockingQueue
import javax.inject.Inject
import kotlin.collections.HashMap

class LibraryViewModel @Inject constructor(val provider: DataProvider) : ViewModel() {

	val like = MutableStateFlow("")

	val size = like.flatMapLatest { provider.module.getCountLike(it) }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0)



	fun reset() {
		like.value = ""
	}

	fun createModule(name: String, call: (Int) -> Unit) = viewModelScope.launch {
		val res = provider.module.add(Module(name = name))
		call(res.toInt())
	}

	suspend fun getCountByModuleIdFlow(id: Int) = provider.moduleCard.getCountByModuleIdFlow(id).stateIn(viewModelScope)

	suspend fun getCountByModule(id: Module) = provider.moduleCard.getCountByModule(id)

	suspend fun getCardByModule(module: Module, position: Int) = provider.moduleCard.getByPositionOfModule(module.id, position)

	suspend fun getModuleByPosition(position: Int) = provider.module.getByPosition(like.value, position)

	private class ShareAction(val job: Job, var callback: (String) -> Unit)

	private val shareActions = HashMap<Int, ShareAction>()

	fun share(module: Module, loading: (String) -> Unit) {
		val job = viewModelScope.launch(Dispatchers.IO) {
			runCatching {
				provider.module.share(module.id)
				val size = provider.moduleCard.getCountByModule(module)
				val shareBuffer = LinkedBlockingQueue<Job>(16)
				for (i in 0 until size) {
					val mc = provider.moduleCard.getByPositionOfModule(module.id, i).ifNull { return@launch }
					val job = viewModelScope.launch { runCatching { provider.moduleCard.share(mc.id) } }
					shareBuffer.put(job)
					viewModelScope.launch(Dispatchers.IO) {
						job.join()
						shareBuffer.remove(job)
					}
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

	fun stopSharing(module: Module) {
		val action = shareActions[module.id].ifNull { return }
		action.job.cancel()
		action.callback("Cancel")
		shareActions.remove(module.id)
	}
}
