package com.uogames.database.entity

import androidx.room.*

@Entity(
	tableName = "phrase_table",
	foreignKeys = [
		ForeignKey(
			entity = PronunciationEntity::class,
			parentColumns = ["id"],
			childColumns = ["id_pronounce"],
			onDelete = ForeignKey.SET_NULL
		), ForeignKey(
			entity = ImageEntity::class,
			parentColumns = ["id"],
			childColumns = ["id_image"],
			onDelete = ForeignKey.SET_NULL
		)],
	indices = [
		Index(value = ["global_id"], orders = [Index.Order.ASC])
	]
)
data class PhraseEntity(
	@PrimaryKey(autoGenerate = true)
	@ColumnInfo(name = "id")
	val id: Int,
	@ColumnInfo(name = "phrase")
	val phrase: String,
	@ColumnInfo(name = "definition")
	val definition: String?,
	@ColumnInfo(name = "lang")
	val lang: String,
	@ColumnInfo(name = "country")
	val country: String,
	@ColumnInfo(name = "id_pronounce", index = true)
	val idPronounce: Int?,
	@ColumnInfo(name = "id_image", index = true)
	val idImage: Int?,
	@ColumnInfo(name = "time_change")
	val timeChange: Long,
	@ColumnInfo(name = "like")
	val like: Long,
	@ColumnInfo(name = "dislike")
	val dislike: Long,
	@ColumnInfo(name = "global_id")
	val globalId: String,
	@ColumnInfo(name = "global_owner")
	val globalOwner: String?,
	@ColumnInfo(name = "changed", defaultValue = "false")
	val changed: Boolean = false
)
