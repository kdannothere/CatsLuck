package com.adrenaline.ofathlet.presentation.fragments

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.adrenaline.ofathlet.BestActivity
import com.adrenaline.ofathlet.R
import com.adrenaline.ofathlet.data.DataManager
import com.adrenaline.ofathlet.databinding.FragmentSettingsBinding
import com.adrenaline.ofathlet.presentation.GameViewModel
import com.adrenaline.ofathlet.presentation.utilities.MusicUtility
import com.adrenaline.ofathlet.presentation.utilities.ViewUtility
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
        }

        binding.volumeMusic.setOnClickListener {
            binding.buttonMusic.callOnClick()
        }

        binding.buttonSound.setOnClickListener {
            playClickSound()
            changeSoundSetting()
        }

        binding.volumeSound.setOnClickListener {
            binding.buttonSound.callOnClick()
        }

        binding.switchVibration.setOnClickListener {
            playClickSound()
            changeVibrationSetting()
        }

        binding.textButtonResetScore.setOnClickListener {
            playClickSound()
            viewModel.resetScore(requireContext())
        }

        binding.buttonBack.setOnClickListener {
            playClickSound()
            findNavController().navigateUp()
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            // fixing auto text feature for older Android APIs
            ViewUtility.apply {
                makeTextAutoSize(binding.textVibration)
                makeTextAutoSize(binding.textButtonResetScore)
            }
        }

        return binding.root
    }

    private fun changeMusicSetting() {
        viewModel.viewModelScope.launch(Dispatchers.IO) {
            if (viewModel.isMusicOn) {
                DataManager.saveMusicSetting(requireContext(), false)
                MusicUtility.pauseMusic(
                    (activity as BestActivity).musicPlayer,
                    this
                )
                viewModel.isMusicOn = false
            } else {
                DataManager.saveMusicSetting(requireContext(), true)
                MusicUtility.playMusic(
                    (activity as BestActivity).musicPlayer,
                    requireContext(),
                    this
                )
                viewModel.isMusicOn = true
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
                // set music
                if (viewModel.isMusicOn) {
                    binding.volumeMusic.setImageResource(R.drawable.volume_max)
                } else {
                    binding.volumeMusic.setImageResource(R.drawable.volume_min)
                }
                // set sound
                if (viewModel.isSoundOn) {
                    binding.volumeSound.setImageResource(R.drawable.volume_max)
                } else {
                    binding.volumeSound.setImageResource(R.drawable.volume_min)
                }
                // set vibration
                binding.switchVibration.isChecked = viewModel.isVibrationOn
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