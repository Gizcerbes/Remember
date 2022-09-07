package com.uogames.network.provider

import com.uogames.network.map.UserMap.toDTO
import com.uogames.network.service.UserService

class UserProvider(private val service: UserService) {

	suspend fun get(globalOwner: String) = service.get(globalOwner).toDTO()

}