package com.uogames.repository.map

import com.uogames.database.entity.UserEntity
import com.uogames.dto.User

object UserMap {

	fun UserEntity.toDTO() = User(
		globalOwner = globalId,
		name = name
	)

	fun User.toEntity() = UserEntity(
		globalId = globalOwner,
		name = name
	)

}