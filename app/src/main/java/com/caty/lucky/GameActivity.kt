package com.caty.lucky

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import android.os.Bundle
import android.app.AlertDialog
import android.content.pm.ActivityInfo
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.caty.lucky.managers.MngTheMusic
import io.michaelrocks.paranoid.Obfuscate
import androidx.appcompat.content.res.AppCompatResources
import com.caty.lucky.managers.MngView
import com.google.android.material.textview.MaterialTextView

@Obfuscate
class GameActivity : AppCompatActivity() {

    val soundPlayer: MediaPlayer by lazy {
        MediaPlayer()
    }

    val musicPlayer: MediaPlayer by lazy {
        MediaPlayer.create(this, MngTheMusic.musicRes)
    }

    private val viewModel: CatViewModel by viewModels()

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContentView(R.layout.activity_game)
        window.statusBarColor = Color.TRANSPARENT
        window.setBackgroundDrawable(
            AppCompatResources.getDrawable(
                applicationContext,
                R.color.black
            )
        )

        soundPlayer.setOnPreparedListener {
            soundPlayer.start()
        }

        soundPlayer.setOnCompletionListener {
            soundPlayer.reset()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return Navigation.findNavController(this, R.id.nav_host_fragment_container).navigateUp()
    }

    override fun onResume() {
        super.onResume()
        MngTheMusic.playTheMusic(lifecycleScope, musicPlayer, applicationContext)
    }

    override fun onPause() {
        super.onPause()
        MngTheMusic.pauseTheMusic(musicPlayer, lifecycleScope)
    }

    override fun onDestroy() {
        super.onDestroy()
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR
    }

    fun showDialog(message: String) {
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.msg_dialog, null)

        val builder = AlertDialog.Builder(this)
        builder.setView(dialogLayout)

        val dialog = builder.create()

        val displayMetrics = resources.displayMetrics
        val width = displayMetrics.widthPixels
        val height = displayMetrics.heightPixels

        dialog.window?.setLayout((width * 0.8).toInt(), (height * 0.8).toInt())

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val messageField = dialogLayout.findViewById<MaterialTextView>(R.id.message_field)

        messageField.text = message

        val button = dialogLayout.findViewById<ConstraintLayout>(R.id.button_ok)
        button.setOnClickListener {
            MngView.playClickSound(this, viewModel, applicationContext)
            dialog.dismiss()
        }

        dialog.show()
    }
}