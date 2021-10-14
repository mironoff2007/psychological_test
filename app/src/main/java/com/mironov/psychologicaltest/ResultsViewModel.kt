package com.mironov.psychologicaltest

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import com.mironov.psychologicaltest.constants.ResultsStatus
import com.mironov.psychologicaltest.constants.Status
import com.mironov.psychologicaltest.data.AnswerDatabase
import com.mironov.psychologicaltest.data.QuestionDatabase
import com.mironov.psychologicaltest.model.Answer
import com.mironov.psychologicaltest.repository.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ResultsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: Repository

    lateinit var usersList:ArrayList<String?>
    lateinit var testsList:ArrayList<String?>

    lateinit var questionsList:ArrayList<String?>

    val resultsModelStatus: MutableLiveData<ResultsStatus> = MutableLiveData<ResultsStatus>()

    init {
        val questionDao = QuestionDatabase.getDatabase(
            application.applicationContext
        ).questionDao()
        val answerDao = AnswerDatabase.getDatabase(
            application.applicationContext
        ).answerDao()
        repository = Repository(questionDao, answerDao)

    }

    fun readAnswersByTest(userName:String,testName:String) {
        repository.readAnswersByTest(testName,userName).observeForever(object :
            Observer<List<Answer?>> {
            override fun onChanged(t: List<Answer?>) {

                t.forEach(){v-> Log.d("My_tag",v.toString())}
            }
        })
    }


    fun readUsers() {
        repository.readUsers().observeForever(object : Observer<List<String?>> {
            override fun onChanged(list: List<String?>) {
                usersList= list as ArrayList<String?>
                list.forEach(){v-> Log.d("My_tag", v.toString())}
                resultsModelStatus.postValue(ResultsStatus.USERS_LOADED)
            }
        })
    }

    fun readFinishedTest(userName: String) {
        repository.readFinishedTest(userName).observeForever(object : Observer<List<String?>> {
            override fun onChanged(list: List<String?>) {
                list.forEach(){v-> Log.d("My_tag", v.toString())}
                testsList= list as ArrayList<String?>
                resultsModelStatus.postValue(ResultsStatus.TEST_NAMES_LOADED)
            }
        })
    }
}