package com.adrenaline.ofathlet.presentation.fragments

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.adrenaline.ofathlet.BestActivity
import com.adrenaline.ofathlet.R
import com.adrenaline.ofathlet.data.DataManager
import com.adrenaline.ofathlet.databinding.FragmentSettingsBinding
import com.adrenaline.ofathlet.presentation.GameViewModel
import com.adrenaline.ofathlet.presentation.utilities.MusicUtility
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: GameViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        setupSettings()

        binding.buttonMusic.setOnClickListener {
            playClickSound()
            changeMusicSetting()
            changeSoundSetting()
        }

        binding.buttonVibration.setOnClickListener {
            playClickSound()
            changeVibrationSetting()
        }

        binding.removeAccount.setOnClickListener {
            playClickSound()
            viewModel.removeAccount(requireContext())
            Toast.makeText(requireContext(), "Your account was deleted", Toast.LENGTH_SHORT).show()
        }

        binding.textButtonResetScore.setOnClickListener {
            playClickSound()
            viewModel.resetScore(requireContext())
            Toast.makeText(requireContext(), "Your scores were reset", Toast.LENGTH_SHORT).show()
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR
    }

    private fun changeMusicSetting() {
        viewModel.viewModelScope.launch(Dispatchers.IO) {
            if (viewModel.isMusicOn) {
                DataManager.saveMusicSetting(requireContext(), false)
                DataManager.saveSoundSetting(requireContext(), false)
                MusicUtility.pauseMusic(
                    (activity as BestActivity).musicPlayer,
                    this
                )
                viewModel.isMusicOn = false
                viewModel.isSoundOn = false
            } else {
                DataManager.saveMusicSetting(requireContext(), true)
                DataManager.saveSoundSetting(requireContext(), true)
                MusicUtility.playMusic(
                    (activity as BestActivity).musicPlayer,
                    requireContext(),
                    this
                )
                viewModel.isMusicOn = true
                viewModel.isSoundOn = true
            }
            setupSettings()
        }
    }

    private fun changeSoundSetting() {
        viewModel.viewModelScope.launch(Dispatchers.IO) {
            if (viewModel.isSoundOn) {
                DataManager.saveSoundSetting(requireContext(), false)
                viewModel.isSoundOn = false
            } else {
                DataManager.saveSoundSetting(requireContext(), true)
                viewModel.isSoundOn = true
                playClickSound()
            }
            setupSettings()
        }
    }

    private fun changeVibrationSetting() {
        viewModel.viewModelScope.launch(Dispatchers.IO) {
            if (viewModel.isVibrationOn) {
                DataManager.saveVibrationSetting(requireContext(), false)
                viewModel.isVibrationOn = false
            } else {
                DataManager.saveVibrationSetting(requireContext(), true)
                viewModel.isVibrationOn = true
                MusicUtility.doVibrate(requireContext(), lifecycleScope, viewModel.isVibrationOn)
            }
            setupSettings()
        }
    }

    private fun setupSettings() {
        viewModel.viewModelScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                // set music and sound
                if (viewModel.isMusicOn) {
                    binding.buttonMusic.setImageResource(R.drawable.volume_on)
                } else {
                    binding.buttonMusic.setImageResource(R.drawable.volume_off)
                }
                // set vibration
                if (viewModel.isVibrationOn) {
                    binding.buttonVibration.setImageResource(R.drawable.volume_on)
                } else {
                    binding.buttonVibration.setImageResource(R.drawable.volume_off)
                }
            }
        }
    }

    private fun playClickSound() {
        MusicUtility.playSound(
            mediaPlayer = (activity as BestActivity).soundPlayer,
            MusicUtility.soundClickResId,
            requireContext(),
            viewModel.viewModelScope,
            viewModel.isSoundOn,
            viewModel.isVibrationOn
        )
    }
}