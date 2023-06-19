package com.uogames.database.map

import com.uogames.database.entity.UserEntity
import com.uogames.dto.User

object UserMapper : Map<UserEntity, User> {

	override fun UserEntity.toDTO() = User(
		globalOwner = globalId,
		name = name
	)

	override fun User.toEntity() = UserEntity(
		globalId = globalOwner,
		name = name
	)
}