package com.mironov.psychologicaltest

import android.app.Application
import android.content.Context
import android.os.Build
import android.text.Layout
import androidx.annotation.RequiresApi
import androidx.lifecycle.*
import com.mironov.psychologicaltest.constants.ResultsStatus
import com.mironov.psychologicaltest.model.Answer
import com.mironov.psychologicaltest.model.Question
import com.mironov.psychologicaltest.model.TestResult
import com.mironov.psychologicaltest.repository.Repository
import com.mironov.psychologicaltest.repository.TextCreator
import com.mironov.psychologicaltest.util.PdfCreator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ResultsViewModel(application: Application) : AndroidViewModel(application) {

    private lateinit var usersRequest: LiveData<List<String?>>
    private lateinit var finishedTestRequest: LiveData<List<String?>>
    private lateinit var answerRequest: LiveData<List<Answer?>>
    private var importAnswerRequest: LiveData<List<Answer?>>? = null
    private lateinit var questionRequest: LiveData<Question?>
    private lateinit var resultRequest: LiveData<TestResult?>
    private lateinit var requestResultList: LiveData<List<TestResult?>>

    private val repository: Repository

    var resultText = ""

    lateinit var usersList: ArrayList<String?>
    lateinit var testsList: ArrayList<String?>

    lateinit var questionsList: ArrayList<String?>

    lateinit var answerList: ArrayList<Answer?>

    lateinit var textDocument: TextCreator

    val pdfCreator = PdfCreator()

    var i: Int = 0

    val resultsModelStatus: MutableLiveData<ResultsStatus> = MutableLiveData<ResultsStatus>()

    init {
        repository = Repository(application.applicationContext)
    }

    fun readUsers() {
        resultsModelStatus.postValue(ResultsStatus.LOADING)
        usersRequest = repository.readUsers()
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
        finishedTestRequest = repository.readFinishedTest(userName)
        finishedTestRequest.observeForever(object : Observer<List<String?>> {
            override fun onChanged(list: List<String?>) {

                testsList = list as ArrayList<String?>

                finishedTestRequest.removeObserver(this)

                resultsModelStatus.postValue(ResultsStatus.TEST_NAMES_LOADED)
            }
        })
    }

    fun getResult(userName: String, tableName: String) {
        resultsModelStatus.postValue(ResultsStatus.LOADING)
        resultRequest = repository.readTestResult((tableName + userName).hashCode())
        resultRequest.observeForever(object : Observer<TestResult?> {
            override fun onChanged(result: TestResult?) {
                resultText = result?.resultText.toString()
                resultRequest.removeObserver(this)
                resultsModelStatus.postValue(ResultsStatus.RESULTS_LOADED)
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    fun printResultsToPDF(path: String, selectedTable: String, selectedUser: String) {
        resultsModelStatus.postValue(ResultsStatus.LOADING)

        i = 1
        pdfCreator.createpdf(path, 300, 500)
        pdfCreator.addLine(selectedUser, Layout.Alignment.ALIGN_CENTER)
        pdfCreator.addLine(
            "___________________________________________\n",
            Layout.Alignment.ALIGN_CENTER
        )
        answerRequest = repository.readAnswersByTest(selectedTable, selectedUser)
        answerRequest.observeForever(object : Observer<List<Answer?>> {
            override fun onChanged(list: List<Answer?>?) {
                answerList = list as ArrayList<Answer?>
                answerRequest.removeObserver(this)
                getQuestionByIdForPrintPDF(i, selectedTable)
            }
        })
    }


    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun getQuestionByIdForPrintPDF(id: Int, selectedTable: String) {
        val maxId = answerList.size
        if (id <= maxId) {
            questionRequest = repository.getQuestionById(selectedTable, id)
            questionRequest.observeForever(object : Observer<Question?> {
                @RequiresApi(Build.VERSION_CODES.KITKAT)
                override fun onChanged(q: Question?) {

                    pdfCreator.addLine("$i. " + q?.questionText, Layout.Alignment.ALIGN_NORMAL)
                    pdfCreator.addLine(
                        "\n Ответ - " + answerList[i - 1]?.answer,
                        Layout.Alignment.ALIGN_CENTER
                    )
                    pdfCreator.addLine(
                        "___________________________________________\n",
                        Layout.Alignment.ALIGN_CENTER
                    )
                    i++
                    questionRequest.removeObserver(this)
                    getQuestionByIdForPrintPDF(i, selectedTable)
                }
            })
        } else {
            pdfCreator.addLine(resultText, Layout.Alignment.ALIGN_NORMAL)
            pdfCreator.writePDF()
            resultsModelStatus.postValue(ResultsStatus.PRINTED)
        }
    }

    fun printResultsToTXT(filePath: String, selectedTable: String, selectedUser: String) {

        textDocument = TextCreator()
        textDocument.createTextFile(filePath)

        i = 1
        answerRequest = repository.readAnswersByTest(selectedTable, selectedUser)
        answerRequest.observeForever(object : Observer<List<Answer?>> {
            override fun onChanged(list: List<Answer?>?) {
                answerList = list as ArrayList<Answer?>
                answerRequest.removeObserver(this)
                getQuestionByIdForPrintTXT(i, selectedTable)
            }
        })
    }

    private fun getQuestionByIdForPrintTXT(id: Int, selectedTable: String) {
        val maxId = answerList.size
        if (id <= maxId) {
            questionRequest = repository.getQuestionById(selectedTable, id)
            questionRequest.observeForever(object : Observer<Question?> {
                @RequiresApi(Build.VERSION_CODES.KITKAT)
                override fun onChanged(q: Question?) {

                    textDocument.appendText("\n$i. " + q?.questionText)
                    textDocument.appendText("\n Ответ - " + answerList[i - 1]?.answer)
                    textDocument.appendText("\n___________________________________________\n")
                    i++
                    questionRequest.removeObserver(this)
                    getQuestionByIdForPrintTXT(i, selectedTable)
                }
            })
        } else {
            textDocument.appendText(resultText)
            textDocument.closeTextFile()
            resultsModelStatus.postValue(ResultsStatus.PRINTED)
        }
    }

    fun saveDbToStorage(path: String, context: Context) {
        repository.saveDbToStorage(path, context)
    }

    fun importAnswers(path: String, context: Context) {
        importAnswerRequest = repository.importAnswers(path, context)
        importAnswerRequest?.observeForever(object : Observer<List<Answer?>> {
            override fun onChanged(list: List<Answer?>) {
                importAnswerRequest?.removeObserver(this)
                viewModelScope.launch(Dispatchers.IO) {
                    repository.answerDao.insertAllAnswers(list as List<Answer>)
                    resultsModelStatus.postValue(ResultsStatus.IMPORTED_ANSWERS_FROM_STORAGE)
                }
            }
        })
    }

    fun importResults(path: String, context: Context) {
        requestResultList = repository.importResult(path, context)
        requestResultList.observeForever(object : Observer<List<TestResult?>> {
            override fun onChanged(list: List<TestResult?>) {
                requestResultList.removeObserver(this)
                viewModelScope.launch(Dispatchers.IO) {
                    repository.answerDao.insertAllResults(list as List<TestResult>)
                    resultsModelStatus.postValue(ResultsStatus.IMPORTED_RESULTS_FROM_STORAGE)
                }
            }
        })
    }

}
