package com.uogames.database.repository

import com.uogames.database.dao.NewCardDAO
import com.uogames.database.map.NewCardMap.toDTO
import com.uogames.database.map.NewCardMap.toEntity
import com.uogames.dto.NewCard
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CardRepository(private val dao: NewCardDAO) {

	suspend fun insert(card: NewCard) = dao.insert(card.toEntity())

	suspend fun delete(card: NewCard) = dao.delete(card.toEntity()) > 0

	suspend fun update(card: NewCard) = dao.update(card.toEntity()) > 0

	fun getCountFlow(like:String) = dao.getCountFlow(like)

	fun getCardFlow(like: String, number: Int) = dao.getCardFlow(like, number).map { it?.toDTO() }

	fun getByIdFlow(id: Int) = dao.getByIdFlow(id).map { it?.toDTO() }

}