package com.example.preferencedatastoremvvm

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.elvishew.xlog.BuildConfig
import com.elvishew.xlog.LogConfiguration
import com.elvishew.xlog.LogLevel
import com.elvishew.xlog.XLog
import com.elvishew.xlog.flattener.ClassicFlattener
import com.elvishew.xlog.printer.AndroidPrinter
import com.elvishew.xlog.printer.Printer
import com.elvishew.xlog.printer.file.FilePrinter
import com.elvishew.xlog.printer.file.naming.ChangelessFileNameGenerator
import com.elvishew.xlog.printer.file.writer.SimpleWriter
import com.example.preferencedatastoremvvm.BaseApplication.Companion.isLoggingPaused
import com.example.preferencedatastoremvvm.databinding.ActivityMainBinding
import com.example.preferencedatastoremvvm.viewmodel.DataViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.time.LocalDate
import kotlin.random.Random

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: DataViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding

    private var logCoroutineJob: Job? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        XLog.d("MainActivity OnCreate")
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.saveBtn.setOnClickListener {
            val name = binding.nameEt.text.toString()
            val age = Integer.parseInt(binding.ageEt.text.toString())
            viewModel.saveData(name, age)
        }

        binding.dispTv.setOnClickListener{
            binding.dispTv.text = viewModel.getData()
        }

        startLogging()

        // Rename log file after 5 seconds (for testing purposes)
        CoroutineScope(Dispatchers.Main).launch {
            delay(5000)
            renameLogFile()
        }
    }

    // Function to start logging messages in the background using coroutines
    private fun startLogging() {
        var counter = 0 // Initialize the counter

        logCoroutineJob = CoroutineScope(Dispatchers.IO).launch {
            while (isActive) {
                if (!isLoggingPaused) {
                    counter++ // Increment the counter

                    val randomMessage = "Log $counter: Random log message: ${Random.nextInt()}"
                    XLog.d(randomMessage)
                }
                delay(1000) // Log every second
            }
        }
    }


    // Function to rename the log file
    private suspend fun renameLogFile() {
        // Pause the logging process
        isLoggingPaused = true

        // Wait for 1 second to ensure no logging happens while renaming
        delay(1000)

        // Rename the log file
        LoggerHelper.renameLogFile()

        // Resume the logging process
        isLoggingPaused = false
    }

    override fun onDestroy() {
        super.onDestroy()
        // Cancel the coroutine when activity is destroyed
        logCoroutineJob?.cancel()
    }
}