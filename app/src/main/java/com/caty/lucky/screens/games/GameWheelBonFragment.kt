package com.caty.lucky.screens.games

import kotlinx.coroutines.launch
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import com.caty.lucky.CatViewModel
import com.caty.lucky.databinding.FragmentGameWheelBonBinding
import com.caty.lucky.managers.MngData
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.caty.lucky.CatApp
import com.caty.lucky.managers.MngView
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import android.content.pm.ActivityInfo
import android.view.LayoutInflater

class GameWheelBonFragment : Fragment() {

    private var _binding: FragmentGameWheelBonBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CatViewModel by activityViewModels()
    private val gameId = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentGameWheelBonBinding.inflate(inflater, container, false)

        setClickListeners()

        viewModel.currentBet.onEach { newValue ->
            binding.betValue.text = newValue.toString()
        }.launchIn(lifecycleScope)

        viewModel.currentScores.onEach { newValue ->
            binding.totalValue.text = newValue.toString()
        }.launchIn(lifecycleScope)

        viewModel.lastResult.onEach { newValue ->
            binding.winValue.text = newValue.toString()
        }.launchIn(lifecycleScope)

        viewModel.playSoundWin.onEach { newValue ->
            if (newValue) {
                MngView.playWinSound(requireActivity(), viewModel, requireContext())
                viewModel.playWin(false)
            }
        }.launchIn(lifecycleScope)

        viewModel.playSoundLose.onEach { newValue ->
            if (newValue) {
                MngView.playLoseSound(requireActivity(), viewModel, requireContext())
                viewModel.playLose(false)
            }
        }.launchIn(lifecycleScope)



        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.lastGameFinishing(requireContext(), gameId)
        _binding = null
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR
    }

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onResume() {
        super.onResume()
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    private fun setClickListeners() {
        binding.apply {

            plus.setOnClickListener {
                MngView.playClickSound(requireActivity(), viewModel, requireContext())
                viewModel.increaseBet(requireContext())
                lifecycleScope.launch(CatApp.dispatcherIO) {
                    MngData.saveCurrentBet(
                        requireContext(),
                        viewModel.currentBet.value
                    )
                }
            }

            minus.setOnClickListener {
                MngView.playClickSound(requireActivity(), viewModel, requireContext())
                viewModel.decreaseBet(requireContext())
                lifecycleScope.launch(CatApp.dispatcherIO) {
                    MngData.saveCurrentBet(
                        requireContext(),
                        viewModel.currentBet.value
                    )
                }
            }
            buttonRepeat.setOnClickListener {
                MngView.playClickSound(requireActivity(), viewModel, requireContext())
                viewModel.spinGameWheel(binding.wheel, requireContext())
            }
        }
    }
}
