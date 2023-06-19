package com.uogames.database.repository

import com.uogames.database.dao.UserDAO
import com.uogames.database.map.UserMapper.toDTO
import com.uogames.database.map.UserMapper.toEntity
import com.uogames.dto.User
import kotlinx.coroutines.flow.map

class UserRepository(val dao: UserDAO) {

	suspend fun save(user: User) = dao.insert(user.toEntity())

	suspend fun delete(user: User) = dao.delete(user.toEntity())

	suspend fun getById(userId: String) = dao.getById(userId)?.toDTO()

	fun getByIdFlow(userId: String) = dao.getByIdFlow(userId).map { it?.toDTO() }

}