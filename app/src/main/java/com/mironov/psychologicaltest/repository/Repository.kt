package com.mironov.psychologicaltest.repository

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.sqlite.db.SimpleSQLiteQuery
import com.mironov.psychologicaltest.R
import com.mironov.psychologicaltest.data.*
import com.mironov.psychologicaltest.model.Answer
import com.mironov.psychologicaltest.model.Question
import com.mironov.psychologicaltest.model.TestResult
import com.mironov.psychologicaltest.security.LoginProvider
import java.io.File

class Repository(context: Context) {

    var questionDao: QuestionDao = QuestionDatabase.getDatabase(context).questionDao()
    var answerDao: AnswerDao = AnswerDatabase.getDatabase(context).answerDao()
    var passwordShared=DataShared(context,"password")

    lateinit var answerDaoImported: AnswerDaoImport

    lateinit var path: String

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

    fun importAnswers(path: String, context: Context): LiveData<List<Answer?>>? {
        val file = File(path + "exported_db.sqlite")
        if (file.canRead()) {
            answerDaoImported = AnswerDatabaseImport.getDatabase(context, file).answerDaoImport()
        return answerDaoImported.readAllAnswers()
        }
        Toast.makeText(
            context,
            context.getString(R.string.import_failed),
            Toast.LENGTH_LONG
        ).show()
        return null
    }

    fun importResult(path: String, context: Context): LiveData<List<TestResult?>> {
        var file = File(path + context.getString(R.string.export_db_file_name))
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

    fun storePassword(password:String){
        val loginProvider=LoginProvider()
        loginProvider.getEncodedKey(password)?.let { passwordShared.saveByteArray(it,"password") }
    }

    fun getStoredPassword():ByteArray{
        return passwordShared.getByteArray("password")
    }
}