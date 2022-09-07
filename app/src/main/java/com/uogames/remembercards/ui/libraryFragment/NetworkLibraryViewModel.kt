package com.uogames.remembercards.ui.libraryFragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uogames.dto.global.Module
import com.uogames.remembercards.utils.ifNull
import com.uogames.remembercards.utils.ifNullOrEmpty
import com.uogames.remembercards.utils.observeWhile
import com.uogames.repository.DataProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import kotlin.collections.HashMap

class NetworkLibraryViewModel @Inject constructor(private val provider: DataProvider) : ViewModel() {

	private class DownloadAction(val job: Job, var callback: (String) -> Unit)

	private val _size = MutableStateFlow(0L)
	val size = _size.asStateFlow()

	val like = MutableStateFlow("")

	private val downloadAction = HashMap<UUID, DownloadAction>()

	private var searchJob: Job? = null

	init {
		like.observeWhile(viewModelScope, Dispatchers.IO) {
			searchJob?.cancel()
			searchJob = viewModelScope.launch(Dispatchers.IO) {
				_size.value = 0
				it.ifNullOrEmpty { return@launch }
				delay(300)
				runCatching {
					_size.value = provider.module.countGlobal(like.value)
				}.onFailure {
					_size.value = 0
				}
			}
		}
	}

	suspend fun getByPosition(position: Long): Module? {
		runCatching { return provider.module.getGlobal(like.value, position) }
		return null
	}

	suspend fun getModuleCardCount(module: Module): Long {
		runCatching { return provider.moduleCard.getGlobalCount(module.globalId) }
		return 0
	}

	fun download(id: UUID, loading: (String) -> Unit) {
		val job = viewModelScope.launch(Dispatchers.IO) {
			runCatching {
				val module = provider.module.download(id)
				val count = provider.moduleCard.countGlobal(id)
				for (i in 0 until count) {
					provider.moduleCard.download(module!!, i)
				}
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
