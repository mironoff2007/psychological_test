package com.mironov.psychologicaltest.repository

import androidx.lifecycle.LiveData
import androidx.sqlite.db.SimpleSQLiteQuery
import com.mironov.psychologicaltest.data.AnswerDao
import com.mironov.psychologicaltest.data.QuestionDao
import com.mironov.psychologicaltest.model.Answer
import com.mironov.psychologicaltest.model.Question

class QuestionRepository(private val questionDao: QuestionDao,private val answerDao: AnswerDao) {

    fun getQuestionById(tableName:String, id: Int): LiveData<Question?> {
        return questionDao.getQuestionById(SimpleSQLiteQuery("SELECT * FROM $tableName WHERE id =$id"))
    }

    fun getRowsCount(tableName:String): LiveData<Int?> {
        return questionDao.getRowsCount(SimpleSQLiteQuery("SELECT COUNT(*) FROM $tableName"))
    }

    fun addAnswer( answer:Answer){
        answerDao.addAnswer(answer)
    }

    fun resetAnswerTable(){
        answerDao.resetTable()
    }


}