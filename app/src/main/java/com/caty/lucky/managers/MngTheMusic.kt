package com.caty.lucky.managers

import android.content.Context
import android.media.MediaPlayer
import android.os.Build
import android.os.VibratorManager
import com.caty.lucky.CatApp
import kotlinx.coroutines.CoroutineScope
import android.os.VibrationEffect
import android.os.Vibrator
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import kotlinx.coroutines.launch
import com.caty.lucky.R

object MngTheMusic {

    val soundClickRes = R.raw.sound1_click
    val soundLoseRes = R.raw.sound3_lose
    val soundWinRes = R.raw.sound2_win
    val musicRes = R.raw.music1

    fun playTheMusic(scope: CoroutineScope, mediaPlayer: MediaPlayer, context: Context) {
        scope.launch(CatApp.dispatcherIO) {
            val isMusicOn = async { MngData.loadMusicSetting(context) }
            if (!isMusicOn.await() || mediaPlayer.isPlaying) return@launch
            withContext(CatApp.dispatcherMain) {
                mediaPlayer.isLooping = true
                mediaPlayer.start()
            }
        }
    }

    fun pauseTheMusic(mediaPlayer: MediaPlayer, scope: CoroutineScope) {
        if (mediaPlayer.isPlaying) {
            scope.launch(CatApp.dispatcherMain) {
                mediaPlayer.pause()
            }
        }
    }

    fun playSound(
        mediaPlayer: MediaPlayer,
        rawResId: Int,
        context: Context,
        scope: CoroutineScope,
        isSoundOn: Boolean,
        isVibrateOn: Boolean,
    ) {
        if (!isSoundOn) return
        scope.launch(CatApp.dispatcherIO) {
            withContext(CatApp.dispatcherMain) {
                if (rawResId == soundLoseRes || rawResId == soundWinRes) {
                    doVibrate(
                        context,
                        scope,
                        isVibrateOn
                    )
                }
                val assetFileDescriptor =
                    context.resources.openRawResourceFd(rawResId) ?: return@withContext
                mediaPlayer.reset()
                mediaPlayer.setDataSource(
                    assetFileDescriptor.fileDescriptor,
                    assetFileDescriptor.startOffset,
                    assetFileDescriptor.length
                )
                assetFileDescriptor.close()
                mediaPlayer.prepareAsync()
            }
        }
    }

    fun doVibrate(context: Context, scope: CoroutineScope, isVibrateOn: Boolean) {
        if (!isVibrateOn) return
        scope.launch(CatApp.dispatcherIO) {
            withContext(CatApp.dispatcherMain) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    val vibratorManager =
                        context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                    val vibrator = vibratorManager.defaultVibrator
                    vibrator.vibrate(
                        VibrationEffect.createOneShot(
                            500,
                            VibrationEffect.DEFAULT_AMPLITUDE
                        )
                    )
                } else {
                    @Suppress("DEPRECATION")
                    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        vibrator.vibrate(
                            VibrationEffect.createOneShot(
                                500,
                                VibrationEffect.DEFAULT_AMPLITUDE
                            )
                        )
                    } else {
                        @Suppress("DEPRECATION")
                        vibrator.vibrate(500)
                    }
                }
            }
        }
    }
}