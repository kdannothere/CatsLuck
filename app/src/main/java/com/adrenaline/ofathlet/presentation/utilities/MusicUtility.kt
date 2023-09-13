package com.adrenaline.ofathlet.presentation.utilities

import android.content.Context
import android.media.MediaPlayer
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import com.adrenaline.ofathlet.R
import com.adrenaline.ofathlet.data.DataManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object MusicUtility {

    val musicResId = R.raw.music
    val soundWinResId = R.raw.win_sound
    val soundLoseResId = R.raw.lose_sound
    val soundClickResId = R.raw.click_sound

    fun playMusic(mediaPlayer: MediaPlayer, context: Context, scope: CoroutineScope) {
        scope.launch(Dispatchers.IO) {
            val isMusicOn = async { DataManager.loadMusicSetting(context) }
            if (!isMusicOn.await() || mediaPlayer.isPlaying) return@launch
            withContext(Dispatchers.Main) {
                mediaPlayer.isLooping = true
                mediaPlayer.start()
            }
        }
    }

    fun pauseMusic(mediaPlayer: MediaPlayer, scope: CoroutineScope) {
        if (mediaPlayer.isPlaying) {
            scope.launch(Dispatchers.Main) {
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
        scope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                if (rawResId == soundLoseResId || rawResId == soundWinResId) {
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
        scope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
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