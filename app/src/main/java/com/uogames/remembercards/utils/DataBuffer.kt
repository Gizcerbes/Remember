package com.uogames.remembercards.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DataBuffer<T>(
    private val maxSize: Int = 15,
    private val default: T,
    private val updater: suspend (position: Int) -> T
) {

    private val ioScope = CoroutineScope(Dispatchers.IO)

	private val map = HashMap<Int, Data<T>>()

    private data class Data<T>(
        private val default: T,
        val updater: suspend (position: Int) -> T
    ) {
		val item: MutableStateFlow<T> = MutableStateFlow(default)
		val isReady = MutableStateFlow(false)
        var numberRequest = 0L

        suspend fun update(request: Long, position: Int) {
            if (position >= 0) {
				isReady.value = false
                val newValue = updater(position)
                if (numberRequest == request) item.value = newValue
				isReady.value = true
            } else {
                if (numberRequest == request) item.value = default
            }
        }
    }

    @Synchronized
    fun getDataFlow(position: Int): StateFlow<T> {
        val cell = position % maxSize
        if (map[cell] == null) map[cell] = Data(default, updater)
        return map[cell]?.item?.asStateFlow()!!
    }

	@Synchronized
	fun isDataReadyFlow(position: Int): StateFlow<Boolean>{
		val cell = position % maxSize
		if (map[cell] == null) map[cell] = Data(default, updater)
		return map[cell]?.isReady?.asStateFlow()!!
	}

    @Synchronized
    fun update(position: Int) {
        var data = map[position % maxSize]
        if (data == null) {
            data = Data(default, updater)
            map[position % maxSize] = data
        }
        val request = ++data.numberRequest
        ioScope.launch { data.update(request, position) }
    }

    fun updateAll() {
        map.forEach { (t, u) -> ioScope.launch { u.updater(t) } }
    }

	fun clear(){
		map.clear()
	}

}