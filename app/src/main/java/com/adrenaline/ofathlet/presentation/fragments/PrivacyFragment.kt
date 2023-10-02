package com.adrenaline.ofathlet.presentation.fragments

import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
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
import com.adrenaline.ofathlet.databinding.FragmentPrivacyBinding
import com.adrenaline.ofathlet.presentation.GameViewModel
import com.adrenaline.ofathlet.presentation.utilities.MusicUtility
import com.adrenaline.ofathlet.presentation.utilities.ViewUtility
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PrivacyFragment : Fragment() {

    private var _binding: FragmentPrivacyBinding? = null
    private val binding get() = _binding!!
    private val viewModel: GameViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentPrivacyBinding.inflate(inflater, container, false)

        binding.buttonPrivacy.setOnClickListener {
            playClickSound()
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/"))
            startActivity(browserIntent)
        }

        binding.buttonYes.setOnClickListener {
            playClickSound()
            viewModel.privacy = true
            lifecycleScope.launch(Dispatchers.IO) {
                DataManager.savePrivacy(requireContext(), true)
            }
            findNavController().navigate(R.id.action_privacy_to_games)
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        viewModel.setWin(0)
        viewModel.resetPositions()
        viewModel.resetSlots()
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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR
    }
}