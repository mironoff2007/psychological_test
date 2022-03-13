package com.mironov.psychologicaltest.repository

import android.R.attr.data
import android.content.Context
import android.widget.Toast
import com.mironov.psychologicaltest.R
import com.mironov.psychologicaltest.data.AnswerDatabase
import java.io.*


class DbSaveRead() {
    fun exportDatabase(path: String, context: Context) {

        val currentDBPath = AnswerDatabase.getDatabase(context)!!.openHelper.writableDatabase.path
        val backupDBPath =
            context.getString(R.string.export_db_file_name)
        val currentDB = File(currentDBPath)
        val backupDB = File(path, backupDBPath)
        if (currentDB.exists()) {
            try {
                val src = FileInputStream(currentDB).channel
                val dst = FileOutputStream(backupDB).channel
                dst.transferFrom(src, 0, src.size())
                src.close()
                dst.close()
                Toast.makeText(
                    context,
                    context.getString(R.string.saved_to_text) + path + backupDBPath,
                    Toast.LENGTH_LONG
                ).show()
            } catch (e: IOException) {
                e.printStackTrace()
                Toast.makeText(
                    context,
                    e.message,
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    fun exportAnswers(answers: String, path: String, context: Context) {

        val fileName =
            context.getString(R.string.export_answers_file_name)

        val file = File(path, fileName)
        val stream = FileOutputStream(file)
        val outWriter = OutputStreamWriter(stream)
        try {
            outWriter.append(answers)
            outWriter.flush()
            Toast.makeText(
                context,
                context.getString(R.string.saved_to_text) + path + fileName,
                Toast.LENGTH_LONG
            ).show()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        finally {
            stream.close()
        }

    }
}