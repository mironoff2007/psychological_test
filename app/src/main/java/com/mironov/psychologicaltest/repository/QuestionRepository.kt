package com.mironov.psychologicaltest.repository

import androidx.lifecycle.LiveData
import com.mironov.psychologicaltest.data.QuestionDao
import com.mironov.psychologicaltest.model.Question

class QuestionRepository(private val questionDao: QuestionDao) {

    val readAllData: LiveData<List<Question>> = questionDao.readAllData()

    suspend fun addUser(question: Question){
        questionDao.addUser(question)
    }

    suspend fun updateUser(question: Question){
        questionDao.updateUser(question)
    }

    suspend fun deleteUser(question: Question){
        questionDao.deleteUser(question)
    }

    suspend fun deleteAllUsers(){
        questionDao.deleteAllUsers()
    }

}