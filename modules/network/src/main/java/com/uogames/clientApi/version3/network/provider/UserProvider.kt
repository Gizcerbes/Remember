package com.uogames.clientApi.version3.network.provider

import com.uogames.clientApi.version3.network.map.UserMap.toDTO
import com.uogames.clientApi.version3.network.service.UserService

class UserProvider(private val s: UserService) {

    suspend fun get(globalOwner: String) = s.get(globalOwner).toDTO()

    suspend fun getView(globalOwner: String) = s.getView(globalOwner).toDTO()

}