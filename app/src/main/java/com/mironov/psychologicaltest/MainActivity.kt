package com.mironov.psychologicaltest

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import android.content.Intent
import android.os.Debug
import java.io.File
import androidx.core.content.FileProvider
import android.widget.Toast
import com.mironov.psychologicaltest.constants.KeysContainer.KEY_FRAGMENT_USER_DATA
import com.mironov.psychologicaltest.constants.KeysContainer.KEY_NAME_FRAGMENT
import com.mironov.psychologicaltest.constants.KeysContainer.KEY_NAME_MAIN_ACTIVITY
import com.mironov.psychologicaltest.constants.KeysContainer.KEY_SENDER
import com.mironov.psychologicaltest.constants.KeysContainer.KEY_TEST_NAME
import com.mironov.psychologicaltest.constants.KeysContainer.KEY_USER_NAME
import com.mironov.psychologicaltest.constants.Status
import com.mironov.psychologicaltest.ui.InputUserDataFragment



class MainActivity : AppCompatActivity() {

    lateinit var inputUserDataFragment: InputUserDataFragment

    lateinit var viewModel: MainViewModel

    lateinit var yesButton: Button
    lateinit var noButton: Button
    lateinit var prevButton: Button
    lateinit var resetButton: Button
    lateinit var createButton: Button
    lateinit var presentButton: Button

    private lateinit var progressBar: ProgressBar

    private lateinit var tableNameSpinner: Spinner

    private lateinit var questionText: TextView

    lateinit var tableName: String

    private var rootPath = ""
    private var filePath = ""
    private var testName = ""

    private var userName: String? = null


    // Storage Permissions
    private val REQUEST_EXTERNAL_STORAGE = 1
    private val PERMISSIONS_STORAGE = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )


    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {

        //Debug.waitForDebugger()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        viewModel.setAnswers(getString(R.string.answer_no),getString(R.string.answer_yes))
        viewModel.answerToPrintText=getString(R.string.print_answer)

        setupObserver()
        initViews()
        setupButtonsListeners()
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
            val i =tableNameSpinner.selectedItemId.toInt()
            putInt("TABLENAME", i)
            tableNameSpinner.setSelection(i)
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        val i = savedInstanceState?.getInt("TABLENAME")
        tableNameSpinner.setSelection(i)
    }

    private fun receiveData() {
        //RECEIVE DATA VIA INTENT

        //DETERMINE WHO STARTED THIS ACTIVITY
        val sender = this.intent.extras?.getString(KEY_SENDER)

        //IF ITS THE FRAGMENT THEN RECEIVE DATA
        if (sender.equals(KEY_NAME_FRAGMENT)) {
            val name = intent.getStringExtra(KEY_NAME_FRAGMENT)
            userName = name
            viewModel.userName=name
        }
        if(userName==null ||userName?.length==0) {
            tableNameSpinner.setSelection(0)
        }
    }

    private fun initViews() {
        inputUserDataFragment = InputUserDataFragment()

        yesButton = findViewById(R.id.yesButton)
        noButton = findViewById(R.id.noButton)
        prevButton = findViewById(R.id.prevButton)
        resetButton = findViewById(R.id.resetButton)
        createButton = findViewById(R.id.createButton)
        presentButton = findViewById(R.id.presentResultsButton)

        questionText = findViewById(R.id.questionText)
        tableNameSpinner = findViewById(R.id.tableNameSpinner)

        resetButton.visibility = View.GONE
        prevButton.visibility = View.GONE
        createButton.visibility = View.GONE
        //presentButton.visibility=View.GONE

        createButton.isEnabled = false
        noButton.isEnabled = false
        yesButton.isEnabled = false
        prevButton.isEnabled = false

        progressBar = findViewById(R.id.progressBar)
        progressBar.visibility = View.INVISIBLE

        inputUserDataFragment = InputUserDataFragment()
    }


    //Buttons Listeners
    @RequiresApi(Build.VERSION_CODES.N)
    private fun setupButtonsListeners() {
        yesButton.setOnClickListener { v: View? ->
            viewModel.answerYes()
        }
        noButton.setOnClickListener { v: View? ->
            viewModel.answerNo()
        }
        resetButton.setOnClickListener { v: View? ->
            resetButton.visibility = View.GONE
            createButton.visibility = View.GONE
            resetButton.visibility=View.GONE
            viewModel.reset()
        }
        prevButton.setOnClickListener { v: View? ->
            viewModel.prevQuestion()
        }

        createButton.setOnClickListener { v: View? ->
            createButton.visibility = View.GONE
            createButton.isEnabled = false
            filePath = rootPath + userName + "-" + testName + ".pdf"
            viewModel.printResults(filePath)
        }
        presentButton.setOnClickListener { v: View? ->
            val intent = Intent(this, ResultsActivity::class.java)
            intent.putExtra(KEY_SENDER, KEY_NAME_MAIN_ACTIVITY)
            intent.putExtra(KEY_USER_NAME, userName)
            intent.putExtra(KEY_TEST_NAME, tableName)
            this.startActivity(intent)
        }

    }

    //Spinners
    private fun initSpinnerAdapters() {

        val testDbNames = resources.getStringArray(R.array.tests)
        val testsNames = resources.getStringArray(R.array.testsNames)

        val adapter: ArrayAdapter<*> = ArrayAdapter.createFromResource(
            this,
            R.array.testsNames,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)


        tableNameSpinner.adapter = adapter

        tableNameSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>?,
                view: View?,
                i: Int,
                l: Long
            ) {
                tableName = testDbNames[i]

                prevButton.visibility = View.GONE
                resetButton.visibility = View.GONE
                //presentButton.visibility=View.GONE

                testName = testsNames[i]

                if (i>0) {
                    if (userName == null||userName?.length==0) {
                        inputUserDataFragment.show(supportFragmentManager, KEY_FRAGMENT_USER_DATA)

                    }
                    viewModel.changeTableName(tableName)
                }
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {
                tableName = "azenk_child"
            }

        }


    }

    @SuppressLint("SetTextI18n")
    private fun setupObserver() {
        viewModel.viewModelStatus.observe(this) {
            when (it) {
                Status.FIRST -> {
                    prevButton.isEnabled = false
                    val q = viewModel.currentQuestion
                    questionText.text = q?.id.toString() + ". " + q?.questionText + "?"
                    noButton.isEnabled = true
                    yesButton.isEnabled = true
                    noButton.visibility = View.VISIBLE
                    yesButton.visibility = View.VISIBLE
                    progressBar.visibility = View.INVISIBLE

                    Log.d("My_tag", "-------")
                    Log.d("My_tag", viewModel.calculation.getResultString())
                }
                Status.RESPONSE -> {
                    val q = viewModel.currentQuestion
                    questionText.text = q?.id.toString() + ". " + q?.questionText + "?"
                    noButton.isEnabled = true
                    yesButton.isEnabled = true
                    prevButton.isEnabled = true
                    noButton.visibility = View.VISIBLE
                    yesButton.visibility = View.VISIBLE
                    prevButton.visibility = View.VISIBLE
                    progressBar.visibility = View.INVISIBLE
                    Log.d("My_tag", viewModel.calculation.getResultString())
                }
                Status.LOADING -> {
                    noButton.isEnabled = false
                    yesButton.isEnabled = false
                    prevButton.isEnabled = false
                    progressBar.visibility = View.VISIBLE
                }
                Status.DONE -> {
                    questionText.text = viewModel.calculation.getResultString()
                    Log.d("My_tag", viewModel.calculation.getResultString())
                    noButton.isEnabled = false
                    yesButton.isEnabled = false
                    createButton.isEnabled = true

                    noButton.visibility = View.GONE
                    yesButton.visibility = View.GONE
                    prevButton.visibility = View.GONE
                    resetButton.visibility = View.VISIBLE
                    createButton.visibility = View.VISIBLE
                    presentButton.visibility= View.VISIBLE

                    viewModel.addResultsToDb()
                }
                Status.PRINTED -> {
                    progressBar.visibility = View.GONE
                    Toast.makeText(
                        applicationContext,
                        "PRINTED to - " + filePath,
                        Toast.LENGTH_LONG
                    ).show()
                    Log.d("My_tag", "PRINTED to - " + filePath)
                    viewPdfFile(filePath)
                }
                Status.WRITING_RES_TO_DB -> {

                }
            }
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






