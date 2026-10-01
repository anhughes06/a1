package com.example.anhughes_rapidrecall

class Sequence(val numDigits: Int) {

    val numericalSequence: MutableList<Int> = mutableListOf<Int>()
    var inputSequence: MutableList<Int> = mutableListOf<Int>()

    var correct: Boolean = false

    init {

        for (i in 1..numDigits) {

            numericalSequence.add((0..9).random())

        }

    }

    fun get(idx: Int): Int {
        return numericalSequence[idx]
    }

    fun getTargetString(): String {

        var retStr = ""

        for (digit in numericalSequence) {
            retStr += digit.toString()
        }

        return retStr

    }

    fun getInputString(): String {

        var retStr = ""

        for (digit in inputSequence) {
            retStr += digit.toString()
        }

        return retStr

    }

}