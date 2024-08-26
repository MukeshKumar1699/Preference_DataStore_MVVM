package com.example.preferencedatastoremvvm.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.preferencedatastoremvvm.repo.DataStoreRepositoryImpl
import com.example.preferencedatastoremvvm.repo.OperationRepository
import com.example.preferencedatastoremvvm.repo.SharedPreferenceMigrationRepoImpl
import com.example.preferencedatastoremvvm.repo.SharedPreferenceRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class DataViewModel @Inject constructor(
    private val repository: DataStoreRepositoryImpl
): ViewModel() {

    val NAME = "name"
    val AGE = "age"

    fun saveData(name: String, age: Int) {

        saveName(name)
        saveAge(age)
    }

    fun getData(): String  {
        return "${getName()} ${getAge()}"
    }

    private fun saveName(value: String) {
        viewModelScope.launch {
            repository.putString(NAME, value)
        }
    }

    private fun getName(): String? = runBlocking {
        repository.getString(NAME)
    }

    private fun saveAge(value: Int) {
        viewModelScope.launch {
            repository.putInt(AGE, value)
        }
    }

    private fun getAge(): Int? = runBlocking {
        repository.getInt(AGE)
    }
}

//class DataViewModelFactory(
//    private val repository: OperationRepository
//) : ViewModelProvider.Factory {
//
//    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//        if (modelClass.isAssignableFrom(DataViewModel::class.java)) {
//            @Suppress("UNCHECKED_CAST")
//            return DataViewModel(repository) as T
//        }
//        throw IllegalArgumentException("Unknown ViewModel class")
//    }
//}