package com.adrenaline.ofathlet.presentation.fragments

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
import com.adrenaline.ofathlet.databinding.FragmentGamesBinding
import com.adrenaline.ofathlet.presentation.GameViewModel
import com.adrenaline.ofathlet.presentation.utilities.MusicUtility
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class GamesFragment : Fragment() {

    private var _binding: FragmentGamesBinding? = null
    private val binding get() = _binding!!
    private val viewModel: GameViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentGamesBinding.inflate(inflater, container, false)

        viewModel.lvl.onEach { newValue ->
            binding.lvlValue.text = newValue.toString()
        }.launchIn(lifecycleScope)

        binding.buttonSettings.setOnClickListener {
            playClickSound()
            findNavController().navigate(R.id.action_games_to_settings)
        }

        binding.buttonRegister.setOnClickListener {
            playClickSound()
            findNavController().navigate(R.id.action_games_to_email)
        }

        binding.game1.setOnClickListener {
            playClickSound()
            findNavController().navigate(R.id.game_1)
        }

        binding.game2.setOnClickListener {
            playClickSound()
            findNavController().navigate(R.id.game_2)
        }

        binding.game3.setOnClickListener {
            playClickSound()
            findNavController().navigate(R.id.game_3)
        }

        binding.game4.setOnClickListener {
            playClickSound()
            findNavController().navigate(R.id.game_4)
        }

        binding.game5.setOnClickListener {
            playClickSound()
            findNavController().navigate(R.id.game_5)
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        viewStateHandling()
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

    private fun viewStateHandling() {
        when {
            viewModel.isUserLoggedIn -> {
                binding.apply {
                    buttonRegister.visibility = View.GONE
                    lvl.visibility = View.VISIBLE
                    lvlValue.visibility = View.VISIBLE
                }
            }
            else -> {
                binding.apply {
                    buttonRegister.visibility = View.VISIBLE
                    lvl.visibility = View.GONE
                    lvlValue.visibility = View.GONE
                }
            }
        }
    }
}