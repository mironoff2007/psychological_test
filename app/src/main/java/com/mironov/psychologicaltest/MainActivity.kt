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
import java.io.File
import androidx.core.content.FileProvider


class MainActivity : AppCompatActivity() {

    lateinit var viewModel: MainViewModel

    lateinit var yesButton: Button
    lateinit var noButton: Button
    lateinit var prevButton: Button
    lateinit var resetButton: Button
    lateinit var createButton: Button

    private lateinit var progressBar: ProgressBar

    private lateinit var tableNameSpinner: Spinner

    private lateinit var questionText: TextView

    lateinit var tableName: String

    var rootPath=""
    var filePath=""

    // Storage Permissions
    val REQUEST_EXTERNAL_STORAGE = 1
    val PERMISSIONS_STORAGE = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Debug.waitForDebugger()

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        setupObserver()
        initViews()
        setupButtonsListeners()
        initSpinnerAdapters()

        if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED ||
            checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        )
        {

        } else {
            ActivityCompat.requestPermissions(
                this,
                PERMISSIONS_STORAGE,
                REQUEST_EXTERNAL_STORAGE
            )
        }
        rootPath = applicationContext.getExternalFilesDir(null)!!.absolutePath + "/"

    }

    private fun initViews() {
        yesButton = findViewById(R.id.yesButton)
        noButton = findViewById(R.id.noButton)
        prevButton = findViewById(R.id.prevButton)
        resetButton = findViewById(R.id.resetButton)
        createButton = findViewById(R.id.createButton)

        questionText = findViewById(R.id.questionText)
        tableNameSpinner = findViewById(R.id.tableNameSpinner)

        resetButton.visibility = View.GONE
        prevButton.visibility = View.GONE
        createButton.visibility = View.GONE

        createButton.isEnabled = false
        noButton.isEnabled = false
        yesButton.isEnabled = false
        prevButton.isEnabled = false

        progressBar = findViewById(R.id.progressBar)
        progressBar.visibility = View.INVISIBLE
    }

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
            viewModel.reset()
        }
        prevButton.setOnClickListener { v: View? ->
            viewModel.prevQuestion()
        }

        createButton.setOnClickListener { v: View? ->
            createButton.visibility = View.GONE
            createButton.isEnabled = false
            filePath=rootPath+"test_pdf.pdf"
            viewModel.printResults(filePath)
        }

    }

    private fun initSpinnerAdapters() {

        val stringArray = resources.getStringArray(R.array.tests)

        val adapter: ArrayAdapter<*> = ArrayAdapter.createFromResource(
            this,
            R.array.testsNames,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        //Spinner From
        tableNameSpinner.adapter = adapter
        tableNameSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>?,
                view: View?,
                i: Int,
                l: Long
            ) {
                tableName = stringArray[i]
                viewModel.changeTableName(tableName)
                prevButton.visibility = View.GONE
                resetButton.visibility = View.GONE
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {
                tableName = "azenk_child"
            }
        }
    }

    private fun setupObserver() {
        viewModel.viewModelStatus.observe(this) {
            when (it) {
                Status.FIRST -> {
                    prevButton.isEnabled = false
                    var q = viewModel.currentQuestion
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
                }
                Status.PRINTED -> {
                    progressBar.visibility = View.GONE
                    Toast.makeText(applicationContext,"PRINTED to - "+rootPath,Toast.LENGTH_LONG ).show()
                    Log.d("My_tag","PRINTED to - "+rootPath)
                    viewPdfFile(filePath)
                }

            }
        }
    }

    @SuppressLint("IntentReset")
    fun viewPdfFile(path: String?) {

        val file = File(path)

        val intent = Intent(Intent.ACTION_VIEW, FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", file))
        //intent.type = "application/pdf"
        intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION

        startActivity(intent)
    }
}