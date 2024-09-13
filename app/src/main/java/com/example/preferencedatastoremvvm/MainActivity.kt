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
import com.example.preferencedatastoremvvm.databinding.ActivityMainBinding
import com.example.preferencedatastoremvvm.viewmodel.DataViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.time.LocalDate
import kotlin.random.Random

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: DataViewModel by viewModels()

    private lateinit var binding: ActivityMainBinding

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initXlog() {
        val config = LogConfiguration.Builder().logLevel(LogLevel.ALL).tag("XLog").build()
        val androidPrinter: Printer = AndroidPrinter()
        val filePrinter: Printer = FilePrinter.Builder(File(this.filesDir, "logs").path)
            .fileNameGenerator(ChangelessFileNameGenerator("${LocalDate.now()}.log"))
            .flattener(ClassicFlattener()).writer(object : SimpleWriter() {
                override fun onNewFileCreated(file: File) {
                    super.onNewFileCreated(file)
                    val header = """             
         >>>>>>>>>>>>>>>> File Header >>>>>>>>>>>>>>>>
         Device Manufacturer: ${Build.MANUFACTURER}
         Device Model       : ${Build.MODEL}
         Android Version    : ${Build.VERSION.RELEASE}
         Android SDK        : ${Build.VERSION.SDK_INT}
         App VersionName    : ${BuildConfig.VERSION_NAME}
         App VersionCode    : ${BuildConfig.VERSION_CODE}
         <<<<<<<<<<<<<<<< File Header <<<<<<<<<<<<<<<< 
         """.trimIndent()
                    appendLog(header)
                }
            }).build()

        Log.d("[path]", "initXlog: ${File(this.filesDir, "logs").path}")

        XLog.init(config, androidPrinter, filePrinter)
    }



    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        XLog.d("MainActivity OnCreate")
        binding = ActivityMainBinding.inflate(layoutInflater)


        binding.saveBtn.setOnClickListener {
            val name = binding.nameEt.text.toString()
            val age = Integer.parseInt(binding.ageEt.text.toString())

            viewModel.saveData(name, age)
        }

        binding.dispTv.setOnClickListener{
            binding.dispTv.text = viewModel.getData()
        }

        logRandomMessagesInBackground()

        setContentView(binding.root)
    }

    fun logRandomMessagesInBackground() {
        val maxSize = 4 * 1024 * 1024 // 4 MB

        // Use CoroutineScope for background work
        CoroutineScope(Dispatchers.IO).launch {
            while (1 == 1) {
                val randomMessage = "Random log message: ${Random.nextInt()}"
                XLog.d(randomMessage)
            }
        }
    }
}