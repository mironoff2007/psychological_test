package com.mironov.psychologicaltest.model


class Calculation {

    private var resultsMap = HashMap<String?, Int>()

    fun addAnswer(question: Question?, userAnswer: String, incSign: Int) {
        val type = question?.type

        val inc=question!!.inc

        if (type?.contains(';') == true) {
            //If question has multiple types
            if (userAnswer == question.answer) {
                type.split(",").forEach { v ->
                    val oldVal = resultsMap[v] ?: 0
                    resultsMap[v] = oldVal + incSign* inc!!
                }
            }
        }
        else{
            //Question has one types
            if (userAnswer == question?.answer) {
            val oldVal = resultsMap[type] ?: 0
            resultsMap[type] = oldVal + incSign* inc!!
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