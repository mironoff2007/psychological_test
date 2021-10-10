package com.mironov.psychologicaltest

import android.app.Application
import android.os.Build
import android.text.Layout
import androidx.annotation.Nullable
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.mironov.psychologicaltest.constants.Status
import com.mironov.psychologicaltest.data.QuestionDatabase
import com.mironov.psychologicaltest.model.Calculation
import com.mironov.psychologicaltest.model.Question
import com.mironov.psychologicaltest.repository.QuestionRepository
import com.mironov.psychologicaltest.util.PdfCreator
import java.util.*


class MainViewModel(application: Application) : AndroidViewModel(application) {
    var i=1
    var questionId = 0
    var questionMaxId = 0
    lateinit var mutableMaxCount: LiveData<Int?>

    var answersQue: ArrayDeque<String> = ArrayDeque<String>()

    var returned: Boolean = false

    var currentQuestion: Question? = null

    private lateinit var answer: String
    private lateinit var answerYes:String
    private lateinit var answerNo:String
    lateinit var answerToPrintText:String


    val viewModelStatus: MutableLiveData<Status> = MutableLiveData<Status>()
    val calculation: Calculation = Calculation()

    private lateinit var tableName: String

    private val repository: QuestionRepository

    val pdfCreator = PdfCreator()
    var path:String=""

    init {
        val questionDao = QuestionDatabase.getDatabase(
            application.applicationContext
        ).questionDao()
        repository = QuestionRepository(questionDao)
    }

    fun setAnswers(answerNo:String,answerYes:String){
        this.answerNo=answerNo
        this.answerYes=answerYes
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


    fun printResults(path: String) {
        viewModelStatus.postValue(Status.LOADING)

        this.path=path
        i=1
            pdfCreator.createpdf(path,300,500)
            printResultsLoop()
    }

    fun printResultsLoop() {
        getQuestionByIdForPrint(i)
    }

    fun getQuestionByIdForPrint(id: Int) {
        if (id <= questionMaxId) {
            repository.getQuestionById(tableName, id).observeForever(object : Observer<Question?> {
                @RequiresApi(Build.VERSION_CODES.N)
                override fun onChanged(q: Question?) {
                    currentQuestion = q
                    pdfCreator.addLine("$i. "+q?.questionText,Layout.Alignment.ALIGN_NORMAL)
                    pdfCreator.addLine(answerToPrintText+answersQue.removeLast(),Layout.Alignment.ALIGN_CENTER)
                    pdfCreator.addLine("___________________________________________\n",Layout.Alignment.ALIGN_CENTER)

                    i++
                    printResultsLoop()
                }
            })
        } else {
            pdfCreator.addLine(calculation.getResultString(),Layout.Alignment.ALIGN_NORMAL )
            pdfCreator.writePDF()
            viewModelStatus.postValue(Status.PRINTED)
        }
    }
}

