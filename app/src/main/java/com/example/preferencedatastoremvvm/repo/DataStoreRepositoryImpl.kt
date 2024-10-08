package com.example.preferencedatastoremvvm.repo

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first
import javax.inject.Inject


class DataStoreRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : OperationRepository {



    private val TAG = "DataStoreRepositoryImpl"
    override suspend fun putString(key: String, value: String) {
        Log.d(TAG, "putString: ")
        val preferencesKey = stringPreferencesKey(key)
        dataStore.edit { preferences ->
            preferences[preferencesKey] = value
        }
    }

    override suspend fun putInt(key: String, value: Int) {
        Log.d(TAG, "putInt: ")
        val preferencesKey = intPreferencesKey(key)
        dataStore.edit { preferences ->
            preferences[preferencesKey] = value
        }
    }

    override suspend fun getString(key: String): String? {
        Log.d(TAG, "getString: ")
        return try {
            val preferencesKey = stringPreferencesKey(key)
            val preferences = dataStore.data.first()
            preferences[preferencesKey]
        }catch (e: Exception){
            e.printStackTrace()
            null
        }
    }

    override suspend fun getInt(key: String): Int? {
        Log.d(TAG, "getInt: ")
        return try {
            val preferencesKey = intPreferencesKey(key)
            val preferences = dataStore.data.first()
            preferences[preferencesKey]
        }catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}