package com.redcat.stuchat.data.bean

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.Serializable

/**
 *  author : liuxe
 *  date : 2023/3/24 13:33
 *  description :
 */
data class UserBean(
    var userId:Int = 0,
    var nickName: String = "",//昵称
    var avatar: Int = 0,//头像
    var coin: Int = 0,//金币
    var frame: Int = 0,//头像框
    var veh: Int = 0,//座驾
    var level: Int = 1,//等级
    var value: Int = 0,//经验值
    var wordNum: Int = 0//单词数
) : Serializable