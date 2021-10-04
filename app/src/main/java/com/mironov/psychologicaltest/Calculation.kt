package com.mironov.psychologicaltest

import com.mironov.psychologicaltest.model.Question

class Calculation {


    var extr=0
    var neur=0
    var lie=0

    fun addAnswer(question:Question?,userAnswer:String,inc:Int){
        when(question?.type) {
            "extr" -> {
                if(userAnswer.equals(question?.answer)){
                    extr += inc
                }
            }
            "neur" -> {
                if(userAnswer.equals(question?.answer)){
                    neur += inc
                }

            }
            "lie" -> {
                if(userAnswer.equals(question?.answer)){
                    lie += inc
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