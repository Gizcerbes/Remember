package com.uogames.database.entity

import androidx.room.*

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
	]
)
data class ModuleCardEntity(
	@PrimaryKey(autoGenerate = true)
	@ColumnInfo(name = "id")
	val id: Int,
	@ColumnInfo(name = "id_module", index = true)
	val idModule: Int,
	@ColumnInfo(name = "id_card", index = true)
	val idCard: Int,
	@ColumnInfo(name = "global_id")
	val globalId: Long?,
	@ColumnInfo(name = "global_owner")
	val globalOwner: String?
)