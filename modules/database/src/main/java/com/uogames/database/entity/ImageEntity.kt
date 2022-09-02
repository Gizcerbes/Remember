package com.uogames.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "images_table")
data class ImageEntity(
	@PrimaryKey(autoGenerate = true)
	@ColumnInfo(name = "id")
	val id: Int,
	@ColumnInfo(name = "img_uri")
	val imgUri: String,
	@ColumnInfo(name = "global_id")
	val globalId: UUID?,
	@ColumnInfo(name = "global_owner")
	val globalOwner: String?
){

	companion object{
		private const val v1 = "CREATE TABLE `images_table` (" +
				"`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
				"`img_uri` TEXT NOT NULL, " +
				"`global_id` BLOB, " +
				"`global_owner` TEXT" +
				");"
	}


}