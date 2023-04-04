package com.redcat.stuchat.ui

import com.redcat.stuchat.base.BaseViewModel
import com.redcat.stuchat.data.bean.WordBean
import com.redcat.stuchat.utils.PrefUtils

/**
 *  author : liuxe
 *  date : 2023/3/31 10:30
 *  description :
 */
class WordVM:BaseViewModel() {

    private var wordsList: List<WordBean> by PrefUtils(PrefUtils.prefWordList, ArrayList())
    private var wordsIndex by PrefUtils(PrefUtils.prefWordIndex, 0)

    /**
     * 获取今日需要背的单词
     */
    fun getTodayWord(){
        //取十个单词
    }

    /**
     * 开始闯关
     * 1.打乱单词顺序
     * 2.按照新的排序取单词，当选错答案时，记录错题(陌生)，在全部问题结束之后，重新显示错题(模糊)
     * 3.取单词，挖空填字母，考察拼写，记录错题(陌生)，在全部问题结束之后，重新显示错题(模糊)
     * 4。全部结束，闯关完成。同时错题记录在数据库
     */
    fun startGame(){

    }

    /**
     * 创建闯关单词集合
     */
    fun creatGameWord(){

    }

    /**
     * 背单词记录统计
     * 统计内容：背了多少单词，背了几天，今日目标
     * 在用户系统中记录
     */
    fun getUserWordData(){

    }
}