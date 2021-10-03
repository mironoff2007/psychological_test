package com.mironov.psychologicaltest.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.mironov.psychologicaltest.model.Question


@Database(entities = [Question::class], version = 1, exportSchema = false)
abstract class QuestionDatabase : RoomDatabase() {

    abstract fun userDao(): QuestionDatabase

    companion object {
        @Volatile
        private var INSTANCE: QuestionDatabase? = null

        fun getDatabase(context: Context): QuestionDatabase{
            val tempInstance = INSTANCE
            if(tempInstance != null){
                return tempInstance
            }
            synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    QuestionDatabase::class.java,
                    "user_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }

}