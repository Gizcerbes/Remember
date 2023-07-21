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
	indices = [
		Index("id_module", "id_card", unique = true),
		Index(value = ["global_id"], orders = [Index.Order.ASC])
	]
)
data class ModuleCardEntity(
	@PrimaryKey(autoGenerate = true)
	@ColumnInfo(name = "id")
	val id: Int,
	@ColumnInfo(name = "id_module")
	val idModule: Int,
	@ColumnInfo(name = "id_card", index = true)
	val idCard: Int,
	@ColumnInfo(name = "global_id")
	val globalId: String,
	@ColumnInfo(name = "global_owner")
	val globalOwner: String?
)
