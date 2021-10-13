package com.mironov.psychologicaltest

import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.mironov.psychologicaltest.constants.KeysContainer
import com.mironov.psychologicaltest.constants.KeysContainer.KEY_NAME_MAIN_ACTIVITY
import com.mironov.psychologicaltest.constants.KeysContainer.KEY_TEST_NAME
import com.mironov.psychologicaltest.constants.KeysContainer.KEY_USER_NAME

class ResultsActivity : AppCompatActivity() {

    private  var userName :String?=null
    private  var testName :String?=null

    lateinit var viewModel: MainViewModel

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_results_presenter)

        val sender = this.intent.extras?.getString(KeysContainer.KEY_SENDER)


        //IF ITS THE FRAGMENT THEN RECEIVE DATA
        if (sender.equals(KEY_NAME_MAIN_ACTIVITY)) {
            userName = intent.getStringExtra(KEY_USER_NAME)
            testName = intent.getStringExtra(KEY_TEST_NAME)
        }


        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        viewModel.readAnswersByTest(userName.toString(),testName.toString())
    }

}