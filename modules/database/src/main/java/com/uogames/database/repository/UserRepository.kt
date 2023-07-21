package com.uogames.database.repository

import com.uogames.database.dao.UserDAO
import com.uogames.database.entity.UserEntity

class UserRepository(val dao: UserDAO) {

	suspend fun save(user: UserEntity) = dao.insert(user)

	suspend fun delete(user: UserEntity) = dao.delete(user)

	suspend fun getById(userId: String) = dao.getById(userId)

	fun getByIdFlow(userId: String) = dao.getByIdFlow(userId)

}