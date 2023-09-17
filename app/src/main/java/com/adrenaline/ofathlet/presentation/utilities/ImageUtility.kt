package com.adrenaline.ofathlet.presentation.utilities

import com.adrenaline.ofathlet.R
import kotlin.random.Random

object ImageUtility {

    private const val exceptionMsg = "Unknown ImageId"

    // for Slots1
    private val drawable1 = R.drawable.slot1_aquarium
    private val drawable2 = R.drawable.slot1_blue
    private val drawable3 = R.drawable.slot1_green
    private val drawable4 = R.drawable.slot1_orange
    private val drawable5 = R.drawable.slot1_red

    // for Slots2
    private val drawable6 = R.drawable.cat1
    private val drawable7 = R.drawable.cat2
    private val drawable8 = R.drawable.cat3
    private val drawable9 = R.drawable.cat4
    private val drawable10 = R.drawable.question2
    private val drawable11 = R.drawable.sdfsdg

    // for Miner1
    private val drawable12 = R.drawable.question_round
    private val drawable13 = R.drawable.s2
    private val drawable14 = R.drawable.s3
    private val drawable15 = R.drawable.s4
    private val drawable16 = R.drawable.s1

    // for Miner2
    private val drawable17 = R.drawable.question_square
    private val drawable18 = R.drawable.mmmkkk
    private val drawable19 = R.drawable.mmmmaaa
    private val drawable20 = R.drawable.mmmjjj

    fun getRandomImageId(gameId: Int): Int {
        return when (gameId) {
            1, 3 -> Random.nextInt(0, 5)
            2 -> Random.nextInt(0, 6)
            4 -> Random.nextInt(0, 4)
            else -> throw Exception(exceptionMsg)
        }
    }

    fun getDrawableId(imageId: Int, gameId: Int = 1): Int {
        return when (gameId) {
            1 -> when (imageId) {
                0 -> drawable1
                1 -> drawable2
                2 -> drawable3
                3 -> drawable4
                4 -> drawable5
                else -> throw Exception(exceptionMsg)
            }
            2 -> when (imageId) {
                0 -> drawable6
                1 -> drawable7
                2 -> drawable8
                3 -> drawable9
                4 -> drawable10
                5 -> drawable11
                else -> throw Exception(exceptionMsg)
            }
            3 -> when (imageId) {
                0 -> drawable12
                1 -> drawable13
                2 -> drawable14
                3 -> drawable15
                4 -> drawable16
                else -> throw Exception(exceptionMsg)
            }
            4 -> when (imageId) {
                0 -> drawable17
                1 -> drawable18
                2 -> drawable19
                3 -> drawable20
                else -> throw Exception(exceptionMsg)
            }
            else -> throw Exception(exceptionMsg)
        }
    }
}