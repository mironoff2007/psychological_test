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
    fun insertAll(list: List<Answer>)

    @Update
    suspend fun updateAnswer(answer: Answer)

    @Delete
    suspend fun deleteAnswer(answer: Answer)

    @Query("DELETE FROM Answer")
    fun resetTable( )

    @RawQuery(observedEntities = [Answer::class])
    fun readAnswersByTest(query: SupportSQLiteQuery): LiveData<List<Answer?>>

    @Query("SELECT DISTINCT Answer.user  FROM Answer")
    fun readUsers(): LiveData<List<String?>>

    @Query("SELECT DISTINCT Answer.testName  FROM Answer Where Answer.user=:userName")
    fun readFinishedTests(userName:String): LiveData<List<String?>>

    @Query("SELECT * FROM Answer")
    fun readAllAnswers(): LiveData<List<Answer?>>

    @Query("SELECT *  FROM TestResult Where id=:id")
    fun readTestResult(id:Int): LiveData<TestResult?>

}