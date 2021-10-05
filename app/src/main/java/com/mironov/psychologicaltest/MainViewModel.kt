package com.mironov.psychologicaltest

import android.app.Application
import android.os.Build
import androidx.annotation.Nullable
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.mironov.psychologicaltest.data.QuestionDatabase
import com.mironov.psychologicaltest.model.Question
import com.mironov.psychologicaltest.repository.QuestionRepository
import java.util.*

class MainViewModel(application: Application) : AndroidViewModel(application) {

    var questionId = 0
    var questionMaxId = 0
    lateinit var mutableMaxCount: LiveData<Int?>

    var answersQue: ArrayDeque<String> = ArrayDeque<String>()

    var returned: Boolean = false

    var currentQuestion: Question? = null

    lateinit var answer: String

    val viewModelStatus: MutableLiveData<Status> = MutableLiveData<Status>()
    val calculation: Calculation = Calculation()

    private lateinit var tableName: String

    private val repository: QuestionRepository

    init {
        val questionDao = QuestionDatabase.getDatabase(
            application.applicationContext
        ).questionDao()
        repository = QuestionRepository(questionDao)
    }

    fun changeTableName(tableName: String) {
        this.tableName = tableName
        mutableMaxCount = repository.getRowsCount(tableName)
        reset()
    }

    fun getQuestionById(id: Int) {
        if (id <= questionMaxId) {
            viewModelStatus.postValue(Status.LOADING)
            repository.getQuestionById(tableName, id).observeForever(object : Observer<Question?> {
                @RequiresApi(Build.VERSION_CODES.N)
                override fun onChanged(q: Question?) {
                    currentQuestion = q
                    if (returned) {
                        returned = false
                        calculation.addAnswer(currentQuestion, answersQue.removeFirst(), -1)
                    }
                    if (questionId == 1) {
                        viewModelStatus.postValue(Status.FIRST)
                    } else {
                        viewModelStatus.postValue(Status.RESPONSE)
                    }
                }
            })
        } else {
            viewModelStatus.postValue(Status.DONE)
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun answerYes() {
        answer = "yes"
        answersQue.push(answer)
        calculation.addAnswer(currentQuestion, answer, 1)
        getNextQuestion()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun answerNo() {
        answer = "no"
        answersQue.push(answer)
        calculation.addAnswer(currentQuestion, answer, 1)
        getNextQuestion()
    }

    fun reset() {
        answersQue.clear()
        calculation.resetCalc()
        questionId = 0
        questionMaxId = 0
        getNextQuestion()
    }

    fun getNextQuestion() {
        viewModelStatus.postValue(Status.LOADING)
        if (questionMaxId == 0)
            mutableMaxCount.observeForever(object : Observer<Int?> {
                override fun onChanged(@Nullable count: Int?) {
                    if (count != null) {
                        questionMaxId = count
                    }
                    questionId++
                    getQuestionById(questionId)
                    mutableMaxCount.removeObserver(this)
                }
            })
        else {
            questionId++
            getQuestionById(questionId)
        }
    }

    fun prevQuestion() {
        returned = true
        questionId -= 1
        getQuestionById(questionId)
    }
}