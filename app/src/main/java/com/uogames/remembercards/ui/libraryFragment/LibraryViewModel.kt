package com.uogames.remembercards.ui.libraryFragment

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uogames.dto.local.Module
import com.uogames.remembercards.utils.ifNull
import com.uogames.remembercards.utils.observeWhile
import com.uogames.repository.DataProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.concurrent.LinkedBlockingDeque
import javax.inject.Inject
import kotlin.collections.HashMap

class LibraryViewModel @Inject constructor(val provider: DataProvider) : ViewModel() {

	val like = MutableStateFlow("")

	private val _size = like.flatMapLatest { provider.module.getCountLike(it) }

	val size = MutableStateFlow(0)

	init {
		_size.observeWhile(viewModelScope, Dispatchers.IO) { size.value = it }
	}

	fun reset() {
		like.value = ""
	}

	fun createModule(name: String, call: (Int) -> Unit) = viewModelScope.launch {
		val res = provider.module.add(Module(name = name))
		call(res.toInt())
	}

	fun getCountByModuleIdFlow(id: Int) = provider.moduleCard.getCountByModuleIdFlow(id)

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
				val jobBuffer = LinkedBlockingDeque<Job>(16)
				var controlBl = true
				val controlJob = launch {
					val first = viewModelScope.launch(Dispatchers.IO) { while (controlBl) jobBuffer.takeLast().join() }
					val second = viewModelScope.launch(Dispatchers.IO) { while (controlBl) jobBuffer.takeFirst().join() }
					first.join()
					second.join()
				}
				for (i in 0 until size) {
					val mc = provider.moduleCard.getByPositionOfModule(module.id, i).ifNull { return@launch }
					jobBuffer.put(launch { provider.moduleCard.share(mc.id) })
					Log.e("TAG", "share: $i", )
				}
				controlBl = false
				if (jobBuffer.size > 0) controlJob.join()
				else controlJob.cancel()
				jobBuffer.forEach { it.join() }
			}.onSuccess {
				launch(Dispatchers.Main) { shareActions[module.id]?.callback?.let { back -> back("Ok") } }
			}.onFailure {
				launch(Dispatchers.Main) { shareActions[module.id]?.callback?.let { back -> back(it.message ?: "Error") } }
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