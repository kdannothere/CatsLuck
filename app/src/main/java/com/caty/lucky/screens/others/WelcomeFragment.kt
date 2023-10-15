package com.caty.lucky.screens.others

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import com.caty.lucky.managers.MngData
import com.caty.lucky.managers.MngData.scoreDefault
import com.caty.lucky.managers.MngData.theBetDefault
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.caty.lucky.CatApp
import com.caty.lucky.R
import com.caty.lucky.databinding.FragmentWelcomeBinding
import com.caty.lucky.managers.MngView
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import com.caty.lucky.CatViewModel

class WelcomeFragment : Fragment() {

    private var _binding: FragmentWelcomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CatViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentWelcomeBinding.inflate(inflater, container, false)

        lifecycleScope.launch(CatApp.dispatcherIO) {
            loadData()
            viewModel.setup(requireContext())
        }

        binding.buttonStart.setOnClickListener {
            viewModel.viewModelScope.launch(CatApp.dispatcherIO) {
                MngView.playClickSound(requireActivity(), viewModel, requireContext())
                launch(CatApp.dispatcherMain) {
                    findNavController().navigate(R.id.action_welcome_to_menu)
                }
            }
        }

        return binding.root
    }

    private fun loadData() {
        lifecycleScope.launch {
            launch(CatApp.dispatcherMain) {
                val score =
                    async(CatApp.dispatcherIO) {
                        MngData.loadScores(
                            requireContext(),
                            viewModel.currentScores.value
                        )
                    }

                val currentBet =
                    async(CatApp.dispatcherIO) {
                        MngData.loadLastBet(
                            requireContext(),
                            viewModel.currentBet.value
                        )
                    }

                if (score.await() < currentBet.await()) {
                    viewModel.setScore(scoreDefault, requireContext())
                    viewModel.setTheBet(theBetDefault, requireContext())
                } else {
                    viewModel.setScore(score.await(), requireContext())
                    viewModel.setTheBet(currentBet.await(), requireContext())
                }

                val isPrivacyAccepted = async { MngData.loadPrivacy(requireContext()) }
                val winNumber =
                    async { MngData.loadWinNumber(requireContext(), viewModel.winNumber.value) }

                val lvl = async { MngData.loadLvl(requireContext(), viewModel.lvl.value) }

                viewModel.privacy = isPrivacyAccepted.await()
                viewModel.setWinNumber(winNumber.await(), requireContext())
                viewModel.setLvl(lvl.await(), requireContext())

                val email = async { MngData.loadEmail(requireContext()) }
                if (email.await().isNotEmpty()) {
                    launch(CatApp.dispatcherMain) {
                        viewModel.signIn(requireContext(), email.await())
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

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onResume() {
        super.onResume()
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }
}