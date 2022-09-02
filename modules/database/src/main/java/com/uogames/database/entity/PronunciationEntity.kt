package com.uogames.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "pronounce_table")
data class PronunciationEntity(
	@PrimaryKey(autoGenerate = true)
	@ColumnInfo(name = "id")
	val id: Int = 0,
	@ColumnInfo(name = "audio_uri")
	val audioUri: String,
	@ColumnInfo(name = "global_id")
	val globalId: UUID?,
	@ColumnInfo(name = "global_owner")
	val globalOwner: String?
){

	companion object{
		private const val v1 = "CREATE TABLE `pronounce_table` (" +
				"`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
				"`audio_uri` TEXT NOT NULL, " +
				"`global_id` BLOB, " +
				"`global_owner` TEXT" +
				");"
	}


}