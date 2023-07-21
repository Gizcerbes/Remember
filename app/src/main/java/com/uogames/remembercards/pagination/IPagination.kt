package com.uogames.remembercards.pagination

import kotlinx.coroutines.flow.Flow


interface IPagination<T> {


	suspend fun reload()

	suspend fun get(position: Int): T?

	fun count(): Int

	fun countFlow(): Flow<Int>

	fun isLoading(): Flow<Boolean>


}