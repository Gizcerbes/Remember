package com.uogames.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "test")
data class TestEntity(
	@PrimaryKey(autoGenerate = true)
	val id: Long,
	val uuid: UUID

)