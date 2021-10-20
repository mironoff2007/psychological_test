package com.mironov.psychologicaltest.repository

import java.io.*
import java.nio.charset.Charset

class TextCreator {

    lateinit var  writer:OutputStreamWriter

    fun createTextFile( path: String?) {
        try {
            writer = OutputStreamWriter(FileOutputStream(path), Charset.forName("UTF-8"))
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun appendText(text:String){
        writer.append(text)
        writer.flush()
    }

    fun closeTextFile(){
        writer.close()
    }

}