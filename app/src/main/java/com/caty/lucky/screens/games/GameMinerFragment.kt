package com.caty.lucky.screens.games

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.caty.lucky.CatApp
import com.caty.lucky.CatViewModel
import com.caty.lucky.databinding.FragmentGameMinerBinding
import com.caty.lucky.managers.MngData
import com.caty.lucky.managers.MngImage
import com.caty.lucky.managers.MngView
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class GameMinerFragment : Fragment() {

    private var _binding: FragmentGameMinerBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CatViewModel by activityViewModels()
    private val gameId = 3

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onResume() {
        super.onResume()
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentGameMinerBinding.inflate(inflater, container, false)

        setClickListeners()

        viewModel.apply {
            binding.scoresOfUser.text = currentScores.value.toString()
            binding.currentBet.text = currentBet.value.toString()

            minerStateChanged.onEach {
                updateGameField()
                if (isNowFinishing) {
                    finishMiner(gameId, requireContext())
                }
            }.launchIn(lifecycleScope)

            setRandomImages()

            currentScores.onEach {
                binding.scoresOfUser.text = it.toString()
            }.launchIn(lifecycleScope)

            lastResult.onEach {
                binding.lastResultValue.text = it.toString()
            }.launchIn(lifecycleScope)

            currentBet.onEach {
                binding.currentBet.text = it.toString()
            }.launchIn(lifecycleScope)

            playSoundWin.onEach {
                if (it) {
                    MngView.playWinSound(requireActivity(), this, requireContext())
                    playWin(false)
                }
            }.launchIn(lifecycleScope)

            playSoundLose.onEach {
                if (it) {
                    MngView.playLoseSound(requireActivity(), this, requireContext())
                    playLose(false)
                }
            }.launchIn(lifecycleScope)
        }

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.apply {
            setScore(currentScores.value + lastBet, requireContext())
        }
        _binding = null
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR
    }

    private fun setClickListeners() {

        binding.apply {

            buttonPlay.setOnClickListener {
                MngView.playClickSound(requireActivity(), viewModel, requireContext())
                viewModel.playMiner(gameId, requireContext())
            }

            plus.setOnClickListener {
                MngView.playClickSound(requireActivity(), viewModel, requireContext())
                viewModel.makeMoreBet(requireContext())
                lifecycleScope.launch(CatApp.dispatcherIO) {
                    MngData.saveCurrentBet(
                        requireContext(),
                        viewModel.currentBet.value
                    )
                }
            }

            minus.setOnClickListener {
                MngView.playClickSound(requireActivity(), viewModel, requireContext())
                viewModel.makeLessBet(requireContext())
                lifecycleScope.launch(CatApp.dispatcherIO) {
                    MngData.saveCurrentBet(
                        requireContext(),
                        viewModel.currentBet.value
                    )
                }
            }

            slotLeftTop.setOnClickListener {
                MngView.playClickSound(requireActivity(), viewModel, requireContext())
                if (viewModel.positionsMinerLeft[0] != 0) return@setOnClickListener
                viewModel.discoverCard(gameId, 0, this.slotLeftTop)
            }

            slotLeftCenter.setOnClickListener {
                MngView.playClickSound(requireActivity(), viewModel, requireContext())
                if (viewModel.positionsMinerLeft[1] != 0) return@setOnClickListener
                viewModel.discoverCard(gameId, 1, this.slotLeftCenter)
            }

            slotLeftBottom.setOnClickListener {
                MngView.playClickSound(requireActivity(), viewModel, requireContext())
                if (viewModel.positionsMinerLeft[2] != 0) return@setOnClickListener
                viewModel.discoverCard(gameId, 2, this.slotLeftBottom)
            }

            slotCenterTop.setOnClickListener {
                MngView.playClickSound(requireActivity(), viewModel, requireContext())
                if (viewModel.positionsMinerCenter[0] != 0) return@setOnClickListener
                viewModel.discoverCard(gameId, 3, this.slotCenterTop)
            }

            slotCenterCenter.setOnClickListener {
                MngView.playClickSound(requireActivity(), viewModel, requireContext())
                if (viewModel.positionsMinerCenter[1] != 0) return@setOnClickListener
                viewModel.discoverCard(gameId, 4, this.slotCenterCenter)
            }

            slotCenterBottom.setOnClickListener {
                MngView.playClickSound(requireActivity(), viewModel, requireContext())
                if (viewModel.positionsMinerCenter[2] != 0) return@setOnClickListener
                viewModel.discoverCard(gameId, 5, this.slotCenterBottom)
            }

            slotRightTop.setOnClickListener {
                MngView.playClickSound(requireActivity(), viewModel, requireContext())
                if (viewModel.positionsMinerRight[0] != 0) return@setOnClickListener
                viewModel.discoverCard(gameId, 6, this.slotRightTop)
            }

            slotRightCenter.setOnClickListener {
                MngView.playClickSound(requireActivity(), viewModel, requireContext())
                if (viewModel.positionsMinerRight[1] != 0) return@setOnClickListener
                viewModel.discoverCard(gameId, 7, this.slotRightCenter)
            }

            slotRightBottom.setOnClickListener {
                MngView.playClickSound(requireActivity(), viewModel, requireContext())
                if (viewModel.positionsMinerRight[2] != 0) return@setOnClickListener
                viewModel.discoverCard(gameId, 8, this.slotRightBottom)
            }
        }
    }

    private fun updateGameField() {
        binding.apply {
            slotLeftTop.setImageResource(
                MngImage.getDrawableById(
                    viewModel.positionsMinerLeft[0],
                    gameId
                )
            )
            slotLeftCenter.setImageResource(
                MngImage.getDrawableById(
                    viewModel.positionsMinerLeft[1],
                    gameId
                )
            )
            slotLeftBottom.setImageResource(
                MngImage.getDrawableById(
                    viewModel.positionsMinerLeft[2],
                    gameId
                )
            )
            slotCenterTop.setImageResource(
                MngImage.getDrawableById(
                    viewModel.positionsMinerCenter[0],
                    gameId
                )
            )
            slotCenterCenter.setImageResource(
                MngImage.getDrawableById(
                    viewModel.positionsMinerCenter[1],
                    gameId
                )
            )
            slotCenterBottom.setImageResource(
                MngImage.getDrawableById(
                    viewModel.positionsMinerCenter[2],
                    gameId
                )
            )
            slotRightTop.setImageResource(
                MngImage.getDrawableById(
                    viewModel.positionsMinerRight[0],
                    gameId
                )
            )
            slotRightCenter.setImageResource(
                MngImage.getDrawableById(
                    viewModel.positionsMinerRight[1],
                    gameId
                )
            )
            slotRightBottom.setImageResource(
                MngImage.getDrawableById(
                    viewModel.positionsMinerRight[2],
                    gameId
                )
            )
        }
    }

    private fun setRandomImages() {
        binding.apply {
            slotLeftTop.setImageResource(
                MngImage.getDrawableById(
                    MngImage.getRandomImageId(gameId),
                    gameId
                )
            )
            slotLeftCenter.setImageResource(
                MngImage.getDrawableById(
                    MngImage.getRandomImageId(gameId),
                    gameId
                )
            )
            slotLeftBottom.setImageResource(
                MngImage.getDrawableById(
                    MngImage.getRandomImageId(gameId),
                    gameId
                )
            )
            slotCenterTop.setImageResource(
                MngImage.getDrawableById(
                    MngImage.getRandomImageId(gameId),
                    gameId
                )
            )
            slotCenterCenter.setImageResource(
                MngImage.getDrawableById(
                    MngImage.getRandomImageId(gameId),
                    gameId
                )
            )
            slotCenterBottom.setImageResource(
                MngImage.getDrawableById(
                    MngImage.getRandomImageId(gameId),
                    gameId
                )
            )
            slotRightTop.setImageResource(
                MngImage.getDrawableById(
                    MngImage.getRandomImageId(gameId),
                    gameId
                )
            )
            slotRightCenter.setImageResource(
                MngImage.getDrawableById(
                    MngImage.getRandomImageId(gameId),
                    gameId
                )
            )
            slotRightBottom.setImageResource(
                MngImage.getDrawableById(
                    MngImage.getRandomImageId(gameId),
                    gameId
                )
            )
        }
    }
}