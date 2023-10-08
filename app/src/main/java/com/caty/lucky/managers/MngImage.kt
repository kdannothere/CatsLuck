package com.caty.lucky.managers

import com.caty.lucky.R
import kotlin.random.Random

object MngImage {

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
                0 -> slot1_1
                1 -> slot1_2
                2 -> slot1_3
                3 -> slot1_4
                4 -> slot1_5
                else -> throw Exception(exceptionMsg)
            }

            2 -> when (imageId) {
                0 -> slot2_6
                1 -> slot2_7
                2 -> slot2_8
                3 -> slot2_9
                4 -> slot2_10
                5 -> slot2_11
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

    private const val exceptionMsg = "Unknown ImageId"
    // for Slots1
    private val slot1_1 = R.drawable.slot1_aquarium
    private val slot1_2 = R.drawable.slot1_blue
    private val slot1_3 = R.drawable.slot1_green
    private val slot1_4 = R.drawable.slot1_orange

    private val slot1_5 = R.drawable.slot1_red
    // for Slots2
    private val slot2_6 = R.drawable.cat1
    private val slot2_7 = R.drawable.cat2
    private val slot2_8 = R.drawable.cat3
    private val slot2_9 = R.drawable.cat4
    private val slot2_10 = R.drawable.question2

    private val slot2_11 = R.drawable.sdfsdg
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
}