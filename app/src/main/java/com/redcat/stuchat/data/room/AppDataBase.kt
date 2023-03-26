package com.redcat.stuchat.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.redcat.stuchat.data.room.dao.RecordDao
import com.redcat.stuchat.data.room.entity.Record

/**
 *  author : liuxe
 *  date : 2023/3/22 16:13
 *  description :
 */
@Database(entities = [Record::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun recordDao(): RecordDao

    companion object {

        @JvmStatic
        @Volatile
        private var INSTANCE: AppDatabase? = null

        @JvmStatic
        fun getDatabase(context: Context): AppDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext, AppDatabase::class.java, "app.db"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}