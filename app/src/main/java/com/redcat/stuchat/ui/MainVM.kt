package com.redcat.stuchat.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.liuxe.lib_common.utils.LogUtils
import com.redcat.stuchat.app.AppConfig
import com.redcat.stuchat.app.RedCatApp
import com.redcat.stuchat.base.BaseViewModel
import com.redcat.stuchat.data.bean.UserBean
import com.redcat.stuchat.data.bean.WordBean
import com.redcat.stuchat.data.room.entity.Record
import com.redcat.stuchat.utils.PrefUtils
import com.redcat.stuchat.utils.Preference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.reflect.Type

/**
 *  author : liuxe
 *  date : 2023/3/22 16:33
 *  description :
 */
class MainVM : BaseViewModel() {
    private var isFirst:Boolean by Preference(Preference.isFirst,true)
    private var wordsList: List<WordBean> by PrefUtils(PrefUtils.prefWordList, ArrayList())
    private var userData: UserBean by PrefUtils(PrefUtils.prefUser, UserBean())
    var pageLimit = 20
    var recordList = ArrayList<Record>()
    var recordData = MutableLiveData<List<Record>>()

    fun initData() = liveData<UserBean> {
        var user = UserBean(
            nickName = creatNickName(),//昵称
            avatar = creatAvatar(),//头像
            coin = 1000,//金币
            avatarBg = 0,//头像框
            intoBg = 0,//座驾
            level = 1,//等级
            value = 0//经验值
        )
        LogUtils.e("" + user.toString())
        emit(user)
    }

    /**
     * 单词初始化
     * @return LiveData<String>
     */
    fun loadWord(fileName: String = "word.json") = liveData<String> {
        withContext(Dispatchers.IO) {
            // 解析Json数据
            val newstringBuilder = StringBuilder()
            var inputStream: InputStream? = null
            try {
                inputStream = RedCatApp.context.assets.open(fileName)
                val isr = InputStreamReader(inputStream)
                val reader = BufferedReader(isr)
                var jsonLine: String?
                while (reader.readLine().also { jsonLine = it } != null) {
                    newstringBuilder.append(jsonLine)
                }
                reader.close()
                isr.close()
                inputStream.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            val str = newstringBuilder.toString()
            val gson = Gson()
            val wordType: Type = object : TypeToken<List<WordBean>>() {}.type
            wordsList = gson.fromJson(str, wordType)
            LogUtils.e("wordList:" + wordsList.size)
        }
        emit("success")
    }


    /**
     * 获得第一页数据
     * @return Job
     */
    fun getRecords() = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            var records = RedCatApp.appDatabase.recordDao().queryPageRecord(limit = pageLimit, 0)
            recordList.addAll(records)
        }
        recordData.value = recordList
    }

    /**
     * 查询下一页
     * @return Job
     */
    fun getRecordNext() = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            var records = RedCatApp.appDatabase.recordDao()
                .queryPageRecord(limit = pageLimit, recordList.size)
            recordList.addAll(records)
        }
        recordData.value = recordList
    }

    /**
     * 插入记录
     * @param record Record
     * @return Job
     */
    fun insertRecord(
        type: Int,
        //图片
        image: Int? = null,
        //文字
        text: String? = null,
        //系统通知
        notice: String? = null, nickName: String? = null, avatar: Int? = null,
        //系统单词
        wordName: String? = null, wordUs: String? = null, wordTrans: String? = null
    ) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            RedCatApp.appDatabase.recordDao().insertRecord(
                Record(
                    type = type,
                    image = image,
                    text = text,
                    notice = notice,
                    nickName = nickName,
                    avatar = avatar,
                    wordName = wordName,
                    wordUs = wordUs,
                    wordTrans = wordTrans
                )
            )
            recordList.clear()
        }
        getRecords()
    }

    /**
     * 首次使用
     */
    fun sayHello() = viewModelScope.launch {
        //产品介绍
        var hello = "${userData.nickName},你好，我是Yahh，是你的学习小秘书"
        var ins = "在这里，你可以通过学习单词来增加等级以及赚取金币"
        var ins1 = "等级有什么用？其实没什么用，哈哈哈"
        var ins2 = "金币有什么用？金币可以买礼物，买头像框，买座驾"
        var ins3 = "礼物有什么用？发送礼物，有礼物特效，可以看特效啊，赚大了"
        var ins4 = "头像框和座驾有什么用？装B啊，这个太有用了"
        insertRecord(AppConfig.type_sys_text, text = hello)
        delay(500)
        insertRecord(AppConfig.type_sys_text, text = ins)
        delay(500)
        insertRecord(AppConfig.type_sys_text, text = ins1)
        delay(500)
        insertRecord(AppConfig.type_sys_text, text = ins2)
        delay(500)
        insertRecord(AppConfig.type_sys_text, text = ins3)
        delay(500)
        insertRecord(AppConfig.type_sys_text, text = ins4)
        isFirst = false
    }

    /**
     * 随机事件
     */
    fun randomEvent() {

    }

    /**
     * 随机创建昵称
     * @return String
     */
    fun creatNickName(): String {
        var nickNameList = ArrayList<String>()
        nickNameList.add("轻狂、书生")
        nickNameList.add("落单恋人")
        nickNameList.add("低沉旳呢喃")
        nickNameList.add("宝宝猪")
        nickNameList.add("猪猪公主")
        nickNameList.add("猴哥")
        nickNameList.add("叁三")
        nickNameList.add("瓦妹里仔仔")
        var index = (0 until nickNameList.size - 1).random()
        return nickNameList[index]
    }

    /**
     * 随机创建头像
     * @return Int
     */
    fun creatAvatar(): Int {
        return (1 until 14).random()
    }
}