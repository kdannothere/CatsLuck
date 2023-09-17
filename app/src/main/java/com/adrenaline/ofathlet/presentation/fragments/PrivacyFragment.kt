package com.adrenaline.ofathlet.presentation.fragments

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.adrenaline.ofathlet.BestActivity
import com.adrenaline.ofathlet.R
import com.adrenaline.ofathlet.databinding.FragmentPrivacyBinding
import com.adrenaline.ofathlet.presentation.GameViewModel
import com.adrenaline.ofathlet.presentation.utilities.MusicUtility
import com.adrenaline.ofathlet.presentation.utilities.ViewUtility

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
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/"))
            startActivity(browserIntent)
        }

        binding.buttonNo.setOnClickListener {
            playClickSound()
            findNavController().navigate(R.id.action_privacy_to_menu)
        }

        binding.buttonYes.setOnClickListener {
            playClickSound()
            findNavController().navigate(R.id.action_privacy_to_menu)
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        viewModel.setWin(0)
        viewModel.resetSlotPositions()
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
}