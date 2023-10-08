package com.caty.lucky.screens.others

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.content.pm.ActivityInfo
import android.os.Bundle
import com.caty.lucky.databinding.FragmentMenuBinding
import com.caty.lucky.CatViewModel
import android.view.View
import androidx.fragment.app.Fragment
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.caty.lucky.CatApp
import com.caty.lucky.managers.MngView
import com.caty.lucky.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.system.exitProcess

class MenuFragment : Fragment() {

    private var _binding: FragmentMenuBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CatViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentMenuBinding.inflate(inflater, container, false)

        binding.games.setOnClickListener {
            MngView.playClickSound(requireActivity(), viewModel, requireContext())
            when (viewModel.privacy) {
                true -> findNavController().navigate(R.id.action_menu_to_games)
                false -> findNavController().navigate(R.id.action_menu_to_privacy)
            }
        }

        binding.settings.setOnClickListener {
            MngView.playClickSound(requireActivity(), viewModel, requireContext())
            findNavController().navigate(R.id.action_menu_to_settings)
        }

        binding.privacy.setOnClickListener {
            MngView.playClickSound(requireActivity(), viewModel, requireContext())
            findNavController().navigate(R.id.action_menu_to_web)
        }

        binding.exit.setOnClickListener {
            MngView.showDialog(requireActivity(), "Bye!")
            lifecycleScope.launch(CatApp.dispatcherIO) {
                delay(1000L)
                launch(CatApp.dispatcherMain) {
                    exitProcess(0)
                }
            }
        }

        return binding.root
    }

    @SuppressLint("SourceLockedOrientationActivity")
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