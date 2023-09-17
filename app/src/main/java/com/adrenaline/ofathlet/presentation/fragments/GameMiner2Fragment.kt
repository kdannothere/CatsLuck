package com.adrenaline.ofathlet.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.adrenaline.ofathlet.BestActivity
import com.adrenaline.ofathlet.data.DataManager
import com.adrenaline.ofathlet.databinding.FragmentGameMiner2Binding
import com.adrenaline.ofathlet.presentation.GameViewModel
import com.adrenaline.ofathlet.presentation.slot.SlotAdapter
import com.adrenaline.ofathlet.presentation.utilities.MusicUtility
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class GameMiner2Fragment : Fragment() {

    private var _binding: FragmentGameMiner2Binding? = null
    private val binding get() = _binding!!
    private val viewModel: GameViewModel by activityViewModels()
    private val gameId = 4
    private lateinit var leftSlotAdapter: SlotAdapter
    private lateinit var rightSlotAdapter: SlotAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentGameMiner2Binding.inflate(inflater, container, false)

        viewModel.apply {
            if (leftSlots.isEmpty()) generateSlots(gameId = gameId)
            binding.totalValue.text = balance.value.toString()
            binding.betValue.text = bet.value.toString()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch(Dispatchers.IO) {
            launch(Dispatchers.Main) {
                setRecyclerViews()
                setClickListeners()
                viewModel.apply {
                    binding.leftRecyclerView.scrollToPosition(positions[0])
                    binding.rightRecyclerView.scrollToPosition(positions[2])
                }
            }
        }
        viewModel.balance.onEach { newValue ->
            binding.totalValue.text = newValue.toString()
        }.launchIn(lifecycleScope)

        viewModel.win.onEach { newValue ->
            binding.winValue.text = newValue.toString()
        }.launchIn(lifecycleScope)

        viewModel.bet.onEach { newValue ->
            binding.betValue.text = newValue.toString()
        }.launchIn(lifecycleScope)

        viewModel.isPlayingSoundWin.onEach { newValue ->
            if (newValue) {
                playWinSound()
                viewModel.setIsPlayingSoundWin(false)
            }
        }.launchIn(lifecycleScope)

        viewModel.isPlayingSoundLose.onEach { newValue ->
            if (newValue) {
                playLoseSound()
                viewModel.setIsPlayingSoundLose(false)
            }
        }.launchIn(lifecycleScope)
    }

    override fun onDestroy() {
        super.onDestroy()
        handleLastGameFinishing()
        _binding = null
    }

    private fun setRecyclerViews() {
        leftSlotAdapter = SlotAdapter(viewModel.leftSlots, gameId)
        rightSlotAdapter = SlotAdapter(viewModel.rightSlots, gameId)
        binding.leftRecyclerView.apply {
            adapter = leftSlotAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setOnTouchListener { _, _ -> true }
        }
        binding.rightRecyclerView.apply {
            adapter = rightSlotAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setOnTouchListener { _, _ -> true }
        }
    }

    private fun setClickListeners() {

        binding.apply {

            buttonPlay.setOnClickListener {
                playClickSound()
                viewModel.spinSlots(
                    listOf(
                        leftRecyclerView,
                        rightRecyclerView
                    ),
                    requireContext()
                )
            }

            plus.setOnClickListener {
                playClickSound()
                viewModel.increaseBet(requireContext())
                lifecycleScope.launch(Dispatchers.IO) {
                    DataManager.saveBet(
                        requireContext(),
                        viewModel.bet.value
                    )
                }
            }

            minus.setOnClickListener {
                playClickSound()
                viewModel.decreaseBet(requireContext())
                lifecycleScope.launch(Dispatchers.IO) {
                    DataManager.saveBet(
                        requireContext(),
                        viewModel.bet.value
                    )
                }
            }
        }
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

    private fun playWinSound() {
        MusicUtility.playSound(
            mediaPlayer = (activity as BestActivity).soundPlayer,
            MusicUtility.soundWinResId,
            requireContext(),
            viewModel.viewModelScope,
            viewModel.isSoundOn,
            viewModel.isVibrationOn
        )
    }

    private fun playLoseSound() {
        MusicUtility.playSound(
            mediaPlayer = (activity as BestActivity).soundPlayer,
            MusicUtility.soundLoseResId,
            requireContext(),
            viewModel.viewModelScope,
            viewModel.isSoundOn,
            viewModel.isVibrationOn
        )
    }

    private fun handleLastGameFinishing() {
        viewModel.apply {
            if (isSpinningSlots) {
                setBalance(balance.value + lastBet, requireContext())
            } else return@apply
            isFinished = true
            isSpinningSlots = false
            lastBet = 0
        }
    }
}