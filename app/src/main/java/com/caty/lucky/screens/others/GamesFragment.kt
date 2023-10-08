package com.caty.lucky.screens.others

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.caty.lucky.R
import com.caty.lucky.databinding.FragmentGamesBinding
import com.caty.lucky.CatViewModel
import com.caty.lucky.managers.MngView
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class GamesFragment : Fragment() {

    private var _binding: FragmentGamesBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CatViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        if (!viewModel.privacy) {
            findNavController().navigate(R.id.action_games_to_privacy)
        }

        _binding = FragmentGamesBinding.inflate(inflater, container, false)


        viewModel.lvl.onEach { newValue ->
            binding.lvlValue.text = newValue.toString()
        }.launchIn(lifecycleScope)

        binding.buttonSettings.setOnClickListener {
            MngView.playClickSound(requireActivity(), viewModel, requireContext())
            findNavController().navigate(R.id.action_games_to_settings)
        }

        binding.buttonRegister.setOnClickListener {
            MngView.playClickSound(requireActivity(), viewModel, requireContext())
            findNavController().navigate(R.id.action_games_to_email)
        }

        binding.game1.setOnClickListener {
            MngView.playClickSound(requireActivity(), viewModel, requireContext())
            findNavController().navigate(R.id.action_games_to_gameSlot1)
        }

        binding.game2.setOnClickListener {
            MngView.playClickSound(requireActivity(), viewModel, requireContext())
            findNavController().navigate(R.id.action_games_to_gameSlot2)
        }

        binding.game3.setOnClickListener {
            MngView.playClickSound(requireActivity(), viewModel, requireContext())
            when (viewModel.signedIn) {
                true -> findNavController().navigate(R.id.action_games_to_gameBonus)
                false -> MngView.showDialog(
                    requireActivity(),
                    requireContext().getString(R.string.game_only_for_reg_users)
                )
            }
        }

        binding.game4.setOnClickListener {
            MngView.playClickSound(requireActivity(), viewModel, requireContext())
            when (viewModel.signedIn) {
                true -> findNavController().navigate(R.id.action_games_to_gameMiner1)
                false -> MngView.showDialog(
                    requireActivity(),
                    requireContext().getString(R.string.game_only_for_reg_users)
                )
            }
        }

        binding.game5.setOnClickListener {
            MngView.playClickSound(requireActivity(), viewModel, requireContext())
            findNavController().navigate(R.id.action_games_to_gameMiner2)
        }

        binding.game6ComingSoon.setOnClickListener {
            MngView.playClickSound(requireActivity(), viewModel, requireContext())
            MngView.showDialog(
                requireActivity(),
                requireContext().getString(R.string.coming_soon)
            )
        }

        binding.game7ComingSoon.setOnClickListener {
            MngView.playClickSound(requireActivity(), viewModel, requireContext())
            MngView.showDialog(
                requireActivity(),
                requireContext().getString(R.string.coming_soon)
            )
        }

        return binding.root
    }

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onResume() {
        super.onResume()
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        viewStateHandling()
        viewModel.setLastResult(0)
        viewModel.resetPositions()
        viewModel.resetAllSlots()
        viewModel.isAlreadyFinished = true
        viewModel.isNowFinishing = false
    }

    private fun viewStateHandling() {
        when {
            viewModel.signedIn -> {
                binding.apply {
                    buttonRegister.apply {
                        visibility = View.INVISIBLE
                        isEnabled = false
                    }
                    barLvl.visibility = View.VISIBLE
                    game3.setImageResource(R.drawable.game_3_unlocked)
                    game4.setImageResource(R.drawable.game_4_unlocked)
                }
            }

            else -> {
                binding.apply {
                    buttonRegister.apply {
                        visibility = View.VISIBLE
                        isEnabled = true
                    }
                    barLvl.visibility = View.INVISIBLE
                    game3.setImageResource(R.drawable.game_3_locked)
                    game4.setImageResource(R.drawable.game_4_locked)
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