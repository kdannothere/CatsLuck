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
    val miner1_0 = R.drawable.m_0
    val miner1_1 = R.drawable.m_1
    val miner1_2 = R.drawable.m_2
    val miner1_3 = R.drawable.m_3
    val miner1_4 = R.drawable.m_4

    // for Miner2
    val miner2_0 = R.drawable.m2_0
    val miner2_1 = R.drawable.m2_1
    val miner2_2 = R.drawable.m2_2
    val miner2_3 = R.drawable.m2_3

    fun getRandomImageId(gameId: Int): Int {
        return when (gameId) {
            1 -> Random.nextInt(0, 5)
            2 -> Random.nextInt(0, 6)
            3 -> Random.nextInt(1, 5)
            4 -> Random.nextInt(1, 4)
            else -> throw Exception(exceptionMsg)
        }
    }

    fun getDrawableById(imageId: Int, gameId: Int = 1): Int {
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
                0 -> miner1_0
                1 -> miner1_1
                2 -> miner1_2
                3 -> miner1_3
                4 -> miner1_4
                else -> throw Exception(exceptionMsg)
            }

            4 -> when (imageId) {
                0 -> miner2_0
                1 -> miner2_1
                2 -> miner2_2
                3 -> miner2_3
                else -> throw Exception(exceptionMsg)
            }

            else -> throw Exception(exceptionMsg)
        }
    }
}