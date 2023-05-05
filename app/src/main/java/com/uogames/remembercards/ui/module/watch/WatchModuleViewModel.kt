package com.uogames.remembercards.ui.module.watch

import android.content.Context
import com.uogames.dto.global.GlobalModuleView
import com.uogames.dto.local.LocalModuleView
import com.uogames.remembercards.utils.ifNull
import com.uogames.remembercards.viewmodel.MViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

class WatchModuleViewModel @Inject constructor(
    private val model: MViewModel,
) {

    private val viewModelScope = CoroutineScope(Dispatchers.IO)
    val shouldReset = model.globalViewModel.shouldReset

    val type = MutableStateFlow(false)
    val localID = MutableStateFlow<Int?>(null)
    val globalID = MutableStateFlow<UUID?>(null)

    private val _localModule = MutableStateFlow<MViewModel.LocalModuleModel?>(null)
    val localModule = _localModule.asStateFlow()

    private val _globalModule = MutableStateFlow<MViewModel.GlobalModuleModel?>(null)
    val globalModule = _globalModule.asStateFlow()

    private val _size = MutableStateFlow(0)
    val size = _size.asStateFlow()

    val adapter = WatchModuleAdapter(this)

    fun reset() {
        type.value = false
        localID.value = null
        globalID.value = null
        _localModule.value = null
        _globalModule.value = null
    }

    fun update() = viewModelScope.launch {
        runCatching {
            _size.value = 0
            localID.value?.apply {
                _localModule.value = model.getLocalModel(this)
                if (!type.value) _size.value = model.getCountByModule(this)
            }
            globalID.value?.apply {
                _globalModule.value = model.getGlobalModel(this)
                if (type.value) _size.value = model.getGlobalCount(this).toInt()
            }
        }.onFailure {
            _size.value = 0
        }
    }

    fun getLocalAsync(position: Int) = viewModelScope.async {
        val localID = localID.value.ifNull { return@async null }
        model.getLocalModuleCardModel(localID, position)
    }

    fun getGlobalAsync(position: Int) = viewModelScope.async {
        val globalID = globalID.value.ifNull { return@async null }
        model.getGlobalModuleCardModel(globalID, position)
    }

    fun download(view: GlobalModuleView, loading: (String) -> Unit) = model.download(view, loading)

    fun setDownloadAction(view: GlobalModuleView, loading: (String) -> Unit) = model.setDownloadAction(view.globalId, loading)

    fun getShareAction(module: LocalModuleView) = model.getShareAction(module).stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    fun stopDownloading(view: GlobalModuleView) = model.stopDownloading(view.globalId)

    fun getPicasso(context: Context) = model.globalViewModel.getPicasso(context)

}