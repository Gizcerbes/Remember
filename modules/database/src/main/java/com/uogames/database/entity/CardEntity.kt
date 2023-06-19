package com.uogames.database.entity

import androidx.room.*
import java.util.*

@Entity(
	tableName = "cards_table",
	foreignKeys = [
		ForeignKey(
			entity = PhraseEntity::class,
			parentColumns = ["id"],
			childColumns = ["id_phrase"],
			onDelete = ForeignKey.CASCADE
		), ForeignKey(
			entity = PhraseEntity::class,
			parentColumns = ["id"],
			childColumns = ["id_translate"],
			onDelete = ForeignKey.CASCADE
		)],
	indices = [Index("id_phrase", "id_translate", unique = true)]
)

data class CardEntity(
	@PrimaryKey(autoGenerate = true)
	@ColumnInfo(name = "id")
	val id: Int = 0,
	@ColumnInfo(name = "id_phrase")
	val idPhrase: Int,
	@ColumnInfo(name = "id_translate")
	val idTranslate: Int,
	@ColumnInfo(name = "id_image")
	val idImage: Int?,
	@ColumnInfo(name = "reason")
	val reason: String,
	@ColumnInfo(name = "time_change")
	val timeChange: Long,
	@ColumnInfo(name = "like")
	val like: Long,
	@ColumnInfo(name = "dislike")
	val dislike: Long,
	@ColumnInfo(name = "global_id")
	val globalId: UUID?,
	@ColumnInfo(name = "global_owner")
	val globalOwner: String?,
	@ColumnInfo(name = "changed", defaultValue = "false")
	val changed: Boolean = false
){
	companion object{
		private const val v1 = "CREATE TABLE `cards_table` (" +
				"`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
				"`id_phrase` INTEGER NOT NULL, " +
				"`id_translate` INTEGER NOT NULL, " +
				"`id_image` INTEGER, `reason` TEXT NOT NULL, " +
				"`time_change` INTEGER NOT NULL, " +
				"`like` INTEGER NOT NULL, " +
				"`dislike` INTEGER NOT NULL, " +
				"`global_id` BLOB, " +
				"`global_owner` TEXT, " +
				"FOREIGN KEY(`id_phrase`) REFERENCES `phrase_table`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , " +
				"FOREIGN KEY(`id_translate`) REFERENCES `phrase_table`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE " +
				");"
		private const val indexV1 = "CREATE UNIQUE INDEX `index_cards_table_id_phrase_id_translate` ON `cards_table` (`id_phrase`, `id_translate`);"
	}
}