package com.uogames.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase


@Entity(
    tableName = "share_table",
    foreignKeys = [
        ForeignKey(
            entity = ImageEntity::class,
            parentColumns = ["id"],
            childColumns = ["id_image"],
            onDelete = ForeignKey.CASCADE
        ), ForeignKey(
            entity = PronunciationEntity::class,
            parentColumns = ["id"],
            childColumns = ["id_pronounce"],
            onDelete = ForeignKey.CASCADE
        ), ForeignKey(
            entity = PhraseEntity::class,
            parentColumns = ["id"],
            childColumns = ["id_phrase"],
            onDelete = ForeignKey.CASCADE
        ), ForeignKey(
            entity = CardEntity::class,
            parentColumns = ["id"],
            childColumns = ["id_card"],
            onDelete = ForeignKey.CASCADE
        ), ForeignKey(
            entity = ModuleEntity::class,
            parentColumns = ["id"],
            childColumns = ["id_module"],
            onDelete = ForeignKey.CASCADE
        ), ForeignKey(
            entity = ModuleCardEntity::class,
            parentColumns = ["id"],
            childColumns = ["id_module_card"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ShareEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,
    @ColumnInfo(name = "id_image", index = true)
    val idImage: Int? = null,
    @ColumnInfo(name = "id_pronounce", index = true)
    val idPronounce: Int? = null,
    @ColumnInfo(name = "id_phrase", index = true)
    val idPhrase: Int? = null,
    @ColumnInfo(name = "id_card", index = true)
    val idCard: Int? = null,
    @ColumnInfo(name = "id_module", index = true)
    val idModule: Int? = null,
    @ColumnInfo(name = "id_module_card", index = true)
    val idModuleCard: Int? = null
)