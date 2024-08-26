package com.example.preferencedatastoremvvm

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.preferencedatastoremvvm.databinding.ActivityMainBinding
import com.example.preferencedatastoremvvm.viewmodel.DataViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: DataViewModel by viewModels()

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)


        binding.saveBtn.setOnClickListener {
            val name = binding.nameEt.text.toString()
            val age = Integer.parseInt(binding.ageEt.text.toString())

            viewModel.saveData(name, age)
        }

        binding.dispTv.setOnClickListener{
            binding.dispTv.text = viewModel.getData()
        }


        setContentView(binding.root)
    }
}