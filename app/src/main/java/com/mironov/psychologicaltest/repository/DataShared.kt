package com.mironov.psychologicaltest.repository

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64

class DataShared(context: Context,dataName:String) {

    private val pref: SharedPreferences = context.getSharedPreferences(dataName, Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = pref.edit()

    fun saveString(str: String,strName:String ) {
        editor.putString(strName, str).apply()
    }

    fun getString(strName: String): String {
        return pref.getString(strName,"").toString()
    }

    fun saveByteArray(bArray:ByteArray,fieldName:String ) {
        editor.putString(fieldName,  Base64.encodeToString(bArray, Base64.NO_WRAP)).apply()
    }

    fun getByteArray(fieldName:String):ByteArray {
        val str=pref.getString(fieldName,"")
        return Base64.decode(str, Base64.NO_WRAP)
    }

    fun clearPrefs() {
        editor.clear().commit()
    }
}