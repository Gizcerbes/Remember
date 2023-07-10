package com.uogames.database.entity

import androidx.core.database.getBlobOrNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.migration.Migration
import androidx.room.util.convertByteToUUID
import androidx.sqlite.db.SupportSQLiteDatabase
import java.util.UUID

@Entity(
	tableName = "download_table",
)
data class DownloadEntity(
	@PrimaryKey(autoGenerate = true)
	@ColumnInfo(name = "id")
	val id: Int = 0,
	@ColumnInfo(name = "global_phrase_id")
	val globalPhraseId: String? = null,
	@ColumnInfo(name = "global_card_id")
	val globalCardId: String? = null,
	@ColumnInfo(name = "global_module_id")
	val globalModuleId: String? = null
)

