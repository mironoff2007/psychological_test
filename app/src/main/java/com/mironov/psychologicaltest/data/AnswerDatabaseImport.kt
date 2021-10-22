package com.mironov.psychologicaltest.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.mironov.psychologicaltest.model.Answer
import com.mironov.psychologicaltest.model.TestResult
import java.io.File


@Database(entities = [Answer::class, TestResult::class], version = 1, exportSchema = false)
abstract class AnswerDatabaseImport : RoomDatabase() {

    abstract fun answerDaoImport(): AnswerDaoImport

    companion object {
        @Volatile
        private var INSTANCE: AnswerDatabaseImport? = null

        fun getDatabase(context: Context,file:File): AnswerDatabaseImport {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AnswerDatabaseImport ::class.java,
                    "answer_imported.sqlite"
                ).createFromFile(file)
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }

}