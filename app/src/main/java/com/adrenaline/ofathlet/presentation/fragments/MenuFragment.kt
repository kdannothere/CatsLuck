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
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.adrenaline.ofathlet.BestActivity
import com.adrenaline.ofathlet.R
import com.adrenaline.ofathlet.data.DataManager
import com.adrenaline.ofathlet.databinding.FragmentMenuBinding
import com.adrenaline.ofathlet.presentation.GameViewModel
import com.adrenaline.ofathlet.presentation.utilities.MusicUtility
import com.adrenaline.ofathlet.presentation.utilities.ViewUtility
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
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

    override fun onResume() {
        super.onResume()
        loadData()
        viewModel.setWin(0)
        viewModel.resetSlotPositions()
        viewModel.resetSlots()
    }

    private fun loadData() {
        lifecycleScope.launch(Dispatchers.IO) {
            val isPrivacyAccepted = async { DataManager.loadPrivacy(requireContext()) }
            val winNumber =
                async { DataManager.loadWinNumber(requireContext(), viewModel.winNumber.value) }

            val lvl = async { DataManager.loadLvl(requireContext(), viewModel.lvl.value) }
            viewModel.privacy = isPrivacyAccepted.await()
            viewModel.setWinNumber(winNumber.await(), requireContext())
            viewModel.setLvl(lvl.await(), requireContext())
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