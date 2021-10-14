package com.mironov.psychologicaltest

import android.app.Application
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.mironov.currency_converter.data.DataShared
import com.mironov.psychologicaltest.constants.ResultsStatus
import com.mironov.psychologicaltest.data.AnswerDatabase
import com.mironov.psychologicaltest.data.QuestionDatabase
import com.mironov.psychologicaltest.model.Answer
import com.mironov.psychologicaltest.model.Question
import com.mironov.psychologicaltest.repository.Repository

class ResultsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: Repository

    lateinit var usersList: ArrayList<String?>
    lateinit var testsList: ArrayList<String?>

    lateinit var questionsList: ArrayList<String?>

    lateinit var sharedPrefs: DataShared

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

    fun readAnswersByTest(userName: String, testName: String) {
        repository.readAnswersByTest(testName, userName).observeForever(object :
            Observer<List<Answer?>> {
            override fun onChanged(t: List<Answer?>) {

                t.forEach() { v -> Log.d("My_tag", v.toString()) }
            }
        })
    }


    fun readUsers() {
        repository.readUsers().observeForever(object : Observer<List<String?>> {
            override fun onChanged(list: List<String?>) {
                usersList = list as ArrayList<String?>
                list.forEach() { v -> Log.d("My_tag", v.toString()) }
                resultsModelStatus.postValue(ResultsStatus.USERS_LOADED)
            }
        })
    }

    fun readFinishedTest(userName: String) {
        repository.readFinishedTest(userName).observeForever(object : Observer<List<String?>> {
            override fun onChanged(list: List<String?>) {
                list.forEach() { v -> Log.d("My_tag", v.toString()) }
                testsList = list as ArrayList<String?>
                resultsModelStatus.postValue(ResultsStatus.TEST_NAMES_LOADED)
            }
        })
    }

    fun printAnswers(tableName: String, userName: String) {
        repository.getRowsCount(tableName).observeForever(object : Observer<Int?> {
            override fun onChanged(questionsCount: Int?) {
                for (i in 1..questionsCount!!) {
                    repository.getQuestionById(tableName, i)
                        .observeForever(object : Observer<Question?> {
                            @RequiresApi(Build.VERSION_CODES.N)
                            override fun onChanged(q: Question?) {

                            }
                        })
                }
            }
        })
    }

    fun getResultToPrefs(userName:String, tableName:String):String{
        return sharedPrefs.getResult(userName+tableName)
    }
}