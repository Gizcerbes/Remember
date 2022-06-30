package com.uogames.database.entity

import androidx.room.*

@Entity(
	tableName = "module_card",
	foreignKeys = [
		ForeignKey(
			entity = ModuleEntity::class,
			parentColumns = ["id"],
			childColumns = ["idModule"],
			onDelete = ForeignKey.CASCADE
		),
		ForeignKey(
			entity = CardEntity::class,
			parentColumns = ["id"],
			childColumns = ["idCard"],
			onDelete = ForeignKey.CASCADE
		)
	]
)
data class ModuleCardEntity(
	@PrimaryKey(autoGenerate = true)
	val id: Int,
	@ColumnInfo(index = true)
	val idModule: Int,
	@ColumnInfo(index = true)
	val idCard: Int
)