package com.mironov.psychologicaltest.data

import androidx.lifecycle.LiveData
import androidx.room.*

import com.mironov.psychologicaltest.model.Answer
import com.mironov.psychologicaltest.model.TestResult

@Dao
interface AnswerDaoImport {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addAnswer(answer: Answer)

    @Query("SELECT * FROM Answer")
    fun readAllAnswers(): LiveData<List<Answer?>>

    @Query("SELECT * FROM TestResult")
    fun readAllTestResult(): LiveData<List<TestResult?>>
}