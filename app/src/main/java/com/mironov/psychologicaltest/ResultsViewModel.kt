package com.mironov.psychologicaltest

import android.app.Application
import android.content.Context
import android.os.Build
import android.text.Layout
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.mironov.psychologicaltest.constants.ResultsStatus
import com.mironov.psychologicaltest.model.Answer
import com.mironov.psychologicaltest.model.Question
import com.mironov.psychologicaltest.repository.DbSaveRead
import com.mironov.psychologicaltest.repository.Repository
import com.mironov.psychologicaltest.repository.TextCreator
import com.mironov.psychologicaltest.util.PdfCreator
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ResultsViewModel(application: Application) : AndroidViewModel(application) {

    private lateinit var usersRequest: LiveData<List<String?>>
    private lateinit var finishedTestRequest: LiveData<List<String?>>
    private lateinit var answerRequest: LiveData<List<Answer?>>
    private lateinit var questionRequest: LiveData<Question?>

    private val repository: Repository

    var resultText=""

    lateinit var usersList: ArrayList<String?>
    lateinit var testsList: ArrayList<String?>

    lateinit var questionsList: ArrayList<String?>

    lateinit var answerList: ArrayList<Answer?>


    lateinit var textDocument:TextCreator

    val pdfCreator = PdfCreator()


    var path: String = ""
    var i:Int=0

    val resultsModelStatus: MutableLiveData<ResultsStatus> = MutableLiveData<ResultsStatus>()

    init { repository = Repository(application.applicationContext) }


    fun readUsers() {
        resultsModelStatus.postValue(ResultsStatus.LOADING)
        usersRequest=repository.readUsers()
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
        finishedTestRequest=repository.readFinishedTest(userName)
        finishedTestRequest.observeForever(object : Observer<List<String?>> {
            override fun onChanged(list: List<String?>) {

                testsList = list as ArrayList<String?>

                finishedTestRequest.removeObserver(this)

                resultsModelStatus.postValue(ResultsStatus.TEST_NAMES_LOADED)
            }
        })
    }



    fun getResultFromPrefs(userName:String, tableName:String):String{
        return repository.getResultFromPrefs(userName, tableName)
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    fun printResultsToPDF(path: String, selectedTable:String, selectedUser:String) {
        resultsModelStatus.postValue(ResultsStatus.LOADING)

        this.path = path
        i = 1
        pdfCreator.createpdf(path, 300, 500)
        pdfCreator.addLine(selectedUser, Layout.Alignment.ALIGN_CENTER)
        pdfCreator.addLine(
            "___________________________________________\n",
            Layout.Alignment.ALIGN_CENTER
        )
        answerRequest=repository.readAnswersByTest(selectedTable,selectedUser)
        answerRequest.observeForever(object : Observer<List<Answer?>> {
            override fun onChanged(list: List<Answer?>?) {
                answerList= list as ArrayList<Answer?>
                answerRequest.removeObserver(this)
                getQuestionByIdForPrintPDF(i,selectedTable)
            }
        })
    }


    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun getQuestionByIdForPrintPDF(id: Int, selectedTable:String) {
        val maxId=answerList.size
        if (id <= maxId) {
            questionRequest=repository.getQuestionById(selectedTable, id)
                questionRequest.observeForever(object : Observer<Question?> {
                @RequiresApi(Build.VERSION_CODES.KITKAT)
                override fun onChanged(q: Question?) {

                    pdfCreator.addLine("$i. " + q?.questionText, Layout.Alignment.ALIGN_NORMAL)
                    pdfCreator.addLine(
                        "\n Ответ - "+ answerList[i-1]?.answer,
                        Layout.Alignment.ALIGN_CENTER
                    )
                    pdfCreator.addLine(
                        "___________________________________________\n",
                        Layout.Alignment.ALIGN_CENTER
                    )
                    i++
                    questionRequest.removeObserver(this)
                    getQuestionByIdForPrintPDF(i,selectedTable)
                }
            })
        } else {

            pdfCreator.addLine(resultText, Layout.Alignment.ALIGN_NORMAL)
            pdfCreator.writePDF()
           resultsModelStatus.postValue(ResultsStatus.PRINTED)
        }
    }

    fun printResultsToTXT(filePath: String, selectedTable:String, selectedUser:String) {

        textDocument=TextCreator()
        textDocument.createTextFile(filePath)

        this.path = path
        i = 1
        answerRequest=repository.readAnswersByTest(selectedTable,selectedUser)
        answerRequest.observeForever(object : Observer<List<Answer?>> {
            override fun onChanged(list: List<Answer?>?) {
                answerList= list as ArrayList<Answer?>
                answerRequest.removeObserver(this)
                getQuestionByIdForPrintTXT(i,selectedTable)
            }
        })
    }
    private fun getQuestionByIdForPrintTXT(id: Int, selectedTable:String) {
        val maxId=answerList.size
        if (id <= maxId) {
            questionRequest=repository.getQuestionById(selectedTable, id)
            questionRequest.observeForever(object : Observer<Question?> {
                @RequiresApi(Build.VERSION_CODES.KITKAT)
                override fun onChanged(q: Question?) {

                    textDocument.appendText("\n$i. " + q?.questionText)
                    textDocument.appendText("\n Ответ - "+ answerList[i-1]?.answer,)
                    textDocument.appendText("\n___________________________________________\n",)
                    i++
                    questionRequest.removeObserver(this)
                    getQuestionByIdForPrintTXT(i,selectedTable)
                }
            })
        } else {
            textDocument.appendText(resultText,)
            textDocument.closeTextFile()
            resultsModelStatus.postValue(ResultsStatus.PRINTED)
        }
    }

    fun saveDbToStorage(path:String,context: Context){
        repository.saveDbToStorage(path,context)
    }

    fun readDbFromStorage(path:String,context: Context){
        repository.readDbFromStorage(path,context).observeForever(object : Observer<List<Answer?>> {
            override fun onChanged(list: List<Answer?>) {
                GlobalScope.launch  {
                    repository.answerDao.insertAll(list as List<Answer>)
                    resultsModelStatus.postValue(ResultsStatus.IMPORTED_FROM_STORAGE)
                }
            }
        })
    }
}
