package com.uogames.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.*

@Entity(
	tableName = "images_table",
	indices = [
		Index(value = ["global_id"], orders = [Index.Order.ASC])
	]
)
data class ImageEntity(
	@PrimaryKey(autoGenerate = true)
	@ColumnInfo(name = "id")
	val id: Int,
	@ColumnInfo(name = "img_uri")
	val imgUri: String,
	@ColumnInfo(name = "global_id")
	val globalId: String,
	@ColumnInfo(name = "global_owner")
	val globalOwner: String?
)
