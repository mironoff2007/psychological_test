package com.mironov.psychologicaltest

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.text.method.ScrollingMovementMethod
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.content.MimeTypeFilter
import androidx.lifecycle.ViewModelProvider
import com.mironov.psychologicaltest.constants.ConstantsContainer
import com.mironov.psychologicaltest.constants.ConstantsContainer.KEY_FRAGMENT_LOGIN
import com.mironov.psychologicaltest.constants.ConstantsContainer.KEY_TEST_NAME
import com.mironov.psychologicaltest.constants.ConstantsContainer.KEY_USER_NAME
import com.mironov.psychologicaltest.constants.ResultsStatus
import java.io.File
import java.net.URLConnection

class ResultsActivity : AppCompatActivity() {

    private var receivedUserName: String? = null
    private var receivedTestName: String? = null

    private var selectedUser: String? = null
    private var selectedTest: String? = null

    lateinit var usersList: ArrayList<String?>
    lateinit var testsList: ArrayList<String?>
    lateinit var userTestNames: ArrayList<String?>

    private var testNameSpinner: Spinner? = null
    private var userNameSpinner: Spinner? = null

    private lateinit var progressBar: ProgressBar

    private lateinit var resultText: TextView

    lateinit var viewModel: ResultsViewModel

    private var rootPath = ""
    private var filePath = ""

    lateinit var createButton: Button

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_results_presenter)

        userTestNames = arrayListOf<String?>()

        val sender = this.intent.extras?.getString(ConstantsContainer.KEY_SENDER)

        //IF ITS THE FRAGMENT THEN RECEIVE DATA
        if (sender.equals(KEY_FRAGMENT_LOGIN)) {
            receivedUserName = intent.getStringExtra(KEY_USER_NAME)
            receivedTestName = intent.getStringExtra(KEY_TEST_NAME)
        }

        viewModel = ViewModelProvider(this).get(ResultsViewModel::class.java)

        setupObserver()
        initViews()
        setupButtonsListeners()

        viewModel.readUsers()

        if (applicationContext.getExternalFilesDir(null) == null) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                rootPath = getExternalFilesDirs(null)[0].absolutePath + "/"

            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                rootPath = filesDir.absolutePath + "/"
            }
        } else {
            rootPath = applicationContext.getExternalFilesDir(null)!!.absolutePath + "/"
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.results_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.export_to_storage -> {
                viewModel.saveDbToStorage(rootPath, applicationContext)
                true
            }
            R.id.import_from_storage -> {
                viewModel.importAnswers(rootPath, applicationContext)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.run {
            putString(KEY_TEST_NAME, selectedTest)
            putString(KEY_USER_NAME, selectedUser)
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        receivedUserName = savedInstanceState.getString(KEY_USER_NAME)
        receivedTestName = savedInstanceState.getString(KEY_TEST_NAME)
        viewModel.readUsers()
    }

    private fun initViews() {
        createButton = findViewById(R.id.createButton)
        resultText = findViewById(R.id.result_text)
        progressBar = findViewById(R.id.progressBarResults)
        userNameSpinner = findViewById(R.id.spinner_users)
        testNameSpinner = findViewById(R.id.spinner_tests)

        createButton.isEnabled = false
        progressBar.isEnabled = false
        userNameSpinner?.isEnabled = false
        testNameSpinner?.isEnabled = false

        resultText.movementMethod = ScrollingMovementMethod()
    }

    //Spinners
    private fun initSpinnerUsers() {

        val adapter: ArrayAdapter<*> = ArrayAdapter<String>(this, R.layout.spinner_item, usersList)
        adapter.setDropDownViewResource(R.layout.spinner_item)

        userNameSpinner!!.isEnabled = usersList.isNotEmpty()
        userNameSpinner!!.adapter = adapter

        userNameSpinner!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>?,
                view: View?,
                i: Int,
                l: Long
            ) {
                selectedUser = usersList[i]
                if (selectedUser != null) {
                    viewModel.readFinishedTest(selectedUser.toString())
                }
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }
    }

    private fun initSpinnerTables() {

        val testDbNames = resources.getStringArray(R.array.tests)
        val testsNames = resources.getStringArray(R.array.testsNames)

        var map = testDbNames.zip(testsNames).toMap()

        userTestNames.clear()

        testsList.forEach { v ->
            userTestNames.add(map[v])
        }
        map = emptyMap()

        val adapter: ArrayAdapter<*> =
            ArrayAdapter<String>(this, R.layout.spinner_item, userTestNames)

        adapter.setDropDownViewResource(R.layout.spinner_item)

        testNameSpinner!!.isEnabled = userTestNames.isNotEmpty()
        testNameSpinner!!.adapter = adapter

        testNameSpinner!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>?,
                view: View?,
                i: Int,
                l: Long
            ) {
                selectedTest = testsList[i]

                if (selectedUser != null && selectedTest != null) {
                    viewModel.getResult(selectedUser!!, selectedTest!!)
                    createButton.isEnabled = true
                } else {
                    createButton.isEnabled = false
                }
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }
    }

    private fun setupObserver() {
        viewModel.resultsModelStatus.observe(this) { it ->
            when (it) {
                ResultsStatus.TEST_NAMES_LOADED -> {
                    testsList = viewModel.testsList

                    testNameSpinner?.adapter = null
                    testNameSpinner?.onItemSelectedListener = null
                    initSpinnerTables()

                    testNameSpinner!!.setSelection(testsList.indexOf(receivedTestName))

                    progressBar.isEnabled = false
                    progressBar.visibility = View.GONE
                }
                ResultsStatus.USERS_LOADED -> {
                    usersList = viewModel.usersList

                    userNameSpinner?.adapter = null
                    userNameSpinner?.onItemSelectedListener = null
                    initSpinnerUsers()

                    userNameSpinner!!.setSelection(usersList.indexOf(receivedUserName))

                    progressBar.isEnabled = false
                    progressBar.visibility = View.GONE
                }
                ResultsStatus.PRINTED -> {
                    Toast.makeText(
                        applicationContext,
                        applicationContext.getString(R.string.saved_to_text)+ filePath,
                        Toast.LENGTH_LONG
                    ).show()

                    progressBar.isEnabled = false
                    progressBar.visibility = View.GONE

                    viewFile(filePath)
                }
                ResultsStatus.LOADING -> {
                    progressBar.isEnabled = true
                    progressBar.visibility = View.VISIBLE
                }
                ResultsStatus.IMPORTED_ANSWERS_FROM_STORAGE -> {
                    viewModel.importResults(rootPath, applicationContext)
                }
                ResultsStatus.IMPORTED_RESULTS_FROM_STORAGE -> {
                    Toast.makeText(
                        applicationContext,
                        getString(R.string.db_imported),
                        Toast.LENGTH_LONG
                    ).show()
                    viewModel.readUsers()
                }
                ResultsStatus.RESULTS_LOADED -> {
                    resultText.text = viewModel.resultText
                    progressBar.isEnabled = false
                    progressBar.visibility = View.GONE
                }
            }
        }
    }

    private fun setupButtonsListeners() {
        createButton.setOnClickListener {
            createDocument()
        }
    }

    private fun createDocument() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                filePath = rootPath + selectedUser + "-" + selectedTest + ".pdf"
                viewModel.printResultsToPDF(
                    filePath,
                    selectedTest.toString(),
                    selectedUser.toString()
                )
            } else {
                filePath = rootPath + selectedUser + "-" + selectedTest + ".txt"
                viewModel.printResultsToTXT(
                    filePath,
                    selectedTest.toString(),
                    selectedUser.toString()
                )
            }
        }
        catch (e:Exception){
            Toast.makeText(
                applicationContext,
                e.message,
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun viewFile(path: String) {
        val file = File(path)
        val intent = Intent(
            Intent.ACTION_VIEW,
            FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", file)
        )
        intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION

        startActivity(Intent.createChooser(intent, "Share File"))
    }

    private fun viewFile2(path: String) {
        val intentShareFile =  Intent(Intent.ACTION_SEND)

        val file = File(path)
        intentShareFile.type = "application/pdf"
        intentShareFile.putExtra(
            Intent.EXTRA_STREAM,
            Uri.fromFile(file)
        );

        //if you need
        //intentShareFile.putExtra(Intent.EXTRA_SUBJECT,"Sharing File Subject);
        //intentShareFile.putExtra(Intent.EXTRA_TEXT, "Sharing File Description");

        startActivity(Intent.createChooser(intentShareFile, "Share File"));
    }
}