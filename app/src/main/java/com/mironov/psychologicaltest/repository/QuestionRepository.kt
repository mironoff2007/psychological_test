package com.mironov.psychologicaltest.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.sqlite.db.SimpleSQLiteQuery
import com.mironov.psychologicaltest.data.QuestionDao
import com.mironov.psychologicaltest.model.Question

class QuestionRepository(private val questionDao: QuestionDao) {

    fun getQuestionById(tableName:String, id: Int): LiveData<Question?> {
        return questionDao.getQuestionById(SimpleSQLiteQuery("SELECT * FROM $tableName WHERE id =$id"))
    }

    fun getRowsCount(tableName:String): LiveData<Int?> {
        return questionDao.getRowsCount(SimpleSQLiteQuery("SELECT COUNT(*) FROM $tableName"))
    }
}