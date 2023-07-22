package com.uogames.remembercards.pagination

import com.uogames.remembercards.models.SearchingState
import kotlinx.coroutines.flow.Flow


interface IPagination<T> {

	val loadState: Flow<SearchingState>

	suspend fun reload()

	suspend fun get(position: Int): T?

	fun count(): Int

	fun countFlow(): Flow<Int>

	fun isLoading(): Flow<Boolean>



}