package com.adrenaline.ofathlet.presentation

import android.content.Context
import android.util.DisplayMetrics
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.ImageView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.adrenaline.ofathlet.data.Constants
import com.adrenaline.ofathlet.data.DataManager
import com.adrenaline.ofathlet.presentation.slot.Slot
import com.adrenaline.ofathlet.presentation.utilities.ImageUtility
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date
import kotlin.math.absoluteValue
import kotlin.random.Random

class GameViewModel(
    private val dispatcherIO: CoroutineDispatcher = Dispatchers.IO,
    private val dispatcherMain: CoroutineDispatcher = Dispatchers.Main,
) : ViewModel() {

    val leftSlots = mutableListOf<Slot>()
    val centerSlots = mutableListOf<Slot>()
    val rightSlots = mutableListOf<Slot>()

    private var login = ""
    var privacy = false
    var isMusicOn = true
    var isSoundOn = true
    var isVibrationOn = true
    val isUserLoggedIn get() = login.isNotEmpty()
    var isUserAnonymous = true

    private val _balance = MutableStateFlow(Constants.balanceDefault)
    val balance = _balance.asStateFlow()

    private val _win = MutableStateFlow(0)
    val win = _win.asStateFlow()

    private val _bet = MutableStateFlow(Constants.betDefault)
    val bet = _bet.asStateFlow()

    private val _lvl = MutableStateFlow(1)
    val lvl = _lvl.asStateFlow()

    private val _winNumber = MutableStateFlow(0)
    val winNumber = _winNumber.asStateFlow()

    private val _isPlayingSoundWin = MutableStateFlow(false)
    val isPlayingSoundWin = _isPlayingSoundWin.asStateFlow()

    private val _isPlayingSoundLose = MutableStateFlow(false)
    val isPlayingSoundLose = _isPlayingSoundLose.asStateFlow()

    var isSpinningSlots = false
    private var latestIndex = 0
    private val random = Random(Date().time)
    val positions = mutableListOf(0, 0, 0)


    // wheel variables
    private var spinningDuration = 4000L
    private val sectorsPrizes =
        doubleArrayOf(0.0, 1.2, 1.0, 0.0, 1.2, 1.0, 1.2, 2.0, 1.0, 1.2)
    private val sectorDegrees = mutableListOf<Int>()

    // current position of wheel
    private var sectorIndex = 9
    var isSpinningWheel = false

    // last game data
    var lastBet = 0
    var isFinished = true
    var isFinishing = false


    // Miner

    private val winField = mutableListOf<Int>()
    val positionsMinerLeft = mutableListOf(0, 0, 0)
    val positionsMinerCenter = mutableListOf(0, 0, 0)
    val positionsMinerRight = mutableListOf(0, 0, 0)

    private val _minerStateChanged = MutableStateFlow(false)
    val minerStateChanged = _minerStateChanged.asStateFlow()

    init {
        getDegreeForSectors()
    }

    fun playMiner(gameId: Int, context: Context) {
        if (!isFinished || isFinishing) return
        isFinished = false
        lastBet = bet.value
        setBalance(balance.value - bet.value, context)
        generateWinField(gameId)
        resetPositions()
        setMinerStateChanged(!minerStateChanged.value)
    }

    fun discover(gameId: Int, index: Int, view: ImageView) {
        if (isFinished) return
        when {
            gameId == 3 && index == 0 -> positionsMinerLeft[0] = winField[index]
            gameId == 3 && index == 1 -> positionsMinerLeft[1] = winField[index]
            gameId == 3 && index == 2 -> positionsMinerLeft[2] = winField[index]
            gameId == 3 && index == 3 -> positionsMinerCenter[0] = winField[index]
            gameId == 3 && index == 4 -> positionsMinerCenter[1] = winField[index]
            gameId == 3 && index == 5 -> positionsMinerCenter[2] = winField[index]
            gameId == 3 && index == 6 -> positionsMinerRight[0] = winField[index]
            gameId == 3 && index == 7 -> positionsMinerRight[1] = winField[index]
            gameId == 3 && index == 8 -> positionsMinerRight[2] = winField[index]
            gameId == 4 && index == 0 -> positionsMinerLeft[0] = winField[index]
            gameId == 4 && index == 1 -> positionsMinerLeft[2] = winField[index]
            gameId == 4 && index == 2 -> positionsMinerRight[0] = winField[index]
            gameId == 4 && index == 3 -> positionsMinerRight[2] = winField[index]
        }
        view.setImageResource(ImageUtility.getDrawableById(winField[index], gameId))
        if (noMinesMore(gameId)) isFinishing = true
        setMinerStateChanged(!minerStateChanged.value)
    }

    private fun noMinesMore(gameId: Int): Boolean {
        return if (gameId == 3) {
            !positionsMinerLeft.contains(0)
                    && !positionsMinerCenter.contains(0)
                    && !positionsMinerRight.contains(0)
        } else {
            !(positionsMinerLeft[0] == 0 || positionsMinerLeft[2] == 0)
                    && !(positionsMinerRight[0] == 0 || positionsMinerRight[2] == 0)
        }
    }

    fun finishMiner(gameId: Int, context: Context) {
        val maxValue = if (gameId == 3) 4 else 3
        var prize = 0.0
        winField.forEach {
            prize += bet.value * getMultiplier(maxValue, it)
        }
        if (prize > 0) {
            setIsPlayingSoundWin(true)
            setBalance(balance.value + prize.toInt(), context)
            setWin(prize.toInt())
            setWinNumber(winNumber.value + 1, context)
            setLvl(lvl.value, context)
        } else setIsPlayingSoundLose(true)
        lastBet = 0
        isFinished = true
        isFinishing = false
    }

    private fun getMultiplier(maxValue: Int, index: Int): Double {
        return when (index) {
            1 -> 0.00
            2 -> if (maxValue == 3) 0.15 else 0.05
            3 -> if (maxValue == 3) 0.7 else 0.1
            4 -> 0.3
            else -> 0.0
        }
    }

    fun spinWheel(
        viewImageWheel: ImageView,
        context: Context,
    ) {
        viewModelScope.launch(dispatcherMain) {
            if (isSpinningWheel && !isFinished) return@launch
            isSpinningWheel = true
            isFinished = false
            lastBet = bet.value
            setBalance(balance.value - bet.value, context)
            val fromDegrees = sectorDegrees[sectorIndex].toFloat()
            sectorIndex = random.nextInt(0, sectorDegrees.size)
            val toDegrees = (360 * sectorDegrees.size).toFloat() + sectorDegrees[sectorIndex]
            val rotateAnimation = RotateAnimation(
                fromDegrees,
                toDegrees,
                RotateAnimation.RELATIVE_TO_SELF,
                0.5f,
                RotateAnimation.RELATIVE_TO_SELF,
                0.5f
            )
            rotateAnimation.run {

                duration = spinningDuration
                fillAfter = true
                interpolator = AccelerateDecelerateInterpolator()

                setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationStart(animation: Animation?) {} // not needed

                    override fun onAnimationEnd(animation: Animation?) {
                        val sectorPrize = sectorsPrizes[sectorsPrizes.size - (sectorIndex + 1)]
                        val isUserWon = sectorPrize != 0.0

                        if (isUserWon) {
                            setIsPlayingSoundWin(true)
                            setBalance(balance.value + (sectorPrize * bet.value).toInt(), context)
                            setWinNumber(winNumber.value + 1, context)
                            setLvl(lvl.value, context)
                        } else setIsPlayingSoundLose(true)

                        setWin((sectorPrize * bet.value).toInt())

                        isSpinningWheel = false
                        isFinished = true
                    }

                    override fun onAnimationRepeat(animation: Animation?) {} // not needed

                })
            }
            viewImageWheel.startAnimation(rotateAnimation)
        }
    }

    fun spinSlots(
        recyclers: List<RecyclerView?>,
        context: Context,
        scope: CoroutineScope = viewModelScope,
    ) {
        scope.launch(dispatcherMain) {
            if (isSpinningSlots && !isFinished) return@launch
            isFinished = false
            lastBet = bet.value
            isSpinningSlots = true
            setBalance(balance.value - bet.value, context)
            generateNewPositions()
            repeat(3) { index ->
                scroll(recyclers[index], index, context)
            }
        }
    }

    private fun scroll(
        recycler: RecyclerView?,
        index: Int,
        context: Context,
    ) {
        viewModelScope.launch(dispatcherMain) {
            val smoothScroller = object : LinearSmoothScroller(context) {
                override fun getVerticalSnapPreference(): Int {
                    return SNAP_TO_START
                }

                override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics?): Float {
                    return 0.3f
                }
            }
            smoothScroller.targetPosition = positions[index]
            recycler?.layoutManager?.startSmoothScroll(smoothScroller)
            if (index == latestIndex) attachListener(recycler, context)
        }
    }

    private fun attachListener(column: RecyclerView?, context: Context) {
        viewModelScope.launch(dispatcherIO) {
            column?.addOnScrollListener(
                object : RecyclerView.OnScrollListener() {
                    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                            if (!isFinished) {
                                checkSlotsCombo(context)
                                isSpinningSlots = false
                                isFinished = true
                            }
                            recyclerView.removeOnScrollListener(this)
                        }
                    }
                }
            )
        }
    }

    private fun checkSlotsCombo(
        context: Context,
    ) {
        val leftTopImage = getImageIdById(positions[0], leftSlots)
        val centerTopImage = getImageIdById(positions[1], centerSlots)
        val rightTopImage = getImageIdById(positions[2], rightSlots)
        val leftCenterImage = getImageIdById(positions[0] + 1, leftSlots)
        val centerCenterImage = getImageIdById(positions[1] + 1, centerSlots)
        val rightCenterImage = getImageIdById(positions[2] + 1, rightSlots)
        val leftBottomImage = getImageIdById(positions[0] + 2, leftSlots)
        val centerBottomImage = getImageIdById(positions[1] + 2, centerSlots)
        val rightBottomImage = getImageIdById(positions[2] + 2, rightSlots)
        // win if images are the same
        var creditsWon = 0
        if (leftTopImage == centerTopImage && centerTopImage == rightTopImage) {
            creditsWon += bet.value * 10
        }
        if (leftCenterImage == centerCenterImage && centerCenterImage == rightCenterImage) {
            creditsWon += bet.value * 10
        }
        if (leftBottomImage == centerBottomImage && centerBottomImage == rightBottomImage) {
            creditsWon += bet.value * 10
        }
        if (creditsWon > 0) {
            setIsPlayingSoundWin(true)
            setBalance(balance.value + creditsWon, context)
            setWin(creditsWon)
            setWinNumber(winNumber.value + 1, context)
            setLvl(lvl.value, context)
        } else setIsPlayingSoundLose(true)

        setWin(creditsWon)
    }

    fun generateSlots(amount: Int = 50, gameId: Int) {
        repeat(amount) { id ->
            leftSlots.add(Slot(id = id, imageId = ImageUtility.getRandomImageId(gameId)))
            centerSlots.add(Slot(id = id, imageId = ImageUtility.getRandomImageId(gameId)))
            rightSlots.add(Slot(id = id, imageId = ImageUtility.getRandomImageId(gameId)))
        }
    }

    private fun getImageIdById(id: Int, slots: MutableList<Slot>): Int {
        var imageId = 0
        slots.forEach {
            if (it.id == id) {
                imageId = it.imageId
                return@forEach
            }
        }
        return imageId
    }

    // gen positions and define the latest index
    // which means the column that will stop scrolling latest
    private fun generateNewPositions() {
        var biggestDiff = 0
        repeat(3) { index ->
            val currentPosition = positions[index]
            val newPosition = getNewPosition(index)
            positions[index] = newPosition
            val diff = (currentPosition - newPosition).absoluteValue
            if (diff > biggestDiff) {
                biggestDiff = diff
                latestIndex = index
            }
        }
    }

    // position always will be the top one
    // never be as the previous value
    // at least 20 positions before or after the previous one
    private fun getNewPosition(index: Int): Int {
        val newPosition = random.nextInt(0, leftSlots.lastIndex - 1)
        return if (positions[index] == newPosition ||
            (positions[index] - newPosition).absoluteValue < 20
        ) getNewPosition(index) else newPosition
    }

    fun setBalance(value: Int, context: Context) {
        viewModelScope.launch(dispatcherIO) {
            _balance.emit(
                // for infinite credits
                when {
                    Constants.balanceDefault < bet.value -> bet.value
                    value < bet.value && value != 0 -> Constants.balanceDefault
                    else -> value
                }
            )
            if (isUserAnonymous) return@launch
            DataManager.saveBalance(context, value)
        }
    }

    fun setWin(value: Int) {
        viewModelScope.launch {
            _win.emit(value)
        }
    }

    fun setIsPlayingSoundWin(value: Boolean) {
        viewModelScope.launch {
            _isPlayingSoundWin.emit(value)
        }
    }

    fun setIsPlayingSoundLose(value: Boolean) {
        viewModelScope.launch {
            _isPlayingSoundLose.emit(value)
        }
    }

    private fun setMinerStateChanged(value: Boolean) {
        viewModelScope.launch {
            _minerStateChanged.emit(value)
        }
    }

    fun setBet(value: Int, context: Context) {
        viewModelScope.launch(dispatcherIO) {
            _bet.emit(value)
            if (isUserAnonymous) return@launch
            DataManager.saveBet(context, value)
        }
    }

    fun setLvl(value: Int, context: Context) {
        viewModelScope.launch(dispatcherIO) {
            _lvl.emit(getCurrentLvl())
            if (isUserAnonymous) return@launch
            DataManager.saveLvl(context, value)
        }
    }

    private fun getCurrentLvl(): Int {
        return when (winNumber.value) {
            in 0..10 -> 1
            in 10..20 -> 2
            in 20..30 -> 3
            in 30..40 -> 4
            else -> winNumber.value / 10
        }
    }

    fun setWinNumber(value: Int, context: Context) {
        viewModelScope.launch(dispatcherIO) {
            _winNumber.emit(value)
            if (isUserAnonymous) return@launch
            DataManager.saveWinNumber(context, value)
        }
    }

    fun increaseBet(context: Context) {
        if (
            bet.value < balance.value
            && bet.value + Constants.betDefault <= balance.value
            && !isSpinningSlots
        ) {
            setBet(bet.value + Constants.betDefault, context)
        }
    }

    fun decreaseBet(context: Context) {
        if (bet.value > Constants.betDefault && !isSpinningSlots) {
            setBet(bet.value - Constants.betDefault, context)
        }
    }

    fun signIn(context: Context, login: String) {
        this.login = login
        isUserAnonymous = false
        viewModelScope.launch(dispatcherIO) {
            DataManager.saveLogin(context, login)
        }
    }

    fun resetScore(context: Context) {
        setBalance(Constants.balanceDefault, context)
        setBet(Constants.betDefault, context)
    }

    fun removeAccount(context: Context) {
        setBalance(Constants.balanceDefault, context)
        setBet(Constants.betDefault, context)
        setWinNumber(0, context)
        setLvl(0, context)
        login = ""
        privacy = false
        isUserAnonymous = true
        viewModelScope.launch(dispatcherIO) {
            DataManager.saveLogin(context, login)
            DataManager.savePrivacy(context, false)
        }
    }

    fun resetPositions() {
        positions[0] = 0
        positions[1] = 0
        positions[2] = 0
        positionsMinerLeft[0] = 0
        positionsMinerLeft[1] = 0
        positionsMinerLeft[2] = 0
        positionsMinerCenter[0] = 0
        positionsMinerCenter[1] = 0
        positionsMinerCenter[2] = 0
        positionsMinerRight[0] = 0
        positionsMinerRight[1] = 0
        positionsMinerRight[2] = 0
    }

    fun resetSlots() {
        leftSlots.clear()
        centerSlots.clear()
        rightSlots.clear()
    }

    private fun getDegreeForSectors() {
        viewModelScope.launch {
            val oneSectorDegree = 360 / sectorsPrizes.size
            repeat(sectorsPrizes.size) {
                sectorDegrees += (it + 1) * oneSectorDegree
            }
        }
    }

    private fun generateWinField(gameId: Int) {
        val numberOfSlots = if (gameId == 3) 9 else 4
        winField.clear()
        repeat(numberOfSlots) {
            winField += getMinerSlot(gameId)
        }
    }

    private fun getMinerSlot(gameId: Int): Int {
        val maxValue = if (gameId == 3) 4 else 3
        return random.nextInt(1, maxValue + 1)
    }

    fun loadSettings(context: Context) {
        viewModelScope.launch(dispatcherIO) {
            val isMusicOn = async { DataManager.loadMusicSetting(context) }
            val isSoundOn = async { DataManager.loadSoundSetting(context) }
            val isVibrationOn = async { DataManager.loadVibrationSetting(context) }
            this@GameViewModel.isMusicOn = isMusicOn.await()
            this@GameViewModel.isSoundOn = isSoundOn.await()
            this@GameViewModel.isVibrationOn = isVibrationOn.await()
        }
    }
}