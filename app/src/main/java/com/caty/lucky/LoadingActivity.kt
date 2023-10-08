package com.caty.lucky

import android.annotation.SuppressLint
import kotlinx.coroutines.*
import java.util.*
import android.content.Intent
import android.content.pm.ActivityInfo
import android.webkit.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Build
import android.os.Bundle
import android.view.*
import io.michaelrocks.paranoid.Obfuscate

@Obfuscate
@SuppressLint("CustomSplashScreen")
class LoadingActivity : AppCompatActivity() {
    private val uiScope = CoroutineScope(Dispatchers.Main + Job())
    private val ioScope = CoroutineScope(Dispatchers.IO + Job())

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContentView(R.layout.activity_splash)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
        }

        toMainActivity()
    }

    private fun toMainActivity() {
        ioScope.launch {
            delay(3000)
            uiScope.launch {
                startActivity(Intent(this@LoadingActivity, GameActivity::class.java))
                finish()
            }
        }
    }

    override fun onDestroy() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(true)
        }
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR
        super.onDestroy()
    }
}