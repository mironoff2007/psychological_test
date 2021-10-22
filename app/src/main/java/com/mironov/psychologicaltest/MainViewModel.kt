package com.mironov.psychologicaltest

import android.app.Application
import androidx.annotation.Nullable
import androidx.lifecycle.*
import androidx.lifecycle.Observer
import com.mironov.psychologicaltest.data.DataShared
import com.mironov.psychologicaltest.constants.Status
import com.mironov.psychologicaltest.model.Answer
import com.mironov.psychologicaltest.model.Calculation
import com.mironov.psychologicaltest.model.Question
import com.mironov.psychologicaltest.repository.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*


class MainViewModel(application: Application) : AndroidViewModel(application) {

    var questionId = 0
    var questionMaxId = 0
    private lateinit var mutableMaxCount: LiveData<Int?>
    private lateinit var questionRequest: LiveData<Question?>

    var answersQue: ArrayDeque<String> = ArrayDeque<String>()

    var returned: Boolean = false

    var currentQuestion: Question? = null

    var userName: String? = null

    lateinit var answerToPrintText: String

    val viewModelStatus: MutableLiveData<Status> = MutableLiveData<Status>()

    val calculation: Calculation = Calculation()

    private  var sharedPrefs: DataShared

    private lateinit var tableName: String

    private val repository: Repository

    private val complexTestsList = arrayListOf("family_parenting_strategies")
    var testIsComplex: Boolean = false

    init {
        repository = Repository(application.applicationContext)
        sharedPrefs = DataShared(application.applicationContext)
    }


    fun changeTableName(tableName: String) {
        this.tableName = tableName
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

                    if (returned) {
                        returned = false
                        val answer = answersQue.removeFirst()
                        answer(
                            currentQuestion?.subQuestionText?.split(";")!!.indexOf(answer),
                            answer,
                            -1
                        )
                    }
                    questionRequest.removeObserver(this)
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


    fun addAnswer(answerId: Int, answer: String, inc: Int){
        answer(answerId, answer, inc)
        answersQue.push(answer)
        getNextQuestion()
    }


    fun answer(answerId: Int, answer: String, inc: Int) {
        //Type of test
        if (testIsComplex) {
            //Compex test
            val list = currentQuestion?.type!!.split(";")
            calculation.addAnswer(
                currentQuestion,
                list[answerId], currentQuestion?.answer!!, inc
            )
        } else {
            //YES/NO test
            calculation.addAnswer(currentQuestion, currentQuestion?.type!!, answer, inc)
        }
    }


    private fun reset() {
        answersQue.clear()
        calculation.resetCalc()
        questionId = 0
        questionMaxId = 0
        getNextQuestion()
    }

    fun addResultsToDb() {
        saveResultToPrefs()
        val arr = answersQue.clone()

        viewModelStatus.postValue(Status.WRITING_RES_TO_DB)

        viewModelScope.launch(Dispatchers.IO) {
            for (i in 0 until arr.size) {
                repository.addAnswer(
                    Answer(
                        (i.toString() + tableName + userName).hashCode(),
                        userName!!,
                        tableName,
                        i + 1,
                        arr.removeLast()
                    )
                )
            }
        }
    }

    private fun getNextQuestion() {
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


    fun saveResultToPrefs() {
        repository.saveResultToPrefs(calculation.getResultString(), userName + tableName)
    }

    fun getResultFromPrefs(userName:String, tableName:String):String{
        return repository.getResultFromPrefs(userName, tableName)
    }
}




