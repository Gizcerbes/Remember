package com.uogames.database.entity

import androidx.room.*
import java.util.*

@Entity(
	tableName = "module_card",
	foreignKeys = [
		ForeignKey(
			entity = ModuleEntity::class,
			parentColumns = ["id"],
			childColumns = ["id_module"],
			onDelete = ForeignKey.CASCADE
		),
		ForeignKey(
			entity = CardEntity::class,
			parentColumns = ["id"],
			childColumns = ["id_card"],
			onDelete = ForeignKey.CASCADE
		)
	],
	indices = [Index("id_module", "id_card", unique = true)]
)
data class ModuleCardEntity(
	@PrimaryKey(autoGenerate = true)
	@ColumnInfo(name = "id")
	val id: Int,
	@ColumnInfo(name = "id_module")
	val idModule: Int,
	@ColumnInfo(name = "id_card")
	val idCard: Int,
	@ColumnInfo(name = "global_id")
	val globalId: UUID?,
	@ColumnInfo(name = "global_owner")
	val globalOwner: String?
){
	companion object{
		private const val v1 = "CREATE TABLE `module_card` (" +
				"`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
				"`id_module` INTEGER NOT NULL, " +
				"`id_card` INTEGER NOT NULL, " +
				"`global_id` BLOB, " +
				"`global_owner` TEXT, " +
				"FOREIGN KEY(`id_module`) REFERENCES `modules`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , " +
				"FOREIGN KEY(`id_card`) REFERENCES `cards_table`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE " +
				");"
		private const val indexV1 = "CREATE UNIQUE INDEX `index_module_card_id_module_id_card` ON `module_card` (`id_module`, `id_card`);"
	}

}
