package com.mironov.psychologicaltest.data

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import com.mironov.psychologicaltest.model.Answer
import com.mironov.psychologicaltest.model.Question

@Dao
interface AnswerDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addAnswer(answer: Answer)

    @Update
    suspend fun updateAnswer(answer: Answer)

    @Delete
    suspend fun deleteAnswer(answer: Answer)

    @Query("DELETE FROM Answer")
    fun resetTable( )

    @RawQuery(observedEntities = [Answer::class])
    fun readAnswersByTest(query: SupportSQLiteQuery): LiveData<List<Answer?>>

}