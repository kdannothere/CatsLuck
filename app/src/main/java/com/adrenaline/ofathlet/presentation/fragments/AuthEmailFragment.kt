package com.adrenaline.ofathlet.presentation.fragments

import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.adrenaline.ofathlet.BestActivity
import com.adrenaline.ofathlet.R
import com.adrenaline.ofathlet.databinding.FragmentAuthEmailBinding
import com.adrenaline.ofathlet.presentation.GameViewModel
import com.adrenaline.ofathlet.presentation.utilities.MusicUtility
import com.adrenaline.ofathlet.presentation.utilities.ViewUtility

class AuthEmailFragment : Fragment() {

    private var _binding: FragmentAuthEmailBinding? = null
    private val binding get() = _binding!!
    private val viewModel: GameViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentAuthEmailBinding.inflate(inflater, container, false)

        binding.buttonStart.setOnClickListener {
            playClickSound()
            signIn()
            if (viewModel.isUserLoggedIn) {
                findNavController().navigate(R.id.action_email_to_menu)
            }
        }

        binding.buttonAnonymousMode.setOnClickListener {
            playClickSound()
            viewModel.isUserAnonymous = true
            findNavController().navigate(R.id.action_email_to_menu)
        }

        binding.fieldEmail.setOnEditorActionListener { view, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                ViewUtility.hideSoftKeyboard(view, this)
                binding.buttonStart.callOnClick()
                true
            } else {
                false
            }
        }

        return binding.root
    }

    private fun signIn(email: String = binding.fieldEmail.text.toString()) {
        val isEmailAddress = Patterns.EMAIL_ADDRESS.matcher(email).matches()
        if (!isEmailAddress) {
            MusicUtility.doVibrate(
                requireContext(),
                viewModel.viewModelScope,
                viewModel.isVibrationOn
            )
            return
        }
        viewModel.signIn(requireContext(), email)
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