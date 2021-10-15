package com.mironov.psychologicaltest

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import com.mironov.psychologicaltest.constants.KeysContainer
import com.mironov.psychologicaltest.constants.KeysContainer.KEY_FRAGMENT_LOGIN
import com.mironov.psychologicaltest.constants.KeysContainer.KEY_TEST_NAME
import com.mironov.psychologicaltest.constants.KeysContainer.KEY_USER_NAME
import com.mironov.psychologicaltest.constants.ResultsStatus

import kotlinx.android.synthetic.main.input_dialog_fragment.*
import java.io.File

class ResultsActivity : AppCompatActivity() {

    private var userName: String? = null
    private var testName: String? = null

    private var selectedUser: String? = null
    private var selectedTest: String? = null

    lateinit var usersList: ArrayList<String?>
    lateinit var testsList: ArrayList<String?>
    lateinit var userTestNames: ArrayList<String?>

    private  var testNameSpinner: Spinner?=null
    private  var userNameSpinner: Spinner?=null

    private lateinit var resultText: TextView

    lateinit var viewModel: ResultsViewModel

    private var rootPath = ""
    private var filePath = ""

    lateinit var createButton: Button


    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_results_presenter)

        userTestNames = arrayListOf<String?>()

        val sender = this.intent.extras?.getString(KeysContainer.KEY_SENDER)


        //IF ITS THE FRAGMENT THEN RECEIVE DATA
        if (sender.equals(KEY_FRAGMENT_LOGIN)) {
            userName = intent.getStringExtra(KEY_USER_NAME)
            testName = intent.getStringExtra(KEY_TEST_NAME)
        }

        viewModel = ViewModelProvider(this).get(ResultsViewModel::class.java)
        setupObserver()

        createButton = findViewById(R.id.createButton)
        resultText = findViewById(R.id.result_text)

        //viewModel.readAnswersByTest(userName.toString(), testName.toString())
        viewModel.readUsers()
        setupButtonsListeners()

        rootPath = applicationContext.getExternalFilesDir(null)!!.absolutePath + "/"
    }

    //Spinners
    private fun initSpinnerTables() {

        var testDbNames = resources.getStringArray(R.array.tests)
        var testsNames = resources.getStringArray(R.array.testsNames)

        var map = testDbNames.zip(testsNames).toMap()

        userTestNames.clear()

        testsList.forEach { v ->
            userTestNames.add(map[v])
        }
        testDbNames = emptyArray()
        testsNames= emptyArray()
        map= emptyMap()


        val adapter: ArrayAdapter<*> = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item, userTestNames
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)


        testNameSpinner = findViewById(R.id.spinner_tests)

        testNameSpinner!!.adapter = adapter

        testNameSpinner!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>?,
                view: View?,
                i: Int,
                l: Long
            ) {
                selectedTest = testsList[i]

                resultText.text = viewModel.getResultToPrefs(selectedUser!!, selectedTest!!)

                filePath = rootPath + selectedUser + "-" + selectedTest + ".pdf"
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun initSpinnerUsers() {

        val adapter: ArrayAdapter<*> = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            usersList
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        userNameSpinner = findViewById(R.id.spinner_users)

        userNameSpinner!!.adapter = adapter

        userNameSpinner!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>?,
                view: View?,
                i: Int,
                l: Long
            ) {
                selectedUser = usersList[i]
                viewModel.readFinishedTest(selectedUser.toString())

            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }
    }

    fun setupObserver() {
        viewModel.resultsModelStatus.observe(this) {
            when (it) {
                ResultsStatus.TEST_NAMES_LOADED -> {
                    testsList = viewModel.testsList

                    testNameSpinner?.adapter=null
                    testNameSpinner=null
                    initSpinnerTables()
                }
                ResultsStatus.USERS_LOADED -> {
                    usersList = viewModel.usersList

                    userNameSpinner?.adapter=null
                    userNameSpinner=null
                    initSpinnerUsers()

                    userNameSpinner!!.setSelection(usersList.indexOf(userName))

                }
                ResultsStatus.PRINTED -> {
                   // progressBar.visibility = View.GONE
                    Toast.makeText(
                        applicationContext,
                        "PRINTED to - " + filePath,
                        Toast.LENGTH_LONG
                    ).show()
                    Log.d("My_tag", "PRINTED to - " + filePath)
                    viewPdfFile(filePath)
                }
            }
        }
    }
    private fun setupButtonsListeners() {


        createButton.setOnClickListener { v: View? ->
            filePath = rootPath + userName + "-" + testName + ".pdf"
            viewModel.printResults(filePath,selectedTest.toString(),selectedUser.toString())
        }


    }

    private fun viewPdfFile(path: String?) {
        val file = File(path)
        val intent = Intent(
            Intent.ACTION_VIEW,
            FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", file)
        )
        intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION

        startActivity(intent)
    }
}