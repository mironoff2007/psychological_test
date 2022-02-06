package com.mironov.psychologicaltest

import android.app.Application
import androidx.annotation.Nullable
import androidx.lifecycle.*
import androidx.lifecycle.Observer
import com.mironov.psychologicaltest.constants.Status
import com.mironov.psychologicaltest.model.Answer
import com.mironov.psychologicaltest.model.Calculation
import com.mironov.psychologicaltest.model.Question
import com.mironov.psychologicaltest.model.TestResult
import com.mironov.psychologicaltest.repository.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*


class MainViewModel(application: Application) : AndroidViewModel(application) {

    var questionId = 0
    var questionMaxId = 0
    private lateinit var mutableMaxCount: LiveData<Int?>
    private lateinit var questionRequest: LiveData<Question?>

    var answersQue: ArrayDeque<Int> = ArrayDeque<Int>()

    var returned: Boolean = false

    var currentQuestion: Question? = null

    var userName: String? = null

    lateinit var answerToPrintText: String

    val viewModelStatus: MutableLiveData<Status> = MutableLiveData<Status>()

    private val calculation: Calculation = Calculation()

    private lateinit var tableName: String
    private var testId: Int = 0

    private val repository: Repository = Repository(application.applicationContext)

    //List of complex tests
    private val complexTestsList =
        arrayListOf(*application.resources.getStringArray(R.array.complexTests))

    private var testIsComplex: Boolean = false

    fun changeTableName(tableName: String, testId: Int) {
        this.tableName = tableName
        this.testId = testId
        mutableMaxCount = repository.getRowsCount(tableName)
        //Change test type to complex
        testIsComplex = complexTestsList.contains(tableName)
        reset()
    }

    fun getQuestionById(id: Int) {
        if (id <= questionMaxId) {
            viewModelStatus.postValue(Status.LOADING)
            questionRequest = repository.getQuestionById(tableName, id)
            questionRequest.observeForever(object : Observer<Question?> {
                override fun onChanged(q: Question?) {
                    currentQuestion = q
                    //If user moved back
                    if (returned) {
                        returned = false
                        answersQue.removeFirst()
                    }
                    questionRequest.removeObserver(this)
                    //If user returned to first question
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

    fun addAnswer(answerId: Int) {
        answersQue.push(answerId)
        getNextQuestion()
    }


    private fun reset() {
        answersQue.clear()
        calculation.resetCalc()
        questionId = 0
        questionMaxId = 0
        getNextQuestion()
    }

    fun addResultsToDb() {
        viewModelStatus.postValue(Status.LOADING)
        val arr = answersQue.clone()

        viewModelScope.launch(Dispatchers.IO) {
            for (i in 0 until arr.size) {
                repository.addAnswer(
                    Answer(
                        id = (i.toString() + tableName).hashCode(),
                        testId = testId,
                        questionId = i + 1,
                        arr.removeLast()
                    )
                )
                repository.addTestResult(
                    TestResult(
                        (tableName + userName).hashCode(),
                        calculation.getResultString()
                    )
                )
                viewModelStatus.postValue(Status.RESULTS_SAVED)
            }
        }
    }

    private fun getNextQuestion() {
        viewModelStatus.postValue(Status.LOADING)
        if (questionMaxId == 0)
        //Question is first
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




