package com.uogames.database.entity

import androidx.annotation.Nullable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
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
    @ColumnInfo(name = "id_image")
    val idImage: Int? = null,
    @ColumnInfo(name = "id_pronounce")
    val idPronounce: Int? = null,
    @ColumnInfo(name = "id_phrase")
    val idPhrase: Int? = null,
    @ColumnInfo(name = "id_card")
    val idCard: Int? = null,
    @ColumnInfo(name = "id_module")
    val idModule: Int? = null,
    @ColumnInfo(name = "id_module_card")
    val idModuleCard: Int? = null
) {

    companion object {
        private const val v4 =
            "CREATE TABLE `share_table` " +
                    "(`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "`id_image` INTEGER, " +
                    "`id_pronounce` INTEGER, " +
                    "`id_phrase` INTEGER, " +
                    "`id_card` INTEGER, " +
                    "`id_module` INTEGER, " +
                    "`id_module_card` INTEGER, " +
                    "FOREIGN KEY(`id_image`) REFERENCES `images_table`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , " +
                    "FOREIGN KEY(`id_pronounce`) REFERENCES `pronounce_table`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , " +
                    "FOREIGN KEY(`id_phrase`) REFERENCES `phrase_table`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , " +
                    "FOREIGN KEY(`id_card`) REFERENCES `cards_table`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , " +
                    "FOREIGN KEY(`id_module`) REFERENCES `modules`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , " +
                    "FOREIGN KEY(`id_module_card`) REFERENCES `module_card`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE );"


        fun migration_3_4() :Migration = object : Migration(3,4){

            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(v4)
            }

        }

    }

}