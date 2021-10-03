package com.mironov.psychologicaltest

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.mironov.psychologicaltest.model.Question

class MainActivity : AppCompatActivity() {

    lateinit var viewModel: MainViewModel

    lateinit var nextButton: Button
    private lateinit var questionText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        setupObserver()
        initViews()
        setupButtonsListeners()
    }

    private fun initViews() {
        nextButton = findViewById(R.id.nextButton)
        questionText= findViewById(R.id.questionText)
    }
    private fun setupButtonsListeners() {
        nextButton.setOnClickListener { v: View? ->
            viewModel.getNextQuestion()
        }

    }

    private fun setupObserver() {
        viewModel.viewModelStatus.observe(this) {
            when (it) {
                Status.RESPONSE -> {
                    var q=viewModel.currentQuestion
                    questionText.text=q?.id.toString()+". "+q?.questionText+"?"
                }
            }
        }
    }
}