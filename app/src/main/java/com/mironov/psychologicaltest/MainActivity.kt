package com.mironov.psychologicaltest

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import android.widget.RadioGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.mironov.psychologicaltest.constants.KeysContainer.KEY_FRAGMENT_LOGIN
import com.mironov.psychologicaltest.constants.KeysContainer.KEY_FRAGMENT_USER_DATA
import com.mironov.psychologicaltest.constants.KeysContainer.KEY_NAME_FRAGMENT
import com.mironov.psychologicaltest.constants.KeysContainer.KEY_SENDER
import com.mironov.psychologicaltest.constants.KeysContainer.KEY_TEST_ID
import com.mironov.psychologicaltest.constants.KeysContainer.KEY_TEST_NAME
import com.mironov.psychologicaltest.constants.KeysContainer.KEY_USER_NAME
import com.mironov.psychologicaltest.constants.Status
import com.mironov.psychologicaltest.ui.InputUserDataFragment
import com.mironov.psychologicaltest.ui.LoginFragment


class MainActivity : AppCompatActivity() {

    var inputUserDataFragment: InputUserDataFragment? = null
    var loginFragment: LoginFragment? = null

    lateinit var viewModel: MainViewModel

    lateinit var yesButton: Button
    lateinit var prevButton: Button

    //RadioButton Group
    lateinit var radioGroup: RadioGroup
    lateinit var radioButtonList: ArrayList<RadioButton>
    lateinit var radioButton1: RadioButton
    lateinit var radioButton2: RadioButton
    lateinit var radioButton3: RadioButton
    lateinit var radioButton4: RadioButton
    lateinit var subQuestionList: ArrayList<String>
    var  radioButtonId:Int=0

    private lateinit var progressBar: ProgressBar
    private lateinit var tableNameSpinner: Spinner
    private lateinit var questionText: TextView

    lateinit var tableName: String
    var answer: String=""

    private var rootPath = ""
    private var filePath = ""
    private var testName = ""

    private var userName: String? = null
    private var selectedTest: String? = null

    private var selectedTableId = 0
    private var questionsCount = 0

    // Storage Permissions
    private val REQUEST_EXTERNAL_STORAGE = 1
    private val PERMISSIONS_STORAGE = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                loginFragment = LoginFragment()
                var bundle = Bundle()
                bundle.putString(KEY_USER_NAME, userName)
                bundle.putString(KEY_TEST_NAME, testName)
                loginFragment!!.arguments = bundle
                loginFragment!!.show(supportFragmentManager, KEY_FRAGMENT_LOGIN)
                loginFragment = null
                true
            }
            R.id.new_user -> {
                userName = null
                requestNewUserName()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {

        //Debug.waitForDebugger()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        viewModel.answerToPrintText = getString(R.string.print_answer)

        setupObserver()
        initViews()
        setupListeners()
        initSpinnerAdapters()
        requestPermissions()

        rootPath = applicationContext.getExternalFilesDir(null)!!.absolutePath + "/"
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun requestPermissions() {
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
            checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                PERMISSIONS_STORAGE,
                REQUEST_EXTERNAL_STORAGE
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        inputUserDataFragment = null
        loginFragment = null
        tableNameSpinner.onItemSelectedListener = null
    }

    override fun onContentChanged() {
        super.onContentChanged()
    }

    override fun onResume() {
        super.onResume()
        receiveData()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState?.run {
            val i = tableNameSpinner.selectedItemId.toInt()
            putInt(KEY_TEST_ID, i)
            putString(KEY_TEST_NAME, testName)
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        val i = savedInstanceState?.getInt(KEY_TEST_ID)
        tableNameSpinner.setSelection(i)
        testName = savedInstanceState?.getString(KEY_TEST_NAME).toString()
    }

    private fun receiveData() {
        //RECEIVE DATA VIA INTENT

        //DETERMINE WHO STARTED THIS ACTIVITY
        val sender = this.intent.extras?.getString(KEY_SENDER)

        var testId = 0
        //IF ITS THE FRAGMENT THEN RECEIVE DATA
        if (sender.equals(KEY_NAME_FRAGMENT)) {
            val name = intent.getStringExtra(KEY_NAME_FRAGMENT)
            testId = intent.getIntExtra(KEY_TEST_ID, 0)
            userName = name
            viewModel.userName = name
        }
        if (userName == null || userName?.length == 0) {
            tableNameSpinner.setSelection(0)
        } else {
            tableNameSpinner.setSelection(testId)
        }
    }

    private fun initViews() {
        //Buttons
        yesButton = findViewById(R.id.yesButton)
        prevButton = findViewById(R.id.prevButton)
        yesButton.isEnabled = false
        prevButton.isEnabled = false

        //TextView
        questionText = findViewById(R.id.questionText)
        //Spinner
        tableNameSpinner = findViewById(R.id.tableNameSpinner)

        //ProgressBar
        progressBar = findViewById(R.id.progressBar)
        progressBar.visibility = View.INVISIBLE

        //RadioButtons
        radioGroup = findViewById(R.id.radioButton_group)
        radioButton1 = findViewById(R.id.radio_1)
        radioButton2 = findViewById(R.id.radio_2)
        radioButton3 = findViewById(R.id.radio_3)
        radioButton4 = findViewById(R.id.radio_4)
        radioButtonList = arrayListOf(radioButton1, radioButton2, radioButton3, radioButton4)
        radioButtonList.forEach { v ->
            v.isEnabled = false
            v.visibility = View.GONE
            v.text = ""
        }
        yesButton.isEnabled=false
    }


    //Buttons Listeners
    @RequiresApi(Build.VERSION_CODES.N)
    private fun setupListeners() {
        yesButton.setOnClickListener { v: View? ->
            viewModel.addAnswer(radioButtonId,subQuestionList[radioButtonId],1)
            radioGroup.clearCheck()
        }
        prevButton.setOnClickListener { v: View? ->
            viewModel.prevQuestion()
        }

        radioGroup.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.radio_1 -> {
                    radioButtonId=0
                    yesButton.isEnabled=true
                }
                R.id.radio_2 -> {
                    radioButtonId=1
                    yesButton.isEnabled=true
                }
                R.id.radio_3 -> {
                    radioButtonId=2
                    yesButton.isEnabled=true
                }
                R.id.radio_4 -> {
                    radioButtonId=3
                    yesButton.isEnabled=true
                }
            }
        })

    }

    //Spinners
    private fun initSpinnerAdapters() {

        val testDbNames = resources.getStringArray(R.array.tests)
        val testsNames = resources.getStringArray(R.array.testsNames)


        val testNamesList = listOf(*resources.getStringArray(R.array.testsNames))
        val adapter: ArrayAdapter<*> =
            ArrayAdapter<String>(this, R.layout.spinner_item, testNamesList)
        adapter.setDropDownViewResource(R.layout.spinner_item)

        tableNameSpinner.adapter = adapter

        tableNameSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>?,
                view: View?,
                i: Int,
                l: Long
            ) {
                tableName = testDbNames[i]

                selectedTableId = i

                prevButton.visibility = View.GONE

                selectedTableId = i

                if (i > 0) {
                    if (userName == null || userName?.length == 0) {
                        requestNewUserName()
                    }
                    if (testName != testsNames[i]) {
                        testName = testsNames[i]
                        viewModel.changeTableName(tableName)
                    }
                }
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {
                tableName = "azenk_child"
            }
        }
    }

    private fun requestNewUserName() {
        inputUserDataFragment = InputUserDataFragment()
        val bundle = Bundle()
        bundle.putInt(KEY_TEST_ID, selectedTableId)
        inputUserDataFragment!!.arguments = bundle
        inputUserDataFragment!!.show(supportFragmentManager, KEY_FRAGMENT_USER_DATA)
        inputUserDataFragment = null
    }

    @SuppressLint("SetTextI18n")
    private fun setupObserver() {
        viewModel.viewModelStatus.observe(this) {
            when (it) {
                Status.FIRST -> {
                    postNewQuestion()
                    prevButton.isEnabled = false
                    prevButton.visibility = View.GONE
                    radioGroup.visibility = View.VISIBLE
                    Log.d("My_tag", viewModel.calculation.getResultString())
                }
                Status.RESPONSE -> {
                    postNewQuestion()
                    prevButton.isEnabled = true
                    prevButton.visibility = View.VISIBLE
                    Log.d("My_tag", viewModel.calculation.getResultString())
                }
                Status.LOADING -> {
                    yesButton.isEnabled = false
                    prevButton.isEnabled = false
                    progressBar.visibility = View.VISIBLE
                }
                Status.DONE -> {
                    questionText.text = "Закончен"
                    Log.d("My_tag", viewModel.calculation.getResultString())
                    yesButton.isEnabled = false

                    yesButton.visibility = View.GONE
                    prevButton.visibility = View.GONE
                    radioGroup.visibility = View.GONE

                    viewModel.addResultsToDb()
                }
                Status.PRINTED -> {
                    progressBar.visibility = View.GONE
                    Toast.makeText(
                        applicationContext,
                        "PRINTED to - " + filePath,
                        Toast.LENGTH_LONG
                    ).show()
                }
                Status.WRITING_RES_TO_DB -> {

                }
            }
        }
    }

    private fun postNewQuestion() {
        val q = viewModel.currentQuestion
        subQuestionList = q?.subQuestionText?.split(";") as ArrayList<String>
        questionsCount = viewModel.questionMaxId
        questionText.text =
            q?.id.toString() + "/" + questionsCount + ". " + q?.questionText + "?"

        //UI update
        radioButtonList.forEach { v ->
            v.isEnabled = false
            v.visibility = View.GONE
            v.text = ""
        }
        yesButton.isEnabled=false

        subQuestionList.forEach { v ->
            radioButtonList[subQuestionList.lastIndexOf(v)].isEnabled = true
            radioButtonList[subQuestionList.lastIndexOf(v)].visibility = View.VISIBLE
            radioButtonList[subQuestionList.lastIndexOf(v)].text = v
        }

        yesButton.visibility = View.VISIBLE
        progressBar.visibility = View.INVISIBLE
    }
}






