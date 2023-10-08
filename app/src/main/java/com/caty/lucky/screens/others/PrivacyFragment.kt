package com.caty.lucky.screens.others

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import android.content.pm.ActivityInfo
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.caty.lucky.CatViewModel
import com.caty.lucky.managers.MngData
import com.caty.lucky.managers.MngView
import androidx.navigation.fragment.findNavController
import com.caty.lucky.R
import kotlinx.coroutines.launch
import com.caty.lucky.databinding.FragmentPrivacyBinding
import kotlinx.coroutines.Dispatchers

class PrivacyFragment : Fragment() {

    companion object {
        const val privacyUrl = "https://www.google.com/"
    }

    private var _binding: FragmentPrivacyBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CatViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentPrivacyBinding.inflate(inflater, container, false)

        binding.buttonPrivacy.setOnClickListener {
            MngView.playClickSound(requireActivity(), viewModel, requireContext())
            findNavController().navigate(R.id.action_privacy_to_web)
        }

        binding.buttonYes.setOnClickListener {
            MngView.playClickSound(requireActivity(), viewModel, requireContext())
            viewModel.privacy = true
            lifecycleScope.launch(Dispatchers.IO) {
                MngData.savePrivacy(requireContext(), true)
            }
            findNavController().navigate(R.id.action_privacy_to_games)
        }

        return binding.root
    }

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onResume() {
        super.onResume()
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        viewModel.setLastResult(0)
        viewModel.resetAllSlots()
        viewModel.resetPositions()
    }

    override fun onDestroy() {
        super.onDestroy()
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR
        _binding = null
    }
}