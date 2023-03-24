package com.redcat.stuchat.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.redcat.stuchat.data.room.entity.Record

/**
 *  author : liuxe
 *  date : 2023/3/23 11:33
 *  description :
 */
@Dao
interface RecordDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRecord(record: Record)

    //分页查询 offset代表从第几条记录“之后“开始查询，limit表明查询多少条结果
    @Query("SELECT * FROM record order by record.recordId desc Limit :limit Offset :offset")
    fun queryPageRecord(limit:Int,offset:Int):List<Record>

}