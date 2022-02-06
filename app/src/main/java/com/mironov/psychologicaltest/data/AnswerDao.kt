package com.mironov.psychologicaltest.data

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import com.mironov.psychologicaltest.model.Answer
import com.mironov.psychologicaltest.model.TestResult

@Dao
interface AnswerDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addAnswer(answer: Answer)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addTestResult(answer: TestResult)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    @JvmSuppressWildcards
    fun insertAllAnswers(list: List<Answer>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    @JvmSuppressWildcards
    fun insertAllResults(list: List<TestResult>)

    @Update
    suspend fun updateAnswer(answer: Answer)

    @Delete
    suspend fun deleteAnswer(answer: Answer)

    @Query("DELETE FROM Answer")
    fun resetTable( )

    @RawQuery(observedEntities = [Answer::class])
    fun readAnswersByTest(query: SupportSQLiteQuery): LiveData<List<Answer?>>

    @Query("SELECT * FROM Answer")
    fun readAllAnswers(): LiveData<List<Answer?>>

    @Query("SELECT *  FROM TestResult Where id=:id")
    fun readTestResult(id:Int): LiveData<TestResult?>

}