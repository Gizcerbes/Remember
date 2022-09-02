package com.uogames.database.entity

import androidx.room.*


@Entity(
	tableName = "error_card",
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
data class ErrorCardEntity(
	@PrimaryKey(autoGenerate = true)
	@ColumnInfo(name = "id")
	val id: Long = 0,
	@ColumnInfo(name = "id_phrase")
	val idPhrase: Int,
	@ColumnInfo(name = "id_translate")
	val idTranslate: Int,
	@ColumnInfo(name = "correct")
	val correct: Long,
	@ColumnInfo(name = "incorrect")
	val incorrect: Long,
	@ColumnInfo(name = "percent_correct")
	val percentCorrect: Byte
){
	companion object{
		private const val v1 = "CREATE TABLE `error_card` (" +
				"`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
				"`id_phrase` INTEGER NOT NULL, " +
				"`id_translate` INTEGER NOT NULL, " +
				"`correct` INTEGER NOT NULL, " +
				"`incorrect` INTEGER NOT NULL, " +
				"`percent_correct` INTEGER NOT NULL, " +
				"FOREIGN KEY(`id_phrase`) REFERENCES `phrase_table`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , " +
				"FOREIGN KEY(`id_translate`) REFERENCES `phrase_table`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE " +
				");"
		private const val indexV1 = "CREATE UNIQUE INDEX `index_error_card_id_phrase_id_translate` ON `error_card` (`id_phrase`, `id_translate`);"
	}
}