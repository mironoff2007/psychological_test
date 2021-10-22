package com.mironov.psychologicaltest.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.mironov.psychologicaltest.model.Answer
import com.mironov.psychologicaltest.model.TestResult


@Database(entities = [Answer::class,TestResult::class], version = 1, exportSchema = false)
abstract class AnswerDatabase : RoomDatabase() {

    abstract fun answerDao(): AnswerDao

    companion object {
        @Volatile
        private var INSTANCE: AnswerDatabase? = null

        fun getDatabase(context: Context): AnswerDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AnswerDatabase::class.java,
                    "psychological_test_answer.db"
                ).setJournalMode(JournalMode.TRUNCATE).build()
                INSTANCE = instance
                return instance
            }
        }
    }

}