package com.uogames.remembercards.pagination

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class Pagination<T>(
	private val workScope: CoroutineScope,
	private val loadCount: Int,
	private val askCount: suspend () -> Int,
	private val askPage: suspend (count: Int, offset: Int) -> List<T>
) : IPagination<T> {
	private val _isLoading = MutableStateFlow(false)
	private val loading = _isLoading.asStateFlow()
	private val _count = MutableStateFlow(0)
	private val count = _count.asStateFlow()
	private val loaded = MutableStateFlow(0)

	private val map = HashMap<Int, T>()

	override suspend fun reload() {
		workScope.launch {
			_isLoading.value = true
			loaded.value = 0
			_count.value = askCount()
			map.clear()
			delay(100)
			val p = askPage(loadCount, 0).let {
				var id = 1
				HashMap<Int, T>().apply {
					it.forEach { put(id++, it) }
				}
			}
			map.putAll(p)
			loaded.value = map.size
			_isLoading.value = false
		}
	}

	private fun load(offset: Int) {
		workScope.launch {
			_isLoading.value = true
			_count.value = askCount()
			askPage(loadCount, offset).let { list ->
				var id = offset + 1
				list.forEach { map[id++] = it }
				loaded.value = map.size
				_isLoading.value = false
			}
		}
	}

	override suspend fun get(position: Int): T? {
		if (position == loaded.value - loadCount / 2 && count.value != loaded.value) {
			load(loaded.value)
		}
		return map[position + 1]
	}


	override fun count(): Int {
		return loaded.value
	}

	override fun countFlow() = loaded.asStateFlow()

	override fun isLoading() = loading

}