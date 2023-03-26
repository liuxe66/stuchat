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
    private var isFirst: Boolean by Preference(Preference.isFirstOpen, true)
    private var wordsList: List<WordBean> by PrefUtils(PrefUtils.prefWordList, ArrayList())
    private var userData: UserBean by PrefUtils(PrefUtils.prefUser, UserBean())
    var pageLimit = 100
    var recordList = ArrayList<Record>()
    var recordData = MutableLiveData<List<Record>>()

    var scrollToBottom = MutableLiveData<Boolean>()
    var playSvga = MutableLiveData<Int>()
    var intoRoom = MutableLiveData<UserBean>()
    var initFinish = MutableLiveData<Long>()
    fun initUser() = liveData<UserBean> {
        val user = UserBean(
            nickName = creatNickName(),//昵称
            avatar = creatAvatar(),//头像
            coin = 1000,//金币
            avatarBg = 0,//头像框
            intoBg = 0,//座驾
            level = 1,//等级
            value = 0//经验值
        )
        userData = user
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
        recordList.clear()
        withContext(Dispatchers.IO) {
            var records = RedCatApp.appDatabase.recordDao().queryPageRecord(limit = pageLimit, 0)
            recordList.addAll(records.reversed())
        }
        LogUtils.e("recordList:" + recordList)
        scrollToBottom.value = true
        recordData.value = recordList
    }

    fun initData() = viewModelScope.launch {
        recordList.clear()
        withContext(Dispatchers.IO) {
            var records = RedCatApp.appDatabase.recordDao().queryPageRecord(limit = pageLimit, 0)
            recordList.addAll(records.reversed())
        }
        LogUtils.e("recordList:" + recordList)
        scrollToBottom.value = true
        recordData.value = recordList
        initFinish.value = System.currentTimeMillis()
    }

    /**
     * 查询下一页
     * @return Job
     */
    fun getRecordNext() = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            var records = RedCatApp.appDatabase.recordDao()
                .queryPageRecord(limit = pageLimit, recordList.size)
            recordList.addAll(0, records.reversed())
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
                    wordTrans = wordTrans,
                    timestamp = System.currentTimeMillis()
                )
            )
        }
        getRecords()
    }

    /**
     * 插入记录
     * @param record Record
     * @return Job
     */
    fun updateRecord(
        id: Int
    ) = viewModelScope.launch {
        withContext(Dispatchers.IO) {

            val record = RedCatApp.appDatabase.recordDao().queryById(id)
            record.unread = 1

            RedCatApp.appDatabase.recordDao().updateRecord(record)
        }
    }

    /**
     * 首次使用
     */
    fun sayHello() = viewModelScope.launch {
        if (isFirst) {
            //产品介绍
            var hello = "欢迎${userData.nickName},进入自习室，我是自习室的管理员呀哈哈"
            var hello1 = "我们的自习室，主要目标是学习英语单词，每天任务是学习50个单词。"
            var ins = "在这里，你可以通过学习单词来赚取金币"
            var ins1 = "每学习一个单词会奖励10个金币，每天完成任务额外奖励100个"
            var ins2 = "金币有什么用？要不，你慢慢探索吧？"
            insertRecord(AppConfig.type_sys_text, text = hello)
            delay((80 * hello.length).toLong())
            insertRecord(AppConfig.type_sys_text, text = hello1)
            delay((80 * hello1.length).toLong())
            insertRecord(AppConfig.type_sys_text, text = ins)
            delay((80 * ins.length).toLong())
            insertRecord(AppConfig.type_sys_text, text = ins1)
            delay((80 * ins1.length).toLong())
            insertRecord(AppConfig.type_sys_text, text = ins2)
            delay((80 * ins2.length).toLong())
            isFirst = false
        }
        randomEvent()
    }

    /**
     * 随机事件
     */
    fun randomEvent() = viewModelScope.launch {
        when ((1 until 6).random()) {
            1 -> {
                val luxun = " 我家门前有两棵树，一棵是枣树，另一棵也是枣树。"
                insertRecord(
                    AppConfig.type_sys_text,
                    text = luxun,
                    nickName = creatNickName(),
                    avatar = creatAvatar()
                )
            }
            2 -> {
                val user = UserBean(nickName = creatNickName(), avatar = creatAvatar())
                insertRecord(AppConfig.type_sys_notice, text = "欢迎${user.nickName}进入自习室")
                intoRoom.value = user
            }
            3 -> {
                val image = creatImage()
                insertRecord(
                    AppConfig.type_sys_pic,
                    image = image,
                    nickName = creatNickName(),
                    avatar = creatAvatar()
                )
                playSvga.value = image
            }
            4 -> {
                val luxun = "help me"
                insertRecord(
                    AppConfig.type_user_text,
                    text = luxun,
                    nickName = creatNickName(),
                    avatar = creatAvatar()
                )
            }
            5 -> {
                val image = creatImage()
                insertRecord(
                    AppConfig.type_user_pic,
                    image = image,
                    nickName = creatNickName(),
                    avatar = creatAvatar()
                )
                playSvga.value = image
            }
            else -> {}
        }

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
        var index = (1 until nickNameList.size).random()
        return nickNameList[index]
    }

    /**
     * 随机创建头像
     * @return Int
     */
    fun creatAvatar(): Int {
        return (1 until 8).random()
    }

    /**
     * 随机创建头像
     * @return Int
     */
    fun creatImage(): Int {
        return (1 until 9).random()
    }
}