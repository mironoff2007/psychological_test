package com.mironov.psychologicaltest.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.sqlite.db.SimpleSQLiteQuery
import com.mironov.psychologicaltest.data.*
import com.mironov.psychologicaltest.model.Answer
import com.mironov.psychologicaltest.model.Question
import com.mironov.psychologicaltest.model.TestResult
import java.io.File

class Repository(context: Context) {

    lateinit var sharedPrefs: DataShared
    lateinit var questionDao: QuestionDao
    lateinit var answerDao: AnswerDao
    lateinit var answerDaoImported: AnswerDaoImport

    lateinit var path: String

    init {
        sharedPrefs = DataShared(context)
        questionDao = QuestionDatabase.getDatabase(context).questionDao()
        answerDao = AnswerDatabase.getDatabase(context).answerDao()
    }


    fun getQuestionById(tableName: String, id: Int): LiveData<Question?> {
        return questionDao.getQuestionById(SimpleSQLiteQuery("SELECT * FROM $tableName WHERE id =$id"))
    }

    fun getRowsCount(tableName: String): LiveData<Int?> {
        return questionDao.getRowsCount(SimpleSQLiteQuery("SELECT COUNT(*) FROM $tableName"))
    }

    fun addAnswer(answer: Answer) {
        answerDao.addAnswer(answer)
    }

    fun resetAnswerTable() {
        answerDao.resetTable()
    }

    fun readAnswersByTest(testName: String, userName: String): LiveData<List<Answer?>> {
        return answerDao.readAnswersByTest(SimpleSQLiteQuery("SELECT * FROM Answer WHERE testName='$testName' AND user='$userName' ORDER BY Answer.questionID"))
    }

    fun readUsers(): LiveData<List<String?>> {
        return answerDao.readUsers()
    }

    fun readFinishedTest(userName: String): LiveData<List<String?>> {
        return answerDao.readFinishedTests(userName)
    }

    fun readAllAnswers(): LiveData<List<Answer?>> {
        return answerDao.readAllAnswers()
    }

    fun saveDbToStorage(path: String, context: Context) {
        val dbSaveRead = DbSaveRead()
        dbSaveRead.exportDatabase(path, context)
    }

    fun importAnswers(path: String, context: Context): LiveData<List<Answer?>> {
        var file = File(path + "exported_db.sqlite")
        if (file.canRead()) {
            answerDaoImported = AnswerDatabaseImport.getDatabase(context, file).answerDaoImport()
        }
        return answerDaoImported.readAllAnswers()
    }

    fun importResult(path: String, context: Context): LiveData<List<TestResult?>> {
        var file = File(path + "exported_db.sqlite")
        if (file.canRead()) {
            answerDaoImported = AnswerDatabaseImport.getDatabase(context, file).answerDaoImport()
        }
        return answerDaoImported.readAllTestResult()
    }

    fun addTestResult(testResult:TestResult){
        answerDao.addTestResult(testResult)
    }

    fun readTestResult(id:Int):LiveData<TestResult?> {
        return answerDao.readTestResult(id)
    }
}