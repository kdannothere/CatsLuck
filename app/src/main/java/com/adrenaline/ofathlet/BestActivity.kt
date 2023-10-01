package com.adrenaline.ofathlet

import android.app.AlertDialog
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.adrenaline.ofathlet.data.DataManager
import com.adrenaline.ofathlet.presentation.utilities.MusicUtility
import com.google.android.material.textview.MaterialTextView
import io.michaelrocks.paranoid.Obfuscate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Obfuscate
class BestActivity : AppCompatActivity() {

    val musicPlayer: MediaPlayer by lazy {
        MediaPlayer.create(this, MusicUtility.musicResId)
    }
    val soundPlayer: MediaPlayer by lazy {
        MediaPlayer()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContentView(R.layout.activity_best)
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

    override fun onPause() {
        super.onPause()
        MusicUtility.pauseMusic(musicPlayer, lifecycleScope)
    }

    override fun onResume() {
        super.onResume()
        MusicUtility.playMusic(musicPlayer, applicationContext, lifecycleScope)
    }

    override fun onSupportNavigateUp(): Boolean {
        return Navigation.findNavController(this, R.id.nav_host_fragment_container).navigateUp()
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
            playClickSound()
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun playClickSound() {
        lifecycleScope.launch(Dispatchers.IO) {
            MusicUtility.playSound(
                soundPlayer,
                MusicUtility.soundClickResId,
                applicationContext,
                this,
                isSoundOn = DataManager.loadSoundSetting(applicationContext),
                isVibrateOn = DataManager.loadVibrationSetting(applicationContext)
            )
        }
    }
}