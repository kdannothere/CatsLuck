package com.caty.lucky.screens.games

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import kotlinx.coroutines.flow.onEach
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import com.caty.lucky.databinding.FragmentGame2SlotsBinding
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.caty.lucky.CatApp
import com.caty.lucky.CatViewModel
import com.caty.lucky.managers.MngData
import com.caty.lucky.managers.MngView
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch
import com.caty.lucky.slots.SlotsAdapter

class Game2SlotsFragment : Fragment() {

    private val viewModel: CatViewModel by activityViewModels()
    private var _binding: FragmentGame2SlotsBinding? = null
    private val binding get() = _binding!!
    private val gameId = 2
    private lateinit var leftAdapter: SlotsAdapter
    private lateinit var rightAdapter: SlotsAdapter
    private lateinit var centerAdapter: SlotsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentGame2SlotsBinding.inflate(inflater, container, false)

        viewModel.apply {
            if (leftPosSlot.isEmpty()) genSlots(gameId = gameId)
            binding.betValue.text = currentBet.value.toString()
            binding.totalValue.text = currentScores.value.toString()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch(CatApp.dispatcherIO) {
            launch(CatApp.dispatcherMain) {
                setRecyclers()
                setClickListeners()
                viewModel.apply {
                    binding.leftRecyclerView.scrollToPosition(positionsSlotGame[0])
                    binding.rightRecyclerView.scrollToPosition(positionsSlotGame[2])
                    binding.centerRecyclerView.scrollToPosition(positionsSlotGame[1])
                }
            }
        }
        viewModel.currentScores.onEach { newValue ->
            binding.totalValue.text = newValue.toString()
        }.launchIn(lifecycleScope)


        viewModel.currentBet.onEach { newValue ->
            binding.betValue.text = newValue.toString()
        }.launchIn(lifecycleScope)

        viewModel.lastResult.onEach { newValue ->
            binding.winValue.text = newValue.toString()
        }.launchIn(lifecycleScope)

        viewModel.playSoundLose.onEach { newValue ->
            if(newValue) {
                MngView.playLoseSound(requireActivity(), viewModel, requireContext())
                viewModel.playLose(false)
            }
        }.launchIn(lifecycleScope)

        viewModel.playSoundWin.onEach { newValue ->
            if(newValue) {
                MngView.playWinSound(requireActivity(), viewModel, requireContext())
                viewModel.playWin(false)
            }
        }.launchIn(lifecycleScope)

    }

    override fun onResume() {
        super.onResume()
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.lastGameFinishing(requireContext(), gameId)
        _binding = null
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setRecyclers() {

        leftAdapter = SlotsAdapter(viewModel.leftPosSlot, gameId)
        rightAdapter = SlotsAdapter(viewModel.rightPosSlot, gameId)
        centerAdapter = SlotsAdapter(viewModel.centerPosSlot, gameId)
        binding.leftRecyclerView.apply {
            adapter = leftAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setOnTouchListener { _, _ -> true }
        }
        binding.rightRecyclerView.apply {
            adapter = rightAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setOnTouchListener { _, _ -> true }
        }
        binding.centerRecyclerView.apply {
            adapter = centerAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setOnTouchListener { _, _ -> true }
        }
    }

    private fun setClickListeners() {

        binding.apply {


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

            buttonPlay.setOnClickListener {
                MngView.playClickSound(requireActivity(), viewModel, requireContext())
                viewModel.spinGameSlots(
                    listOf(
                        leftRecyclerView,
                        centerRecyclerView,
                        rightRecyclerView
                    ),
                    requireContext()
                )
            }

        }
    }
}