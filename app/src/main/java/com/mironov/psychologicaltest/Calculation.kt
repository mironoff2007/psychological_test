package com.mironov.psychologicaltest

import com.mironov.psychologicaltest.model.Question

class Calculation {

    var extr=0
    var neur=0
    var lie=0

    fun addAnswer(question:Question?,userAnswer:String){
        when(question?.type) {
            "extr" -> {
                if(userAnswer.equals(question?.answer)){
                    extr++
                }
            }
            "neur" -> {
                if(userAnswer.equals(question?.answer)){
                    neur++
                }

            }
            "lie" -> {
                if(userAnswer.equals(question?.answer)){
                    lie++
                }
            }
        }
    }

    fun getResultString():String{
        return "Экстраверсия="+extr+"/ Нейротизм="+neur+"/ Лживость="+lie
    }

    fun resetCalc(){
        extr=0
        neur=0
        lie=0
    }

}