package com.mironov.psychologicaltest.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.sqlite.db.SimpleSQLiteQuery
import com.mironov.psychologicaltest.data.QuestionDao
import com.mironov.psychologicaltest.model.Question

class QuestionRepository(private val questionDao: QuestionDao) {

    val readAllData: LiveData<List<Question?>> = questionDao.readAllData(SimpleSQLiteQuery("SELECT * FROM question_table ORDER BY id ASC"))


    fun getQuestionById(id: Int): LiveData<Question?> {
        return questionDao.getQuestionById(SimpleSQLiteQuery("SELECT * FROM question_table WHERE id =$id"))
    }

    fun getRowsCount(): LiveData<Int?> {
        return questionDao.getRowsCount(SimpleSQLiteQuery("SELECT COUNT(*) FROM question_table"))
    }
}