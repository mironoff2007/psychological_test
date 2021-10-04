package com.mironov.psychologicaltest

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider

class MainActivity : AppCompatActivity() {

    lateinit var viewModel: MainViewModel

    lateinit var yesButton: Button
    lateinit var noButton: Button
    lateinit var prevButton: Button
    lateinit var resetButton: Button
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
        prevButton = findViewById(R.id.prevButton)
        resetButton = findViewById(R.id.resetButton)
        questionText = findViewById(R.id.questionText)
        resetButton.setVisibility(View.GONE);
        prevButton.setVisibility(View.GONE);
        noButton.isEnabled = false
        yesButton.isEnabled = false
        prevButton.isEnabled = false
    }

    private fun setupButtonsListeners() {
        yesButton.setOnClickListener { v: View? ->
            viewModel.answerYes()
        }
        noButton.setOnClickListener { v: View? ->
            viewModel.answerNo()
        }
        resetButton.setOnClickListener { v: View? ->
            resetButton.setVisibility(View.GONE);
            viewModel.reset()
        }
        prevButton.setOnClickListener { v: View? ->
            viewModel.prevQuestion()
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
                    noButton.setVisibility(View.VISIBLE);
                    yesButton.setVisibility(View.VISIBLE);
                    Log.d("My_tag", viewModel.calculation.getResultString())
                }
                Status.RESPONSE -> {
                    var q = viewModel.currentQuestion
                    questionText.text = q?.id.toString() + ". " + q?.questionText + "?"
                    noButton.isEnabled = true
                    yesButton.isEnabled = true
                    prevButton.isEnabled = true
                    noButton.setVisibility(View.VISIBLE);
                    yesButton.setVisibility(View.VISIBLE);
                    prevButton.setVisibility(View.VISIBLE);
                    Log.d("My_tag", viewModel.calculation.getResultString())
                }
                Status.LOADING -> {
                    noButton.isEnabled = false
                    yesButton.isEnabled = false
                    prevButton.isEnabled = false
                }
                Status.DONE -> {
                    var extr = viewModel.calculation.extr
                    var neur = viewModel.calculation.neur
                    var lie = viewModel.calculation.lie

                    questionText.text =
                        " Экстраверсия=" + extr + "\n Нейротизм=" + neur + "\n Лживость=" + lie
                    Log.d("My_tag", viewModel.calculation.getResultString())
                    noButton.isEnabled = false
                    yesButton.isEnabled = false

                    noButton.setVisibility(View.GONE);
                    yesButton.setVisibility(View.GONE);
                    prevButton.setVisibility(View.GONE);
                    resetButton.setVisibility(View.VISIBLE);
                }

            }
        }
    }
}