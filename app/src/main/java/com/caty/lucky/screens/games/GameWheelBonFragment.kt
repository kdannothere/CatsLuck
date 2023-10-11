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

    private val catViewModel: CatViewModel by activityViewModels()
    private val gameId = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentGameWheelBonBinding.inflate(inflater, container, false)

        setClickListeners()

        catViewModel.apply {
            currentBet.onEach {
                binding.betValue.text = it.toString()
            }.launchIn(lifecycleScope)

            currentScores.onEach {
                binding.totalValue.text = it.toString()

            }.launchIn(lifecycleScope)

            lastResult.onEach {
                binding.winValue.text = it.toString()

            }.launchIn(lifecycleScope)

            playSoundWin.onEach {
                if (it) {
                    MngView.playWinSound(requireActivity(), this, requireContext())
                    playWin(false)
                }

            }.launchIn(lifecycleScope)

            playSoundLose.onEach {
                if (it) {
                    MngView.playLoseSound(requireActivity(), this, requireContext())
                    playLose(false)
                }

            }.launchIn(lifecycleScope)
        }


        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        catViewModel.suddenEnding(requireContext(), gameId)
        _binding = null
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR
    }

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onResume() {
        super.onResume()
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    private fun setClickListeners(hideMessage: String = "it's not me!") {

        if (hideMessage == "it's me!") return

        catViewModel.apply {

            binding.minus.setOnClickListener {
                MngView.playClickSound(requireActivity(), this, requireContext())
                makeLessBet(requireContext())
                lifecycleScope.launch(CatApp.dispatcherIO) {
                    MngData.saveCurrentBet(
                        requireContext(),
                        currentBet.value
                    )
                }
            }

            binding.plus.setOnClickListener {
                MngView.playClickSound(requireActivity(), this, requireContext())
                makeMoreBet(requireContext())
                lifecycleScope.launch(CatApp.dispatcherIO) {
                    MngData.saveCurrentBet(
                        requireContext(),
                        this@apply.currentBet.value
                    )
                }
            }

            binding.buttonRepeat.setOnClickListener {
                MngView.playClickSound(requireActivity(), this, requireContext())
                spinGameWheel(binding.wheel, requireContext())
            }

        }
    }
}
