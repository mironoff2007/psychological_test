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

import java.util.ArrayDeque

class MainViewModel(application: Application) : AndroidViewModel(application) {

    var questionId = 0;
    var questionMaxId=0;
    var mutableMaxCount : LiveData<Int?>

    var answersQue:ArrayDeque<String>

    var returned:Boolean=false

    var currentQuestion:Question?=null

    lateinit var answer:String

    val viewModelStatus: MutableLiveData<Status> = MutableLiveData<Status>()
    val calculation:Calculation= Calculation()

    private val repository: QuestionRepository

    init {
        answersQue=ArrayDeque<String> ()
        val questionDao = QuestionDatabase.getDatabase(
            application.applicationContext
        ).questionDao()
        repository = QuestionRepository(questionDao)

        mutableMaxCount=repository.getRowsCount()

    }

    fun getQuestionById(id: Int) {
        if (id<=questionMaxId) {
            viewModelStatus.postValue(Status.LOADING)
            repository.getQuestionById(id).observeForever(object : Observer<Question?> {
                override fun onChanged(q: Question?) {
                    currentQuestion=q
                    if(returned){
                        returned=false
                        calculation.addAnswer(currentQuestion,answersQue.removeLast(),-1)
                    }
                    if(questionId==1){
                        viewModelStatus.postValue(Status.FIRST)
                    }
                    else {
                        viewModelStatus.postValue(Status.RESPONSE)
                    }
                }
            })
        }
        else{
            viewModelStatus.postValue(Status.DONE)
        }
    }

    fun answerYes() {
        answer="yes"
        answersQue.push(answer)
        calculation.addAnswer(currentQuestion,answer,1)
        getNextQuestion()
    }

    fun answerNo() {
        answer="no"
        answersQue.push(answer)
        calculation.addAnswer(currentQuestion,answer,1)
        getNextQuestion()
    }

    fun reset(){
        answersQue.clear()
        calculation.resetCalc()
        questionId = 0
        questionMaxId=0
        getNextQuestion()
    }

    fun getQuestionText():String? {
        return currentQuestion?.questionText
    }

    public fun getNextQuestion() {
        viewModelStatus.postValue(Status.LOADING)
        if (questionMaxId == 0)
            mutableMaxCount?.observeForever(object : Observer<Int?> {
                override fun onChanged(@Nullable count: Int?) {
                    if (count != null) {
                        questionMaxId = count
                    }
                    questionId++
                    getQuestionById(questionId)
                    mutableMaxCount?.removeObserver(this)
                }
            })
        else {
            questionId++
            getQuestionById(questionId)
        }
    }

    fun prevQuestion() {
        returned=true
        questionId -= 1
        getQuestionById(questionId)
    }
}