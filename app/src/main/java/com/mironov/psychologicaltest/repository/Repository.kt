package com.mironov.psychologicaltest.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Query
import androidx.sqlite.db.SimpleSQLiteQuery
import com.mironov.currency_converter.data.DataShared
import com.mironov.psychologicaltest.data.AnswerDao
import com.mironov.psychologicaltest.data.AnswerDatabase
import com.mironov.psychologicaltest.data.QuestionDao
import com.mironov.psychologicaltest.data.QuestionDatabase
import com.mironov.psychologicaltest.model.Answer
import com.mironov.psychologicaltest.model.Question

class Repository(context: Context) {

    lateinit var sharedPrefs: DataShared
    lateinit var questionDao : QuestionDao
    lateinit var answerDao : AnswerDao

    init{
        sharedPrefs=DataShared(context)

       questionDao = QuestionDatabase.getDatabase(context).questionDao()
        answerDao = AnswerDatabase.getDatabase(context).answerDao()

    }


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
        return answerDao.readAnswersByTest(SimpleSQLiteQuery("SELECT * FROM Answer WHERE testName='$testName' AND user='$userName' ORDER BY Answer.questionID"))
    }

    fun readUsers(): LiveData<List<String?>> {
        return answerDao.readUsers()
    }

    fun readFinishedTest(userName: String): LiveData<List<String?>> {
        return answerDao.readFinishedTests(userName)
    }

    fun readAllAnswers(): LiveData<List<Answer?>>{
        return  answerDao.readAllAnswers()
    }

    fun getResultFromPrefs(userName:String, tableName:String):String{
        return sharedPrefs.getResult(userName+tableName)
    }

    fun saveResultToPrefs(text:String,name:String) {
        sharedPrefs.saveResults(text, name)
    }


}