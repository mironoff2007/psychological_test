package com.mironov.psychologicaltest.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.mironov.psychologicaltest.data.QuestionDao
import com.mironov.psychologicaltest.model.Question

class QuestionRepository(private val questionDao: QuestionDao) {

    val readAllData: LiveData<List<Question>> = questionDao.readAllData()

    suspend fun addQuestion(question: Question) {
        questionDao.addQuestion(question)
    }

    suspend fun updateQuestion(question: Question) {
        questionDao.updateQuestion(question)
    }

    suspend fun deleteQuestion(question: Question) {
        questionDao.deleteQuestion(question)
    }

    suspend fun deleteAllQuestions() {
        questionDao.deleteAllQuestions()
    }

    fun getQuestionById(id: Int): LiveData<Question> {
        return questionDao.getQuestionById(id)
    }

    fun getRowsCount(): LiveData<Int> {
        return questionDao.getRowsCount()
    }
}