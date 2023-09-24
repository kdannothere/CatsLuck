package com.adrenaline.ofathlet.presentation.fragments

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
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
import com.adrenaline.ofathlet.data.Constants
import com.adrenaline.ofathlet.data.DataManager
import com.adrenaline.ofathlet.databinding.FragmentWelcomeBinding
import com.adrenaline.ofathlet.presentation.GameViewModel
import com.adrenaline.ofathlet.presentation.utilities.MusicUtility
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class WelcomeFragment : Fragment() {

    private var _binding: FragmentWelcomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: GameViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentWelcomeBinding.inflate(inflater, container, false)

        binding.buttonStart.setOnClickListener {
            viewModel.viewModelScope.launch(Dispatchers.IO) {
                playClickSound()
                viewModel.loadSettings(requireContext())
                loadData()
                val login =
                    async(Dispatchers.IO) {
                        DataManager.loadLogin(requireContext())
                    }
                if (login.await().isNotEmpty()) {
                    launch(Dispatchers.Main) {
                        viewModel.signIn(requireContext(), login.await())
                        findNavController().navigate(R.id.action_welcome_to_menu)
                    }
                } else {
                    launch(Dispatchers.Main) {
                        findNavController().navigate(R.id.action_welcome_to_menu)
                    }
                }
            }
        }

        return binding.root
    }

    private fun loadData() {
        lifecycleScope.launch {
            launch(Dispatchers.Main) {
                val balance =
                    async(Dispatchers.IO) {
                        DataManager.loadBalance(
                            requireContext(),
                            viewModel.balance.value
                        )
                    }

                val bet =
                    async(Dispatchers.IO) {
                        DataManager.loadBet(
                            requireContext(),
                            viewModel.bet.value
                        )
                    }

                if (balance.await() < bet.await()) {
                    viewModel.setBalance(Constants.balanceDefault, requireContext())
                    viewModel.setBet(Constants.betDefault, requireContext())
                } else {
                    viewModel.setBalance(balance.await(), requireContext())
                    viewModel.setBet(bet.await(), requireContext())
                }

                val isPrivacyAccepted = async { DataManager.loadPrivacy(requireContext()) }
                val winNumber =
                    async { DataManager.loadWinNumber(requireContext(), viewModel.winNumber.value) }

                val lvl = async { DataManager.loadLvl(requireContext(), viewModel.lvl.value) }
                viewModel.privacy = isPrivacyAccepted.await()
                viewModel.setWinNumber(winNumber.await(), requireContext())
                viewModel.setLvl(lvl.await(), requireContext())
            }
        }
    }

    private fun playClickSound() {
        viewModel.viewModelScope.launch {
            val isSoundOn = async { DataManager.loadSoundSetting(requireContext()) }
            MusicUtility.playSound(
                mediaPlayer = (activity as BestActivity).soundPlayer,
                MusicUtility.soundClickResId,
                requireContext(),
                this,
                isSoundOn.await(),
                viewModel.isVibrationOn
            )
        }
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