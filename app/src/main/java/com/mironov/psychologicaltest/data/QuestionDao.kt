package com.mironov.psychologicaltest.data

import androidx.lifecycle.LiveData
import androidx.room.*
import com.mironov.psychologicaltest.model.Question


@Dao
interface QuestionDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addUser(question:Question)

    @Update
    suspend fun updateUser(question: Question)

    @Delete
    suspend fun deleteUser(question: Question)

    @Query("DELETE FROM question_table")
    suspend fun deleteAllUsers()

    @Query("SELECT * FROM question_table ORDER BY id ASC")
    fun readAllData(): LiveData<List<Question>>

}