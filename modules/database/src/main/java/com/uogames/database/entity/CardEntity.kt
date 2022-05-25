package com.uogames.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "card_table")
data class CardEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val phrase: String,
    val translate: String
)