package com.mironov.psychologicaltest.repository

import android.content.Context
import android.util.Log
import com.mironov.psychologicaltest.data.AnswerDatabase
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

class DbSaveRead() {
    fun exportDatabase(path: String, context: Context) {

        val currentDBPath = AnswerDatabase.getDatabase(context)!!.openHelper.writableDatabase.path
        val backupDBPath =
            "exported_db.sqlite"      //you can modify the file type you need to export
        val currentDB = File(currentDBPath)
        val backupDB = File(path, backupDBPath)
        if (currentDB.exists()) {
            try {
                val src = FileInputStream(currentDB).channel
                val dst = FileOutputStream(backupDB).channel
                dst.transferFrom(src, 0, src.size())
                src.close()
                dst.close()
                Log.d("My_tag", "saved to -$path")
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}