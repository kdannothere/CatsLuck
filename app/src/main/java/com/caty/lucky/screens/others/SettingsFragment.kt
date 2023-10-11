package com.caty.lucky.screens.others

import android.annotation.SuppressLint
import androidx.fragment.app.activityViewModels
import android.os.Bundle
import android.view.LayoutInflater
import android.content.pm.ActivityInfo
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import android.view.View
import com.caty.lucky.managers.MngTheMusic
import com.caty.lucky.managers.MngView
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.caty.lucky.R
import com.caty.lucky.databinding.FragmentSettingsBinding
import com.caty.lucky.managers.MngData
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.caty.lucky.CatApp
import com.caty.lucky.CatViewModel
import com.caty.lucky.GameActivity


class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CatViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        setupSettings()

        binding.buttonMusic.setOnClickListener {
            MngView.playClickSound(requireActivity(), viewModel, requireContext())
            changeMusicSetting()
        }

        binding.buttonVibration.setOnClickListener {
            MngView.playClickSound(requireActivity(), viewModel, requireContext())
            changeVibroSetting()
        }

        binding.removeAccount.setOnClickListener {
            MngView.playClickSound(requireActivity(), viewModel, requireContext())
            viewModel.removeAccount(requireContext())
            Toast.makeText(requireContext(), "Your account was deleted", Toast.LENGTH_SHORT).show()
        }

        binding.textButtonResetScore.setOnClickListener {
            MngView.playClickSound(requireActivity(), viewModel, requireContext())
            viewModel.resetScore(requireContext())
            Toast.makeText(requireContext(), "Your scores were reset", Toast.LENGTH_SHORT).show()
        }

        return binding.root
    }

    @SuppressLint("SourceLockedOrientationActivity")
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
        lifecycleScope.launch(CatApp.dispatcherIO) {
            if (viewModel.isMusicSetOn) {
                MngData.saveMusicSetting(requireContext(), false)
                MngTheMusic.pauseTheMusic(
                    (activity as GameActivity).musicPlayer,
                    this
                )
                viewModel.isMusicSetOn = false
            } else {
                MngData.saveMusicSetting(requireContext(), true)
                MngTheMusic.playTheMusic(
                    this,
                    (activity as GameActivity).musicPlayer,
                    requireContext()
                )
                viewModel.isMusicSetOn = true
            }
            setupSettings()
        }
    }

    private fun changeVibroSetting() {
        lifecycleScope.launch(CatApp.dispatcherIO) {

            if (viewModel.isVibroSetOn) {

                MngData.saveVibrationSetting(requireContext(), false)
                viewModel.isVibroSetOn = false
            } else {

                MngData.saveVibrationSetting(requireContext(), true)
                viewModel.isVibroSetOn = true
                MngTheMusic.doVibrate(requireContext(), lifecycleScope, viewModel.isVibroSetOn)
            }
            setupSettings()
        }
    }

    private fun setupSettings() {
        viewModel.viewModelScope.launch(CatApp.dispatcherIO) {
            withContext(CatApp.dispatcherMain) {

                // set music and sound
                if (viewModel.isMusicSetOn) {
                    binding.buttonMusic.setImageResource(R.drawable.volume_on)
                } else {
                    binding.buttonMusic.setImageResource(R.drawable.volume_off)
                }

                // set vibration
                if (viewModel.isVibroSetOn) {
                    binding.buttonVibration.setImageResource(R.drawable.volume_on)
                } else {
                    binding.buttonVibration.setImageResource(R.drawable.volume_off)
                }
            }
        }
    }
}