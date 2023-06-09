package com.redcat.stuchat.app

import com.redcat.stuchat.R

/**
 *  author : liuxe
 *  date : 2023/3/24 13:42
 *  description :
 */
class AppConfig {

    companion object {
        //1 系统文字 2 系统图片 3 系统单词 4 系统通知 5 用户文字 6 用户图片
        const val type_sys_text = 1
        const val type_sys_pic = 2
        const val type_sys_word = 3
        const val type_sys_notice = 4
        const val type_user_text = 5
        const val type_user_pic = 6
        const val type_timestamp = 7

        //用户金币
        //初次使用会送1000金币
        // 基础学习一个单词+10
        // 每天累计学习20个单词额外+20 学习50个单词额外+50 每天完成任务会有570个金币

        //用户等级
        //累计学习单词数：
        //0-50  50-> 1级
        //50-150 100 ->2
        //150-300 150 ->3
        //300-500 200 ->4
        //500-800 300 ->5
        //800-1200 400 ->6
        //1200-1700 500 ->7
        //1700-2300 600 ->8
        //2300-3000 700 ->9
        //3000-  ->10

        fun getPhoto(image: Int?) = when (image) {

            0 -> R.drawable.ic_avatar1
            1 -> R.drawable.ic_avatar2
            2 -> R.drawable.ic_avatar3
            3 -> R.drawable.ic_avatar4
            4 -> R.drawable.ic_avatar5
            5 -> R.drawable.ic_avatar6
            6 -> R.drawable.ic_avatar7
            7 -> R.drawable.ic_avatar8
            8 -> R.drawable.ic_avatar9
            10 -> R.drawable.ic_avatar10

            99 ->R.drawable.ic_avatar
            100 ->R.drawable.ic_avatar_luxun
            else -> R.drawable.ic_logo
        }

        fun getImage(image: Int?) = when (image) {
            1 -> R.drawable.ic_gift_rose
            2 -> R.drawable.ic_gift_bear
            3 -> R.drawable.ic_gift_boom
            4 -> R.drawable.ic_gift_birth
            5 -> R.drawable.ic_gift_car
            6 -> R.drawable.ic_gift_firework
            7 -> R.drawable.ic_gift_rocket
            8 -> R.drawable.ic_gift_room
            else -> R.drawable.ic_gift_rose
        }

        fun getSvga(image: Int?) = when (image) {
            1 -> "gift_rose"
            2 -> "gift_bear"
            3 -> "gift_boom"
            4 -> "gift_birth"
            5 -> "gift_car"
            6 -> "gift_firework"
            7 -> "gift_rock"
            8 -> "gift_room"
            else -> "gift_rose"
        }

        fun getVehSvga(ven:Int?) = when(ven){
            1 -> "veh_lv"
            2 -> "veh_falali"
            3 -> "veh_maibahe"
            4 -> "veh_air"
            else -> ""
        }

        fun getFrameSvga(ven:Int?) = when(ven){
            1 -> "frame1"
            2 -> "frame2"
            3 -> "frame3"
            4 -> "frame4"
            5 -> "frame5"
            6 -> "frame6"
            else -> ""
        }

    }
}