package com.mironov.psychologicaltest

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.mironov.psychologicaltest.model.Question

class MainActivity : AppCompatActivity() {

    lateinit var viewModel: MainViewModel

    lateinit var yesButton: Button
    lateinit var noButton: Button
    private lateinit var questionText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        setupObserver()
        initViews()
        setupButtonsListeners()
        viewModel.getNextQuestion()
    }

    private fun initViews() {
        yesButton = findViewById(R.id.yesButton)
        noButton = findViewById(R.id.noButton)
        questionText= findViewById(R.id.questionText)
    }
    private fun setupButtonsListeners() {
        yesButton.setOnClickListener { v: View? ->
            viewModel.answerYes()
        }
        noButton.setOnClickListener { v: View? ->
            viewModel.answerNo()
        }

    }

    private fun setupObserver() {
        viewModel.viewModelStatus.observe(this) {
            when (it) {
                Status.RESPONSE -> {
                    var q=viewModel.currentQuestion
                    questionText.text=q?.id.toString()+". "+q?.questionText+"?"
                    noButton.isEnabled=true
                    yesButton.isEnabled=true
                }
                Status.LOADING -> {
                   noButton.isEnabled=false
                    yesButton.isEnabled=false
                }
                Status.DONE -> {
                    var extr=viewModel.calculation.extr
                    var neur=viewModel.calculation.neur
                    var lie=viewModel.calculation.lie
                    Toast.makeText(applicationContext,viewModel.calculation.getResultString(),Toast.LENGTH_LONG).show()
                    Log.d("My_tag",viewModel.calculation.getResultString())
                }
            }
        }
    }
}