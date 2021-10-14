package com.mironov.psychologicaltest

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.mironov.psychologicaltest.constants.KeysContainer
import com.mironov.psychologicaltest.constants.KeysContainer.KEY_NAME_MAIN_ACTIVITY
import com.mironov.psychologicaltest.constants.KeysContainer.KEY_TEST_NAME
import com.mironov.psychologicaltest.constants.KeysContainer.KEY_USER_NAME
import com.mironov.psychologicaltest.constants.ResultsStatus
import com.mironov.psychologicaltest.constants.Status

class ResultsActivity : AppCompatActivity() {

    private var userName: String? = null
    private var testName: String? = null

    private var selectedUser: String? = null
    private var selectedTest: String? = null

    lateinit var usersList:ArrayList<String?>
    lateinit var testsList:ArrayList<String?>
    lateinit var userTestNames:ArrayList<String?>

    private lateinit var testNameSpinner: Spinner
    private lateinit var userNameSpinner: Spinner

    private lateinit var resultText: TextView

    lateinit var viewModel: ResultsViewModel

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_results_presenter)

        userTestNames=arrayListOf<String?>()

        val sender = this.intent.extras?.getString(KeysContainer.KEY_SENDER)


        //IF ITS THE FRAGMENT THEN RECEIVE DATA
        if (sender.equals(KEY_NAME_MAIN_ACTIVITY)) {
            userName = intent.getStringExtra(KEY_USER_NAME)
            testName = intent.getStringExtra(KEY_TEST_NAME)
        }



        viewModel = ViewModelProvider(this).get(ResultsViewModel::class.java)
        setupObserver()

        userNameSpinner = findViewById(R.id.spinner_users)
        testNameSpinner = findViewById(R.id.spinner_tests)

        resultText = findViewById(R.id.result_text)

        //viewModel.readAnswersByTest(userName.toString(), testName.toString())
        viewModel.readUsers()



    }

    //Spinners
    private fun initSpinnerTables() {

        val testDbNames = resources.getStringArray(R.array.tests)
        val testsNames = resources.getStringArray(R.array.testsNames)

        val map=testDbNames.zip(testsNames).toMap()

        userTestNames.clear()

        testsList.forEach { v->
            userTestNames.add(map[v])
        }

        val adapter: ArrayAdapter<*> = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,userTestNames
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)


        testNameSpinner.adapter = adapter

        testNameSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>?,
                view: View?,
                i: Int,
                l: Long
            ) {
                selectedTest= testsList[i]

                resultText.text=viewModel.getResultToPrefs(selectedUser!!,selectedTest!!)


                Log.d("My_tag","Selected table ="+selectedTest)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }
    }

    private fun initSpinnerUsers() {


        val adapter: ArrayAdapter<*> = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            usersList
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)


        userNameSpinner.adapter = adapter

        userNameSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
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
                    testsList=viewModel.testsList
                    initSpinnerTables()
                }
                ResultsStatus.USERS_LOADED -> {
                    usersList=viewModel.usersList
                    initSpinnerUsers()
                    userNameSpinner.setSelection(usersList.indexOf(userName))

                }

            }
        }
    }


}