package com.mironov.currency_converter.data

import android.content.Context
import android.content.SharedPreferences

class DataShared(context: Context) {

    private val pref: SharedPreferences = context.getSharedPreferences("Results", Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = pref.edit()

    fun saveResults(result: String,resultsName:String ) {
        editor.putString(resultsName, result).apply()
    }

    fun getResult(resultsName: String): String {
        return pref.getString(resultsName,"").toString()
    }

    fun clearPrefs() {
        editor.clear().commit()
    }

}