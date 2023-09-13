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

        binding.buttonBack.setOnClickListener {
            playClickSound()
            findNavController().navigateUp()
        }

        binding.buttonGame1.setOnClickListener {
            playClickSound()
            findNavController().navigate(R.id.action_MenuFragment_to_Game1Fragment)
        }

        binding.buttonGame2.setOnClickListener {
            playClickSound()
            findNavController().navigate(R.id.action_MenuFragment_to_Game2Fragment)
        }

        binding.buttonGameBonus.setOnClickListener {
            playClickSound()
            findNavController().navigate(R.id.action_MenuFragment_to_GameBonusFragment)
        }

        binding.buttonSettings.setOnClickListener {
            playClickSound()
            findNavController().navigate(R.id.action_MenuFragment_to_SettingsFragment)
        }

        binding.linkPrivacy.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/"))
            startActivity(browserIntent)
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            // fix for auto text feature for older Android APIs
            ViewUtility.apply {
                makeTextAutoSize(binding.linkPrivacy)
                makeTextAutoSize(binding.titleGame1)
                makeTextAutoSize(binding.titleGame2)
                makeTextAutoSize(binding.titleGameBonus)
            }
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