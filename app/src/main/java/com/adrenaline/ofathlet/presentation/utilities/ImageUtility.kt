package com.adrenaline.ofathlet.presentation.utilities

import com.adrenaline.ofathlet.R
import kotlin.random.Random

object ImageUtility {

    // for Slots game
    private const val drawableTen = R.drawable.slot_ten
    private const val drawableA = R.drawable.slot_a
    private const val drawableJ = R.drawable.slot_j
    private const val drawableK = R.drawable.slot_k
    private const val drawableQ = R.drawable.slot_q

    // for bonus game
    private const val drawableGold = R.drawable.card_gold
    private const val drawableMan = R.drawable.card_man
    private const val drawableBlue = R.drawable.card_horse_blue
    private const val drawablePurple = R.drawable.card_horse_purple
    private const val drawableRed = R.drawable.card_horse_red

    fun getRandomImageId(): Int {
        return Random.nextInt(0, 4)
    }

    fun getDrawableId(imageId: Int, isGame2: Boolean = false): Int {
        if (isGame2) {
            return when (imageId) {
                0 -> drawableGold
                1 -> drawableMan
                2 -> drawableBlue
                3 -> drawablePurple
                4 -> drawableRed
                else -> throw Exception("Unknown ImageId")
            }
        }
        return when (imageId) {
            0 -> drawableTen
            1 -> drawableA
            2 -> drawableJ
            3 -> drawableK
            4 -> drawableQ
            else -> throw Exception("Unknown ImageId")
        }
    }

//    fun getFieldHeightPort2(): Int {
//
//    }
//
//    fun getFieldHeightLand2(): Int {
//
//    }
}