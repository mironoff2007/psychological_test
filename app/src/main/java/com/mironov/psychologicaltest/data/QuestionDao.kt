package com.mironov.psychologicaltest.data

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import com.mironov.psychologicaltest.model.Question

@Dao
interface QuestionDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addQuestion(question:Question)

    @Update
    suspend fun updateQuestion(question: Question)

    @Delete
    suspend fun deleteQuestion(question: Question)

    @RawQuery(observedEntities = [Question::class])//"SELECT * FROM question_table ORDER BY id ASC"
    fun readAllData(query: SupportSQLiteQuery): LiveData<List<Question?>>

    @RawQuery(observedEntities = [Question::class])//"SELECT * FROM question_table WHERE id =:id"
    fun getQuestionById(query:SupportSQLiteQuery): LiveData<Question?>

    @RawQuery(observedEntities = [Question::class])//"SELECT COUNT(*) FROM question_table"
    fun getRowsCount(query:SupportSQLiteQuery): LiveData<Int?>

}