package com.mironov.psychologicaltest

import android.app.Application
import android.os.Build
import android.os.Bundle
import android.text.Layout
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.mironov.currency_converter.data.DataShared
import com.mironov.psychologicaltest.constants.ResultsStatus
import com.mironov.psychologicaltest.data.AnswerDatabase
import com.mironov.psychologicaltest.data.QuestionDatabase
import com.mironov.psychologicaltest.model.Answer
import com.mironov.psychologicaltest.model.Question
import com.mironov.psychologicaltest.repository.Repository
import com.mironov.psychologicaltest.util.PdfCreator

class ResultsViewModel(application: Application) : AndroidViewModel(application) {

    private lateinit var usersRequest: LiveData<List<String?>>
    private lateinit var finishedTestRequest: LiveData<List<String?>>
    private lateinit var answerRequest: LiveData<List<Answer?>>
    private lateinit var questionRequest: LiveData<Question?>

    private val repository: Repository

    var resultText=""

    lateinit var usersList: ArrayList<String?>
    lateinit var testsList: ArrayList<String?>

    lateinit var questionsList: ArrayList<String?>

    lateinit var answerList: ArrayList<Answer?>

    lateinit var sharedPrefs: DataShared

    val pdfCreator = PdfCreator()
    var path: String = ""
    var i:Int=0

    val resultsModelStatus: MutableLiveData<ResultsStatus> = MutableLiveData<ResultsStatus>()

    init {
        val questionDao = QuestionDatabase.getDatabase(
            application.applicationContext
        ).questionDao()
        val answerDao = AnswerDatabase.getDatabase(
            application.applicationContext
        ).answerDao()
        repository = Repository(questionDao, answerDao)

        sharedPrefs=DataShared(application.applicationContext)

    }


    fun readUsers() {
        resultsModelStatus.postValue(ResultsStatus.LOADING)
        usersRequest=repository.readUsers()
        usersRequest.observeForever(object : Observer<List<String?>> {
            override fun onChanged(list: List<String?>) {
                usersList = list as ArrayList<String?>

                usersRequest.removeObserver(this)

                resultsModelStatus.postValue(ResultsStatus.USERS_LOADED)
            }
        })
    }

    fun readFinishedTest(userName: String) {
        resultsModelStatus.postValue(ResultsStatus.LOADING)
        finishedTestRequest=repository.readFinishedTest(userName)
        finishedTestRequest.observeForever(object : Observer<List<String?>> {
            override fun onChanged(list: List<String?>) {

                testsList = list as ArrayList<String?>

                finishedTestRequest.removeObserver(this)

                resultsModelStatus.postValue(ResultsStatus.TEST_NAMES_LOADED)
            }
        })
    }



    fun getResultToPrefs(userName:String, tableName:String):String{
        resultText=sharedPrefs.getResult(userName+tableName)
        return resultText
    }

    fun printResults(path: String,selectedTable:String,selectedUser:String) {
        resultsModelStatus.postValue(ResultsStatus.LOADING)

        this.path = path
        i = 1
        pdfCreator.createpdf(path, 300, 500)
        pdfCreator.addLine(selectedUser, Layout.Alignment.ALIGN_CENTER)
        pdfCreator.addLine(
            "___________________________________________\n",
            Layout.Alignment.ALIGN_CENTER
        )
        answerRequest=repository.readAnswersByTest(selectedTable,selectedUser)
        answerRequest.observeForever(object : Observer<List<Answer?>> {
            override fun onChanged(list: List<Answer?>?) {
                answerList= list as ArrayList<Answer?>
                answerRequest.removeObserver(this)
                getQuestionByIdForPrint(i,selectedTable)
            }
        })
    }


    private fun getQuestionByIdForPrint(id: Int,selectedTable:String) {
        val maxId=answerList.size
        if (id <= maxId) {
            questionRequest=repository.getQuestionById(selectedTable, id)
                questionRequest.observeForever(object : Observer<Question?> {
                override fun onChanged(q: Question?) {

                    pdfCreator.addLine("$i. " + q?.questionText, Layout.Alignment.ALIGN_NORMAL)
                    pdfCreator.addLine(
                        "\n Ответ - "+ answerList[i-1]?.answer,
                        Layout.Alignment.ALIGN_CENTER
                    )
                    pdfCreator.addLine(
                        "___________________________________________\n",
                        Layout.Alignment.ALIGN_CENTER
                    )
                    i++
                    questionRequest.removeObserver(this)
                    getQuestionByIdForPrint(i,selectedTable)
                }
            })
        } else {

            pdfCreator.addLine(resultText, Layout.Alignment.ALIGN_NORMAL)
            pdfCreator.writePDF()
           resultsModelStatus.postValue(ResultsStatus.PRINTED)
        }
    }
}
