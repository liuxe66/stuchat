package com.redcat.stuchat.data.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 *  author : liuxe
 *  date : 2023/3/22 15:44
 *  description :
 */
@Entity(tableName = "word")
data class Word(
    @PrimaryKey(autoGenerate = true) val wordId:Int = 0,
    val name: String,
    val usphone: String?,
    val trans: String?,
    var flag: Int = 0
)