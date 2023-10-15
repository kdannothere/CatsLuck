package com.caty.lucky.screens.games

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import android.os.Bundle
import android.view.LayoutInflater
import com.caty.lucky.managers.MngData
import com.caty.lucky.managers.MngView
import com.caty.lucky.adapter.SlotsAdapter
import android.view.View
import android.view.ViewGroup
import com.caty.lucky.CatApp
import com.caty.lucky.CatViewModel
import com.caty.lucky.databinding.FragmentGame1SlotsBinding
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class Game1SlotsFragment : Fragment() {

    private var _binding: FragmentGame1SlotsBinding? = null
    private val binding get() = _binding!!
    private val catViewModel: CatViewModel by activityViewModels()
    private val gameId = 1
    private lateinit var lAdapter: SlotsAdapter
    private lateinit var rAdapter: SlotsAdapter
    private lateinit var cAdapter: SlotsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentGame1SlotsBinding.inflate(inflater, container, false)

        catViewModel.apply {
            if (leftPosSlot.isEmpty()) genSlots(gameId = gameId)

            binding.scoresOfUser.text = currentScores.value.toString()

            binding.currentBet.text = currentBet.value.toString()

            lifecycleScope.launch(CatApp.dispatcherIO) {
                launch(CatApp.dispatcherMain) {

                    setRecyclers()

                    setClickListeners()

                    binding.leftColumn.scrollToPosition(positionsSlotGame[0])

                    binding.rightColumn.scrollToPosition(positionsSlotGame[2])

                    binding.centerColumn.scrollToPosition(positionsSlotGame[1])
                }
            }

            lastResult.onEach {

                binding.lastResultValue.text = it.toString()
            }.launchIn(lifecycleScope)

            currentScores.onEach {

                binding.scoresOfUser.text = it.toString()
            }.launchIn(lifecycleScope)

            currentBet.onEach {
                binding.currentBet.text = it.toString()

            }.launchIn(lifecycleScope)


            playSoundLose.onEach {
                if (it) {
                    MngView.playLoseSound(requireActivity(), this, requireContext())

                    playLose(value = false, index = 0)
                }
            }.launchIn(lifecycleScope)

            playSoundWin.onEach {
                if (it) {

                    MngView.playWinSound(requireActivity(), this, requireContext())

                    playWin(value = false, index = 0)
                }
            }.launchIn(lifecycleScope)
        }

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        catViewModel.suddenEnding(requireContext(), gameId)
        _binding = null
    }

    private fun setClickListeners(hideMessage: String = "it's not me!") {

        if (hideMessage == "it's me!") return

        catViewModel.apply {

            binding.minus.setOnClickListener {
                MngView.playClickSound(requireActivity(), this, requireContext())
                makeLessBet(requireContext())
                lifecycleScope.launch(CatApp.dispatcherIO) {
                    MngData.saveCurrentBet(
                        requireContext(),
                        currentBet.value
                    )
                }
            }

            binding.plus.setOnClickListener {
                MngView.playClickSound(requireActivity(), this, requireContext())
                makeMoreBet(requireContext())
                lifecycleScope.launch(CatApp.dispatcherIO) {
                    MngData.saveCurrentBet(
                        requireContext(),
                        this@apply.currentBet.value
                    )
                }
            }

            binding.buttonPlay.setOnClickListener {
                MngView.playClickSound(requireActivity(), this, requireContext())
                this.spinGameSlots(
                    listOf(
                        binding.leftColumn,
                        binding.centerColumn,
                        binding.rightColumn
                    ),
                    requireContext()
                )
            }

        }
    }

    override fun onResume() {
        super.onResume()
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR
    }

    private fun setAdapters() {
        lAdapter = SlotsAdapter(catViewModel.leftPosSlot, gameId)
        rAdapter = SlotsAdapter(catViewModel.rightPosSlot, gameId)
        cAdapter = SlotsAdapter(catViewModel.centerPosSlot, gameId)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setRecyclers(index: Int = 0, n: Int = 999) {


        if (index != 0) return
        setAdapters()
        binding.apply {
            leftColumn.layoutManager = LinearLayoutManager(requireContext())
            leftColumn.adapter = lAdapter
            leftColumn.setOnTouchListener { _, _ -> true }

            rightColumn.layoutManager = LinearLayoutManager(requireContext())
            rightColumn.adapter = rAdapter
            rightColumn.setOnTouchListener { _, _ -> true }

            if (n == 1000) MngView.showDialog(requireActivity(), "Hi there!")
            centerColumn.layoutManager = LinearLayoutManager(requireContext())
            centerColumn.adapter = cAdapter
            centerColumn.setOnTouchListener { _, _ -> true }
        }
    }
}