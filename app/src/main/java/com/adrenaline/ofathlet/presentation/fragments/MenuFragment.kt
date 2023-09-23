package com.adrenaline.ofathlet.presentation.fragments

import android.content.pm.ActivityInfo
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
import com.adrenaline.ofathlet.databinding.FragmentMenuBinding
import com.adrenaline.ofathlet.presentation.GameViewModel
import com.adrenaline.ofathlet.presentation.utilities.MusicUtility
import kotlin.system.exitProcess

class MenuFragment : Fragment() {

    private var _binding: FragmentMenuBinding? = null
    private val binding get() = _binding!!
    private val viewModel: GameViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentMenuBinding.inflate(inflater, container, false)

        binding.games.setOnClickListener {
            playClickSound()
            when (viewModel.privacy) {
                true -> findNavController().navigate(R.id.action_menu_to_games)
                false -> findNavController().navigate(R.id.action_menu_to_privacy)
            }
        }

        binding.settings.setOnClickListener {
            playClickSound()
            findNavController().navigate(R.id.action_menu_to_settings)
        }

        binding.privacy.setOnClickListener {
            playClickSound()
            findNavController().navigate(R.id.action_menu_to_privacy)
        }

        binding.exit.setOnClickListener {
            exitProcess(0)
        }

        return binding.root
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

    override fun onResume() {
        super.onResume()
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR
    }
}