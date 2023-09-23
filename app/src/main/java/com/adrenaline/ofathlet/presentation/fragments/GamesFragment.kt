package com.adrenaline.ofathlet.presentation.fragments

import android.content.pm.ActivityInfo
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
            findNavController().navigate(R.id.action_games_to_gameSlot1)
        }

        binding.game2.setOnClickListener {
            playClickSound()
            findNavController().navigate(R.id.action_games_to_gameSlot2)
        }

        binding.game3.setOnClickListener {
            playClickSound()
            findNavController().navigate(R.id.action_games_to_gameBonus)
        }

        binding.game4.setOnClickListener {
            playClickSound()
            findNavController().navigate(R.id.action_games_to_gameMiner1)
        }

        binding.game5.setOnClickListener {
            playClickSound()
            findNavController().navigate(R.id.action_games_to_gameMiner2)
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        if (!viewModel.privacy) {
            findNavController().navigateUp()
        }
        viewStateHandling()
        viewModel.setWin(0)
        viewModel.resetPositions()
        viewModel.resetSlots()
        viewModel.isFinished = true
        viewModel.isFinishing = false
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
                    buttonRegister.apply {
                        visibility = View.INVISIBLE
                        isEnabled = false
                    }
                    barLvl.visibility = View.VISIBLE
                    game3.apply {
                        setImageResource(R.drawable.game_3_unlocked)
                        isClickable = true
                    }
                    game4.apply {
                        setImageResource(R.drawable.game_4_unlocked)
                        isClickable = true
                    }
                }
            }
            else -> {
                binding.apply {
                    buttonRegister.apply {
                        visibility = View.VISIBLE
                        isEnabled = true
                    }
                    barLvl.visibility = View.INVISIBLE
                    game3.apply {
                        setImageResource(R.drawable.game_3_locked)
                        isClickable = false
                    }
                    game4.apply {
                        setImageResource(R.drawable.game_4_locked)
                        isClickable = false
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR
    }
}