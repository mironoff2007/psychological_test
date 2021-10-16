package com.mironov.psychologicaltest

import android.app.Application
import android.os.Build
import androidx.annotation.Nullable
import androidx.annotation.RequiresApi
import androidx.lifecycle.*
import androidx.lifecycle.Observer
import com.mironov.currency_converter.data.DataShared
import com.mironov.psychologicaltest.constants.Status
import com.mironov.psychologicaltest.data.AnswerDatabase
import com.mironov.psychologicaltest.data.QuestionDatabase
import com.mironov.psychologicaltest.model.Answer
import com.mironov.psychologicaltest.model.Calculation
import com.mironov.psychologicaltest.model.Question
import com.mironov.psychologicaltest.repository.Repository
import com.mironov.psychologicaltest.util.PdfCreator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*


class MainViewModel(application: Application) : AndroidViewModel(application) {
    var i = 1
    var questionId = 0
    var questionMaxId = 0
    private lateinit var mutableMaxCount: LiveData<Int?>
    private lateinit var questionRequest: LiveData<Question?>

    var answersQue: ArrayDeque<String> = ArrayDeque<String>()

    var returned: Boolean = false

    var currentQuestion: Question? = null

    var userName: String? = null

    private lateinit var answer: String
    private lateinit var answerYes: String
    private lateinit var answerNo: String
    lateinit var answerToPrintText: String


    val viewModelStatus: MutableLiveData<Status> = MutableLiveData<Status>()

    val calculation: Calculation = Calculation()

    lateinit var sharedPrefs: DataShared

    private lateinit var tableName: String

    private val repository: Repository

    val pdfCreator = PdfCreator()
    var path: String = ""

    init {
        val questionDao = QuestionDatabase.getDatabase(
            application.applicationContext
        ).questionDao()
        val answerDao = AnswerDatabase.getDatabase(
            application.applicationContext
        ).answerDao()
        repository = Repository(questionDao, answerDao)

        viewModelScope.launch(Dispatchers.IO) {
            //repository.resetAnswerTable()//REMOVE -TODO-
        }

        sharedPrefs=DataShared(application.applicationContext)
    }

    fun setAnswers(answerNo: String, answerYes: String) {
        this.answerNo = answerNo
        this.answerYes = answerYes
    }

    fun changeTableName(tableName: String) {
        this.tableName = tableName
        mutableMaxCount = repository.getRowsCount(tableName)
        reset()
    }

    fun getQuestionById(id: Int) {
        if (id <= questionMaxId) {
            viewModelStatus.postValue(Status.LOADING)
           questionRequest= repository.getQuestionById(tableName, id)
            questionRequest.observeForever(object : Observer<Question?> {
                @RequiresApi(Build.VERSION_CODES.N)
                override fun onChanged(q: Question?) {
                    currentQuestion = q

                    if (returned) {
                        returned = false
                        calculation.addAnswer(currentQuestion, answersQue.removeFirst(), -1)

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

    @RequiresApi(Build.VERSION_CODES.N)
    fun answerYes() {
        answer = answerYes
        answersQue.push(answer)
        calculation.addAnswer(currentQuestion, answer, 1)
        getNextQuestion()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun answerNo() {
        answer = answerNo
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

    fun addResultsToDb() {
        saveResultToPrefs()
        val arr = answersQue.clone()

        viewModelStatus.postValue(Status.WRITING_RES_TO_DB)

        viewModelScope.launch(Dispatchers.IO) {
            for (i in 0 until arr.size) {
                repository.addAnswer(
                    Answer(
                        (i.toString()+tableName+userName).hashCode() ,
                        userName!!,
                        tableName,
                        i+1,
                        arr.removeLast()
                    )
                )
            }
        }
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


    fun saveResultToPrefs(){
        sharedPrefs.saveResults(calculation.getResultString(),userName+tableName)
    }

    fun getResultToPrefs(userName:String, tableName:String):String{
        return sharedPrefs.getResult(userName+tableName)
    }
}




