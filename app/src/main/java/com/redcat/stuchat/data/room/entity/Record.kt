package com.redcat.stuchat.data.room.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 *  author : liuxe
 *  date : 2023/3/23 11:33
 *  description :
 */
@Entity(tableName = "record")
data class Record(
    @PrimaryKey(autoGenerate = true) val recordId: Int = 0,
    /**
     * type 暂定 1 系统文字 2 系统图片 3 系统单词 4 系统通知 5 用户文字 6 用户图片
     */
    val type: Int,
    //图片
    val image: Int? = null,
    //文字
    val text: String? = null,
    //系统通知
    val notice:String? = null,

    //系统单词
    val wordName: String? = null,
    val wordUs: String? = null,
    val wordTrans: String? = null,
    //用户
    val nickName:String? = null,
    val avatar:Int? = null,
    val frame:Int? = null,
    val veh:Int? = null,
    val timestamp:Long = System.currentTimeMillis(),
)