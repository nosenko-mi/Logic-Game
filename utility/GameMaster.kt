package com.ltl.mpmp_lab3.utility

import android.content.Context
import com.ltl.mpmp_lab3.R
import com.ltl.mpmp_lab3.constants.AnswerOption
import java.io.Serializable
import java.util.*

class GameMaster(val context: Context) :Serializable {

    private var colorNames: Array<String> = context.resources.getStringArray(R.array.color_names_array)
    private var colors: IntArray = context.resources.getIntArray(R.array.game_colors_array)
    private val colorsMap = HashMap<String, Int>()

    private var generator = Random()

    var points: Int = 0
    var penalty: Int = 1

    init {
        require(colorNames.size == colors.size) { "The number of keys doesn't match the number of values." }
        for (i in colorNames.indices) {
            colorsMap[colorNames[i]] = colors[i]
        }
    }

    fun start(){
        points = 0
    }

    fun checkAnswer(answer: AnswerOption, currentText: CharSequence, currentColor: Int): Boolean {
        val expectedColor: Int = colorsMap[currentText]!!

        if (expectedColor == currentColor && answer == AnswerOption.YES) {
            points++
            return true
        } else if (expectedColor != currentColor && answer == AnswerOption.NO) {
            points++
            return true
        }

        points -= penalty
        if (points < 0) points = 0
        return false
    }

    fun shuffle(): IntArray {
        val text1: Int = generator.nextInt(colors.size)
        val color1: Int = generator.nextInt(colorNames.size)
        val text2: Int = generator.nextInt(colors.size)
        val color2: Int = generator.nextInt(colorNames.size)

        return intArrayOf(text1, color1, text2, color2)
    }


}