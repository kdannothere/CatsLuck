package com.caty.lucky.managers

import com.caty.lucky.CatViewModel
import android.app.Activity
import androidx.fragment.app.Fragment
import androidx.lifecycle.viewModelScope
import android.content.Context
import android.util.DisplayMetrics
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.caty.lucky.CatApp
import com.caty.lucky.GameActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

object MngView {

    fun hideSoftKeyboard(view: View, fragment: Fragment) {
        val inputMethodManager =
            fragment.activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun showDialog(activity: Activity, message: String) {
        val gameActivity = activity as GameActivity
        gameActivity.showDialog(message)
    }

    fun playClickSound(activity: Activity, viewModel: CatViewModel, context: Context) {
        MngTheMusic.playSound(
            mediaPlayer = (activity as GameActivity).soundPlayer,
            MngTheMusic.soundClickRes,
            context,
            viewModel.viewModelScope,
            viewModel.isMusicSetOn,
            viewModel.isVibroSetOn
        )
    }

    fun playWinSound(activity: Activity, viewModel: CatViewModel, context: Context) {
        MngTheMusic.playSound(
            mediaPlayer = (activity as GameActivity).soundPlayer,
            MngTheMusic.soundWinRes,
            context,
            viewModel.viewModelScope,
            viewModel.isMusicSetOn,
            viewModel.isVibroSetOn
        )
    }

    fun playLoseSound(activity: Activity, viewModel: CatViewModel, context: Context) {
        MngTheMusic.playSound(
            mediaPlayer = (activity as GameActivity).soundPlayer,
            MngTheMusic.soundLoseRes,
            context,
            viewModel.viewModelScope,
            viewModel.isMusicSetOn,
            viewModel.isVibroSetOn
        )
    }

    fun goTo(
        recycler: RecyclerView?,
        index: Int,
        context: Context,
        scope:CoroutineScope,
        positionsSlotGame: MutableList<Int>,
        latestIndex: Int,
        listener: () -> Unit
    ) {
        scope.launch(CatApp.dispatcherMain) {
            val smoothScroller = object : LinearSmoothScroller(context) {
                override fun getVerticalSnapPreference(): Int {
                    return SNAP_TO_START
                }

                override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics?): Float {
                    return 0.299f
                }
            }
            smoothScroller.targetPosition = positionsSlotGame[index]
            recycler?.layoutManager?.startSmoothScroll(smoothScroller)
            if (recycler == null) return@launch
            if (index == latestIndex) listener.invoke()
        }
    }

}