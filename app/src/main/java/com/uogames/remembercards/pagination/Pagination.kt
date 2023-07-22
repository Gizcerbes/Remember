package com.uogames.remembercards.pagination

import com.uogames.remembercards.models.SearchingState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
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
	private val _loadState = MutableStateFlow(SearchingState.SEARCHED)
	override val loadState: Flow<SearchingState> = _loadState.asStateFlow()

	private var job: Job? = null


	override suspend fun reload() {
		job?.cancel()
		job = workScope.launch {
			runCatching {
				_loadState.value = SearchingState.SEARCHING
				_isLoading.value = true
				loaded.value = 0
				_count.value = askCount()
				map.clear()
				delay(100)
				val offset = 0
				askPage(loadCount, 0).let { list ->
					var id = offset + 1
					list.forEach { map[id++] = it }
					loaded.value = map.size
				}
				loaded.value = map.size
				_loadState.value = SearchingState.SEARCHED
				_isLoading.value = false
			}.onFailure {
				if (it is CancellationException) return@launch
				_loadState.value = SearchingState.FAIL
				_isLoading.value = false
			}
		}
	}

	private fun load(offset: Int) {
		job?.cancel()
		job = workScope.launch {
			runCatching {
				_loadState.value = SearchingState.SEARCHING
				_isLoading.value = true
				delay(100)
				_count.value = askCount()
				askPage(loadCount, offset).let { list ->
					var id = offset + 1
					list.forEach { map[id++] = it }
					loaded.value = map.size
				}
				_loadState.value = SearchingState.SEARCHED
				_isLoading.value = false
			}.onFailure {
				if (it is CancellationException) return@launch
				_loadState.value = SearchingState.FAIL
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