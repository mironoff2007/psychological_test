package com.mironov.psychologicaltest.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.sqlite.db.SimpleSQLiteQuery
import com.mironov.psychologicaltest.data.AnswerDao
import com.mironov.psychologicaltest.data.QuestionDao
import com.mironov.psychologicaltest.model.Answer
import com.mironov.psychologicaltest.model.Question

class Repository(private val questionDao: QuestionDao,private val answerDao: AnswerDao) {

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

    fun readAnswersByTest(testName:String, userName:String): LiveData<List<Answer?>>{
        return answerDao.readAnswersByTest(SimpleSQLiteQuery("SELECT * FROM Answer WHERE testName='$testName' AND user='$userName'"))
    }

    //SELECT DISTINCT Answer.user  FROM Answer
    fun readUsers(): LiveData<List<String?>> {
        return answerDao.readUsers()
    }

    //SELECT DISTINCT Answer.testName  FROM Answer Where Answer.user='миронов'
    fun readFinishedTest(userName: String): LiveData<List<String?>> {
        return answerDao.readFinishedTests(userName)
    }

}