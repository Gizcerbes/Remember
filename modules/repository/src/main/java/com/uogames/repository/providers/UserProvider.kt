package com.uogames.repository.providers

import com.uogames.database.DatabaseRepository
import com.uogames.dto.User
import com.uogames.network.NetworkProvider
import com.uogames.repository.DataProvider

class UserProvider(
	private val dataProvider: DataProvider,
	database: DatabaseRepository,
	private val network: NetworkProvider
) {
	private val up = database.userRepository

	private suspend fun insert(user: User) = up.save(user)

	fun getByOwnerFlow(globalOwner: String) = up.getByIdFlow(globalOwner)

	suspend fun update(globalOwner: String) {
		val user = network.user.get(globalOwner)
		insert(user)
	}

}