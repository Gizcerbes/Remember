package com.uogames.remembercards.ui.libraryFragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uogames.dto.global.Module
import com.uogames.remembercards.utils.observeWhile
import com.uogames.repository.DataProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class NetworkLibraryViewModel @Inject constructor(private val provider: DataProvider) : ViewModel() {

    private val _size = MutableStateFlow(0L)
    val size = _size.asStateFlow()

    val like = MutableStateFlow("")

    init {
        like.observeWhile(viewModelScope, Dispatchers.IO) {
            runCatching {
                _size.value = provider.module.countGlobal(like.value)
            }.onFailure {
                _size.value = 0
            }
        }
    }

    suspend fun getByPosition(position: Long): Module? {
        runCatching {
            return provider.module.getGlobal(like.value, position)
        }
        return null
    }

    suspend fun getModuleCardCount(module: Module): Long {
        runCatching {
            return provider.moduleCard.getGlobalCount(module.globalId)
        }
        return 0
    }
}
