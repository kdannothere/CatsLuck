package com.adrenaline.ofathlet.presentation.fragments

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Surface
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.WindowManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.adrenaline.ofathlet.BestActivity
import com.adrenaline.ofathlet.data.DataManager
import com.adrenaline.ofathlet.databinding.FragmentGameSlot2Binding
import com.adrenaline.ofathlet.presentation.GameViewModel
import com.adrenaline.ofathlet.presentation.slot.SlotAdapter
import com.adrenaline.ofathlet.presentation.utilities.MusicUtility
import com.adrenaline.ofathlet.presentation.utilities.ViewUtility
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class GameSlot2Fragment : Fragment() {

    private var _binding: FragmentGameSlot2Binding? = null
    private val binding get() = _binding!!
    private val viewModel: GameViewModel by activityViewModels()
    private val isGame2 = true
    private lateinit var vto: ViewTreeObserver
    private lateinit var leftSlotAdapter: SlotAdapter
    private lateinit var centerSlotAdapter: SlotAdapter
    private lateinit var rightSlotAdapter: SlotAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentGameSlot2Binding.inflate(inflater, container, false)

        vto = binding.slotField.viewTreeObserver
        ViewUtility.updateFieldHeight(binding.slotField, vto)

        viewModel.apply {
            if (leftSlots.isEmpty()) generateSlots()
            binding.totalValue.text = balance.value.toString()
            binding.betValue.text = bet.value.toString()
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            // fixing auto text feature for older Android APIs
            ViewUtility.apply {
                makeTextAutoSize(binding.totalTitle)
                makeTextAutoSize(binding.winTitle)
                makeTextAutoSize(binding.winValue)
                makeTextAutoSize(binding.totalValue)
                makeTextAutoSize(binding.betValue)
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch(Dispatchers.IO) {
            while (!ViewUtility.isHeightCorrect) {
                delay(100L)
            }
            launch(Dispatchers.Main) {
                setRecyclerViews()
                setClickListeners()
                viewModel.apply {
                    binding.leftRecyclerView.scrollToPosition(positions[0])
                    binding.centerRecyclerView.scrollToPosition(positions[1])
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
            if(newValue) {
                playWinSound()
                viewModel.setIsPlayingSoundWin(false)
            }
        }.launchIn(lifecycleScope)

        viewModel.isPlayingSoundLose.onEach { newValue ->
            if(newValue) {
                playLoseSound()
                viewModel.setIsPlayingSoundLose(false)
            }
        }.launchIn(lifecycleScope)
    }

    override fun onDestroy() {
        super.onDestroy()
        handleLastGameFinishing()
        _binding = null
        ViewUtility.isHeightCorrect = false
    }

    private fun setRecyclerViews() {

        leftSlotAdapter = SlotAdapter(viewModel.leftSlots, isGame2)
        centerSlotAdapter = SlotAdapter(viewModel.centerSlots, isGame2)
        rightSlotAdapter = SlotAdapter(viewModel.rightSlots, isGame2)
        binding.leftRecyclerView.apply {
            adapter = leftSlotAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setOnTouchListener { _, _ -> true }
        }
        binding.centerRecyclerView.apply {
            adapter = centerSlotAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setOnTouchListener { _, _ -> true }
        }
        binding.rightRecyclerView.apply {
            adapter = rightSlotAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setOnTouchListener { _, _ -> true }
        }
        setCorrectRecyclerViewHeight(
            listOf(
                binding.leftRecyclerView,
                binding.centerRecyclerView,
                binding.rightRecyclerView
            )
        )
    }

    private fun setCorrectRecyclerViewHeight(recyclers: List<RecyclerView?>) {
        val params = listOf(
            recyclers[0]?.layoutParams,
            recyclers[1]?.layoutParams,
            recyclers[2]?.layoutParams
        )
        val display = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            requireContext().display
        } else {
            @Suppress("DEPRECATION")
            (requireContext().getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
        }
        val rotation = display?.rotation
        val heightMultiplier =
            if (rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180) {
                // for the device in the portrait orientation
                2.5.also { ViewUtility.orientation = 0 }
            } else {
                // for the device in the landscape orientation
                2.8.also { ViewUtility.orientation = 1 }
            }
        repeat(3) { index ->
            params[index]?.height = (ViewUtility.getSlotHeight() * heightMultiplier).toInt()
            recyclers[index]?.layoutParams = params[index]
        }
    }

    private fun setClickListeners() {

        binding.apply {

            buttonRepeat.setOnClickListener {
                playClickSound()
                viewModel.spinSlots(
                    listOf(
                        leftRecyclerView,
                        centerRecyclerView,
                        rightRecyclerView
                    ),
                    requireContext()
                )
            }

            buttonIncreaseBet.setOnClickListener {
                playClickSound()
                viewModel.increaseBet(requireContext())
                lifecycleScope.launch(Dispatchers.IO) {
                    DataManager.saveBet(
                        requireContext(),
                        viewModel.bet.value
                    )
                }
            }

            buttonDecreaseBet.setOnClickListener {
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