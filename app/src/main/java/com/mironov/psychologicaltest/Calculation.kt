package com.mironov.psychologicaltest

import com.mironov.psychologicaltest.model.Question


class Calculation {

    private var resultsMap = HashMap<String?, Int>()

    fun addAnswer(question: Question?, userAnswer: String, inc: Int) {
        val type = question?.type

        if (type?.contains(',') == true) {
            //If question has multiple types
            if (userAnswer ==  question?.answer) {
                type.split(",").forEach { v ->
                    val oldVal = resultsMap[v] ?: 0
                    resultsMap[v] = oldVal + inc
                }
            }
        }
        else{
            //Question has one types
            if (userAnswer == question?.answer) {
            val oldVal = resultsMap[type] ?: 0
            resultsMap[type] = oldVal + inc
            }
        }
    }

    fun getResultString(): String {
        var str = ""
        resultsMap.forEach { (key, value) -> str = "$str$key = $value \n" }
        return str
    }

    fun resetCalc() {
        resultsMap.clear()
    }

}