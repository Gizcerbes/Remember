package com.uogames.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.InputStream

@Entity(tableName = "images_table")
data class ImageEntity(
	@PrimaryKey(autoGenerate = true)
	val id: Int,
	@ColumnInfo(typeAffinity = ColumnInfo.TEXT)
	val imgBase64: String
)