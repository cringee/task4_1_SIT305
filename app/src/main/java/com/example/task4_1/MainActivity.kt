package com.example.task4_1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.SystemClock
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.media.MediaPlayer


class MainActivity : AppCompatActivity() {
    // declare variables
    private lateinit var workoutTimer: CountDownTimer
    private lateinit var restTimer: CountDownTimer
    private lateinit var handler: Handler
    private lateinit var workoutTime: TextView
    private lateinit var restTime: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var startButton: Button
    private lateinit var stopButton: Button
    private lateinit var workoutDurationEditText: EditText
    private lateinit var restDurationEditText: EditText
    private lateinit var mediaPlayer: MediaPlayer

    private var workoutTimeRemaining: Long = 0
    private var restTimeRemaining: Long = 0
    private var workoutDuration: Long = 0
    private var restDuration: Long = 0
    private var isWorkoutTimerRunning = false
    private var isRestTimerRunning = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // UI
        workoutTime = findViewById(R.id.workoutTime)
        restTime = findViewById(R.id.restTime)
        progressBar = findViewById(R.id.progressBar)

        startButton = findViewById(R.id.startButton)
        stopButton = findViewById(R.id.stopButton)
        workoutDurationEditText = findViewById(R.id.workoutDuration)
        restDurationEditText = findViewById(R.id.restDuration)

        // media player for chime sound at the end of the timer
        mediaPlayer = MediaPlayer.create(this, R.raw.chime)

        //handler = Handler()

        // listener for start button
        startButton.setOnClickListener {
            // hide start button and show stop button
            startButton.visibility = View.GONE
            stopButton.visibility = View.VISIBLE

            // Get workout and rest durations from input fields and convert to milliseconds
            workoutDuration = workoutDurationEditText.text.toString().toLong() * 1000
            restDuration = restDurationEditText.text.toString().toLong() * 1000

            // Set progress bar maximum value based on workout duration
            progressBar.max = (workoutDuration / 1000).toInt()

            // start timer
            startWorkoutTimer()
        }

        // Set listener for stop button
        stopButton.setOnClickListener {
            stopButton.visibility = View.GONE
            startButton.visibility = View.VISIBLE

            // Stop both timers
            stopWorkoutTimer()
            stopRestTimer()
        }

    }

    // starts workout timer
    private fun startWorkoutTimer() {
        isWorkoutTimerRunning = true

        workoutTimer = object : CountDownTimer(workoutDuration, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                workoutTimeRemaining = millisUntilFinished
                updateWorkoutTime()

                val progress = ((workoutDuration - millisUntilFinished) / 1000).toInt()
                progressBar.progress = progress
            }

            override fun onFinish() {
                workoutTimeRemaining = 0
                progressBar.progress = 0
                updateWorkoutTime()
                mediaPlayer.start()
                startRestTimer()
            }
        }

        workoutTimer.start()
        updateWorkoutTime()
    }


    // Stops the workout timer and updates the timer display
    private fun stopWorkoutTimer() {
        if (isWorkoutTimerRunning) {
            workoutTimer.cancel()
            isWorkoutTimerRunning = false
            workoutTimeRemaining = 0
            updateWorkoutTime()
        }
    }

    // starts rest timer
    private fun startRestTimer() {
        isRestTimerRunning = true
        progressBar.max = (restDuration / 1000).toInt()

        restTimer = object : CountDownTimer(restDuration, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                restTimeRemaining = millisUntilFinished
                updateRestTime()

                val progress = ((restDuration - millisUntilFinished) / 1000).toInt()
                progressBar.progress = progress
            }

            override fun onFinish() {
                restTimeRemaining = 0
                progressBar.progress = 0
                updateRestTime()
                mediaPlayer.start()
                startWorkoutTimer()
            }
        }

        restTimer.start()
        updateRestTime()
    }

    // Stops the reset timer and updates the timer display
    private fun stopRestTimer() {
        if (isRestTimerRunning) {
            restTimer.cancel()
            isRestTimerRunning = false
            restTimeRemaining = 0
            updateRestTime()
        }
    }

    private fun updateWorkoutTime() {
        // Calculate the minutes and seconds remaining in the workout timer
        val minutes = (workoutTimeRemaining / 1000) / 60
        val seconds = (workoutTimeRemaining / 1000) % 60

        // update text
        workoutTime.text = String.format("%02d:%02d", minutes, seconds)

        // update progress bar
        val progress = ((workoutDuration - workoutTimeRemaining) / 1000).toInt()
        progressBar.progress = progress
    }

    private fun updateRestTime() {
        val minutes = (restTimeRemaining / 1000) / 60
        val seconds = (restTimeRemaining / 1000) % 60

        restTime.text = String.format("%02d:%02d", minutes, seconds)
    }

    override fun onDestroy() {
        super.onDestroy()
        // free up space when destroyed!!
        mediaPlayer.release()
    }
}