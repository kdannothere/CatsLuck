package com.caty.lucky.screens.others

import android.annotation.SuppressLint
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import com.caty.lucky.R
import com.caty.lucky.databinding.FragmentSignInBinding
import com.caty.lucky.CatViewModel
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.caty.lucky.managers.MngTheMusic
import com.caty.lucky.managers.MngView

class SignInFragment : Fragment() {

    private val viewModel: CatViewModel by activityViewModels()

    private var _binding: FragmentSignInBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSignInBinding.inflate(inflater, container, false)

        binding.buttonStart.setOnClickListener {
            MngView.playClickSound(requireActivity(), viewModel, requireContext())
            signIn()
            if (viewModel.signedIn) {
                findNavController().navigate(R.id.action_email_to_games)
            }
        }

        binding.buttonAnonymousMode.setOnClickListener {
            MngView.playClickSound(requireActivity(), viewModel, requireContext())
            viewModel.isUserAnonymous = true
            MngView.showDialog(
                requireActivity(),
                requireContext().getString(R.string.anonymous_explanation)
            )
            findNavController().navigate(R.id.action_email_to_games)
        }

        binding.fieldEmail.setOnEditorActionListener { view, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                MngView.hideSoftKeyboard(view, this)
                binding.buttonStart.callOnClick()
                true
            } else {
                false
            }
        }

        return binding.root
    }

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onResume() {
        super.onResume()
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    private fun signIn(email: String = binding.fieldEmail.text.toString()) {
        val isEmailAddress = Patterns.EMAIL_ADDRESS.matcher(email).matches()
        if (!isEmailAddress) {
            MngTheMusic.doVibrate(
                requireContext(),
                viewModel.viewModelScope,
                viewModel.isVibroSetOn
            )
            MngView.showDialog(requireActivity(), getString(R.string.not_correct_email))
            return
        }
        viewModel.signIn(requireContext(), email)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR
    }
}