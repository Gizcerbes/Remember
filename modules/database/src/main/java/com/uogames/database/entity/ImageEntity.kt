package com.uogames.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "images_table")
data class ImageEntity(
	@PrimaryKey(autoGenerate = true)
	@ColumnInfo(name = "id")
	val id: Int,
	@ColumnInfo(name = "img_uri")
	val imgUri: String,
	@ColumnInfo(name = "global_id")
	val globalId: Long?,
	@ColumnInfo(name = "global_owner")
	val globalOwner: String?
)