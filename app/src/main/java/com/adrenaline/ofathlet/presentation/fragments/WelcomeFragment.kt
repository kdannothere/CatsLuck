package com.adrenaline.ofathlet.presentation.fragments

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
import com.adrenaline.ofathlet.databinding.FragmentWelcomeBinding
import com.adrenaline.ofathlet.presentation.GameViewModel
import com.adrenaline.ofathlet.presentation.utilities.MusicUtility
import com.adrenaline.ofathlet.presentation.utilities.ViewUtility
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

        binding.buttonPlay.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                playClickSound()
                viewModel.loadSettings(requireContext())
                val login =
                    async(Dispatchers.IO) {
                        DataManager.loadLogin(requireContext())
                    }
                if (login.await().isNotEmpty()) {
                    launch(Dispatchers.Main) {
                        viewModel.isUserAnonymous = false
                        loadData()
                        findNavController().navigate(R.id.action_WelcomeFragment_to_MenuFragment)
                    }
                } else {
                    launch(Dispatchers.Main) {
                        findNavController().navigate(R.id.action_WelcomeFragment_to_AuthFragment)
                    }
                }
            }
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            // fixing auto text feature for older Android APIs
            ViewUtility.apply {
                makeTextAutoSize(binding.textSignUp)
                makeTextAutoSize(binding.titlePlay)
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
                viewModel.setBalance(balance.await(), requireContext())

                val bet =
                    async(Dispatchers.IO) {
                        DataManager.loadBet(
                            requireContext(),
                            viewModel.bet.value
                        )
                    }
                viewModel.setBet(bet.await(), requireContext())
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
}