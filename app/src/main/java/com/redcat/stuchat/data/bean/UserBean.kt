package com.redcat.stuchat.data.bean

/**
 *  author : liuxe
 *  date : 2023/3/24 13:33
 *  description :
 */
data class UserBean(
    var nickName: String = "",//昵称
    var avatar:Int = 0,//头像
    var coin:Int = 0,//金币
    var avatarBg:Int = 0,//头像框
    var intoBg:Int = 0,//座驾
    var level:Int = 1,//等级
    var value:Int = 0//经验值
)