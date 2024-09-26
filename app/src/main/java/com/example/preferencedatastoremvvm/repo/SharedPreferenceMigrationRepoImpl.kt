package com.example.preferencedatastoremvvm.repo

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStoreFile
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Named

class SharedPreferenceMigrationRepoImpl @Inject constructor(
    private val context: Context,
    @Named("pref_name") private val preferenceName: String

) : OperationRepository {

    private val TAG = "SharedPreferenceMigration"

    private val Context.dataStore by lazy {
        PreferenceDataStoreFactory.create(
            migrations = listOf(SharedPreferencesMigration(context, preferenceName)),
            produceFile = { context.preferencesDataStoreFile("New_dataStore_Migration") }
        )
    }

    private fun readStringFromDataStore(key: String): String? {
        Log.d(TAG, "readStringFromDataStore: ")
        val dataStoreKey = stringPreferencesKey(key)
        val dataStore = context.dataStore

        return runBlocking {
            val preferences = dataStore.data.first()
            preferences[dataStoreKey]
        }
    }

    private fun readIntFromDataStore(key: String): Int? {
        Log.d(TAG, "readIntFromDataStore: ")
        val dataStoreKey = intPreferencesKey(key)
        val dataStore = context.dataStore

        return runBlocking {
            val preferences = dataStore.data.first()
            preferences[dataStoreKey]
        }
    }

    fun writeStringToDataStore(key: String, value: String) {
        Log.d(TAG, "writeStringToDataStore: ")
        val dataStoreKey = stringPreferencesKey(key)
        val dataStore = context.dataStore

        runBlocking {
            dataStore.edit { settings ->
                settings[dataStoreKey] = value
            }
        }
    }

    fun writeIntToDataStore(key: String, value: Int) {
        val dataStoreKey = intPreferencesKey(key)
        val dataStore = context.dataStore
        Log.d(TAG, "writeIntToDataStore: ")

        runBlocking {
            dataStore.edit { settings ->
                settings[dataStoreKey] = value
            }
        }
    }

    override suspend fun putString(key: String, value: String) {
        Log.d(TAG, "putString: ")
        writeStringToDataStore(key, value)
    }

    override suspend fun putInt(key: String, value: Int) {
        Log.d(TAG, "putInt: ")
        writeIntToDataStore(key, value)
    }

    override suspend fun getString(key: String): String? {
        Log.d(TAG, "getString: ")
        return readStringFromDataStore(key)
    }

    override suspend fun getInt(key: String): Int? {
        Log.d(TAG, "getInt: ")
        return readIntFromDataStore(key)
    }


}