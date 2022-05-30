package com.uogames.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "images_table")
data class ImageEntity(
	@PrimaryKey(autoGenerate = true)
	val id: Int,
	val imgBase64: String
)