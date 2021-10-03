package com.mironov.psychologicaltest

import android.app.Application
import androidx.annotation.Nullable
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.mironov.psychologicaltest.data.QuestionDatabase
import com.mironov.psychologicaltest.model.Question
import com.mironov.psychologicaltest.repository.QuestionRepository

class MainViewModel(application: Application) : AndroidViewModel(application) {

    var questionId = 1;
    var questionMaxId=0;
    var mutableMaxCount : LiveData<Int>

    var currentQuestion:Question?=null

    val viewModelStatus: MutableLiveData<Status> = MutableLiveData<Status>()
    val calculation:Calculation= Calculation()

    private val repository: QuestionRepository

    init {
        val questionDao = QuestionDatabase.getDatabase(
            application.applicationContext
        ).questionDao()
        repository = QuestionRepository(questionDao)
        mutableMaxCount=repository.getRowsCount()
    }

    fun getQuestionById(id: Int) {
        if (id<=questionMaxId) {
            repository.getQuestionById(id).observeForever(object : Observer<Question?> {
                override fun onChanged(q: Question?) {
                    currentQuestion=q
                    viewModelStatus.postValue(Status.RESPONSE)
                }
            })
        }
        else{
            viewModelStatus.postValue(Status.DONE)
        }
    }

    fun answerYes() {
       calculation.addAnswer(currentQuestion,"yes")
        getNextQuestion()
    }

    fun answerNo() {
        calculation.addAnswer(currentQuestion,"no")
        getNextQuestion()
    }

    fun getQuestionText():String? {
        return currentQuestion?.questionText
    }

    public fun getNextQuestion() {
        viewModelStatus.postValue(Status.LOADING)
        if(questionMaxId==0)
            mutableMaxCount.observeForever(object : Observer<Int> {
            override fun onChanged(@Nullable count: Int) {
                questionMaxId=count
                getQuestionById(questionId)
                questionId++
                mutableMaxCount.removeObserver ( this )
            }
        })
        else{
            getQuestionById(questionId)
            questionId++
        }

    }
}