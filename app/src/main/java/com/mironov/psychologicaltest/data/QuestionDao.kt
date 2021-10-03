package com.mironov.psychologicaltest.data

import androidx.lifecycle.LiveData
import androidx.room.*
import com.mironov.psychologicaltest.model.Question

@Dao
interface QuestionDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addQuestion(question:Question)

    @Update
    suspend fun updateQuestion(question: Question)

    @Delete
    suspend fun deleteQuestion(question: Question)

    @Query("DELETE FROM question_table")
    suspend fun deleteAllQuestions()

    @Query("SELECT * FROM question_table ORDER BY id ASC")
    fun readAllData(): LiveData<List<Question>>

    @Query("SELECT * FROM question_table WHERE id =:id")
    fun getQuestionById(id:Int): LiveData<List<Question>>


}