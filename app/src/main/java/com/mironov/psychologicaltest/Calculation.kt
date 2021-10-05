package com.mironov.psychologicaltest

import android.os.Build
import androidx.annotation.RequiresApi
import com.mironov.psychologicaltest.model.Question


class Calculation {

    private var resultsMap= HashMap<String?,Int>()

    @RequiresApi(Build.VERSION_CODES.N)
    fun addAnswer(question:Question?, userAnswer:String, inc:Int){

        if(userAnswer.equals(question?.answer)) {
            var oldVal = resultsMap.getOrDefault(question?.type, 0)
            resultsMap.put(question?.type, oldVal + inc)
        }
    }

    fun getResultString():String{
        //return "Экстраверсия="+extr+"/ Нейротизм="+neur+"/ Лживость="+lie
        var str:String=""
        resultsMap.forEach{ (key, value) -> str=str+("$key = $value \n") }
        return str
    }

    fun resetCalc(){
        resultsMap.clear()
    }

}