package com.adrenaline.ofathlet.presentation.fragments

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.adrenaline.ofathlet.BestActivity
import com.adrenaline.ofathlet.data.DataManager
import com.adrenaline.ofathlet.databinding.FragmentGameMiner2Binding
import com.adrenaline.ofathlet.presentation.GameViewModel
import com.adrenaline.ofathlet.presentation.utilities.ImageUtility
import com.adrenaline.ofathlet.presentation.utilities.MusicUtility
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class GameMiner2Fragment : Fragment() {

    private var _binding: FragmentGameMiner2Binding? = null
    private val binding get() = _binding!!
    private val viewModel: GameViewModel by activityViewModels()
    private val gameId = 4

    override fun onResume() {
        super.onResume()
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentGameMiner2Binding.inflate(inflater, container, false)
        setClickListeners()

        viewModel.apply {
            binding.totalValue.text = balance.value.toString()
            binding.betValue.text = bet.value.toString()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.minerStateChanged.onEach {
            updateGameField()
            if (viewModel.isFinishing) {
                viewModel.finishMiner(gameId, requireContext())
            }
        }.launchIn(lifecycleScope)

        setRandomImages()

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
        viewModel.apply {
            setBalance(balance.value + lastBet, requireContext())
        }
        _binding = null
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR
    }

    private fun setClickListeners() {

        binding.apply {

            buttonPlay.setOnClickListener {
                playClickSound()
                viewModel.playMiner(gameId, requireContext())
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

            slotLeftTop.setOnClickListener {
                playClickSound()
                if (viewModel.positionsMinerLeft[0] != 0) return@setOnClickListener
                viewModel.discover(gameId, 0, this.slotLeftTop)
            }

            slotLeftBottom.setOnClickListener {
                playClickSound()
                if (viewModel.positionsMinerLeft[2] != 0) return@setOnClickListener
                viewModel.discover(gameId, 1, this.slotLeftBottom)
            }

            slotRightTop.setOnClickListener {
                playClickSound()
                if (viewModel.positionsMinerRight[0] != 0) return@setOnClickListener
                viewModel.discover(gameId, 2, this.slotRightTop)
            }

            slotRightBottom.setOnClickListener {
                playClickSound()
                if (viewModel.positionsMinerRight[2] != 0) return@setOnClickListener
                viewModel.discover(gameId, 3, this.slotRightBottom)
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

    private fun updateGameField() {
        binding.apply {
            slotLeftTop.setImageResource(
                ImageUtility.getDrawableById(
                    viewModel.positionsMinerLeft[0],
                    gameId
                )
            )
            slotLeftBottom.setImageResource(
                ImageUtility.getDrawableById(
                    viewModel.positionsMinerLeft[2],
                    gameId
                )
            )
            slotRightTop.setImageResource(
                ImageUtility.getDrawableById(
                    viewModel.positionsMinerRight[0],
                    gameId
                )
            )
            slotRightBottom.setImageResource(
                ImageUtility.getDrawableById(
                    viewModel.positionsMinerRight[2],
                    gameId
                )
            )
        }
    }

    private fun setRandomImages() {
        binding.apply {
            slotLeftTop.setImageResource(
                ImageUtility.getDrawableById(
                    ImageUtility.getRandomImageId(gameId),
                    gameId
                )
            )
            slotLeftBottom.setImageResource(
                ImageUtility.getDrawableById(
                    ImageUtility.getRandomImageId(gameId),
                    gameId
                )
            )
            slotRightTop.setImageResource(
                ImageUtility.getDrawableById(
                    ImageUtility.getRandomImageId(gameId),
                    gameId
                )
            )
            slotRightBottom.setImageResource(
                ImageUtility.getDrawableById(
                    ImageUtility.getRandomImageId(gameId),
                    gameId
                )
            )
        }
    }
}