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
    //fun addAnswer(answer: SimpleSQLiteQuery)

    @Update
    suspend fun updateAnswer(answer: Answer)

    @Delete
    suspend fun deleteAnswer(answer: Answer)

    @Query("DELETE FROM Answer")
    fun resetTable( )

    @RawQuery(observedEntities = [Answer::class])//"SELECT * FROM question_table ORDER BY id ASC"
    fun readAllData(query: SupportSQLiteQuery): LiveData<List<Question?>>

    @RawQuery(observedEntities = [Answer::class])//"SELECT * FROM question_table WHERE id =:id"
    fun getQuestionById(query:SupportSQLiteQuery): LiveData<Question?>

    @RawQuery(observedEntities = [Answer::class])//"SELECT COUNT(*) FROM question_table"
    fun getRowsCount(query:SupportSQLiteQuery): LiveData<Int?>

}