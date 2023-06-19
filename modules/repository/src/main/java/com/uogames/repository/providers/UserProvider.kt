package com.uogames.repository.providers

import com.uogames.clientApi.version3.network.NetworkProvider
import com.uogames.database.DatabaseRepository
import com.uogames.database.repository.UserRepository
import com.uogames.dto.User
import com.uogames.repository.DataProvider

class UserProvider(
    private val dataProvider: DataProvider,
    private val up: UserRepository,
    private val network: NetworkProvider
) {

    suspend fun insert(user: User) = up.save(user)

    fun getByOwnerFlow(globalOwner: String) = up.getByIdFlow(globalOwner)

    suspend fun getByUid(uid: String) = up.getById(uid)

    suspend fun getGlobalByUid(uid: String) = network.user.get(uid)

    suspend fun update(globalOwner: String) {
        val user = network.user.get(globalOwner)
        insert(
            User(
                globalOwner = user.globalOwner,
                name = user.name
            )
        )
    }

}