package com.uogames.remembercards.ui.mainNav

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class NavigationViewModel @Inject constructor() : ViewModel() {

    private val ioScope = CoroutineScope(Dispatchers.IO)
    private var job: Job? = null

    private val _id = MutableStateFlow(-1)
    val id = _id.asStateFlow()

    private val _isShowKey = MutableStateFlow(false)
    val isShowKey = _isShowKey.asStateFlow()

    fun setId(id: Int): Boolean = _id.compareAndSet(_id.value, id)



    fun setShowKeyboard(boolean: Boolean){
        job?.cancel()
        job = ioScope.launch{
            delay(10)
            _isShowKey.value = boolean
        }
    }

}