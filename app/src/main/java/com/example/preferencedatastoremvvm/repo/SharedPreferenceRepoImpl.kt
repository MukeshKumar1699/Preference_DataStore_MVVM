package com.example.preferencedatastoremvvm.repo

import android.content.SharedPreferences
import android.util.Log
import javax.inject.Inject
import javax.inject.Named

class SharedPreferenceRepositoryImpl @Inject constructor(
    private val preferences: SharedPreferences,
): OperationRepository {

    private val editor = preferences.edit()
    private val TAG = "SharedPreferenceRepoImp"

    override suspend fun putString(key: String, value: String) {
        Log.d(TAG, "putString: ")
        editor.putString(key, value)
        editor.commit()
    }

    override suspend fun putInt(key: String, value: Int) {
        Log.d(TAG, "putInt: ")
        editor.putInt(key, value)
        editor.commit()    }

    override suspend fun getString(key: String): String? {
        Log.d(TAG, "getString: ")
        return preferences.getString(key, null)
    }

    override suspend fun getInt(key: String): Int {
        Log.d(TAG, "getInt: ")
        return preferences.getInt(key, -1)
    }
}