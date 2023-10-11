package com.caty.lucky

import android.content.Context
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import com.caty.lucky.managers.MngData
import com.caty.lucky.managers.MngData.scoreDefault
import com.caty.lucky.managers.MngData.theBetDefault
import android.widget.ImageView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView
import com.caty.lucky.managers.MngImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.caty.lucky.managers.MngView
import com.caty.lucky.adapter.Slots
import kotlinx.coroutines.launch
import java.util.Date
import kotlin.math.absoluteValue
import kotlin.random.Random

class CatViewModel : ViewModel() {

    private val winField = mutableListOf<Int>()
    val positionsMinerLeft = mutableListOf(0, 0, 0)
    val positionsMinerCenter = mutableListOf(0, 0, 0)
    val positionsMinerRight = mutableListOf(0, 0, 0)

    private var email = ""

    var privacy = false
    var isMusicSetOn = false
    var isVibroSetOn = false
    val signedIn get() = email.isNotEmpty()
    var isUserAnonymous = true

    private val _currentScores = MutableStateFlow(scoreDefault)
    val currentScores = _currentScores.asStateFlow()

    private val _minerStateChanged = MutableStateFlow(false)
    val minerStateChanged = _minerStateChanged.asStateFlow()

    private val _lastResult = MutableStateFlow(0)
    val lastResult = _lastResult.asStateFlow()

    private val _currentBet = MutableStateFlow(theBetDefault)
    val currentBet = _currentBet.asStateFlow()

    private val _lvl = MutableStateFlow(1)
    val lvl = _lvl.asStateFlow()

    private val _winNumber = MutableStateFlow(0)
    val winNumber = _winNumber.asStateFlow()

    private val _playSoundWin = MutableStateFlow(false)
    val playSoundWin = _playSoundWin.asStateFlow()

    private val _playSoundLose = MutableStateFlow(false)
    val playSoundLose = _playSoundLose.asStateFlow()

    val leftPosSlot = mutableListOf<Slots>()
    val centerPosSlot = mutableListOf<Slots>()
    val rightPosSlot = mutableListOf<Slots>()
    var areSlotsGoing = false
    private var latestIndex = 0
    private val uniRandom = Random(Date().time)


    val positionsSlotGame = mutableListOf(0, 0, 0)
    private var spinDurWheel = 4000L
    private val wheelPrizeList =
        doubleArrayOf(0.0, 1.2, 1.0, 0.0, 1.2, 1.0, 1.2, 2.0, 1.0, 1.2)

    private val degreeList = mutableListOf<Int>()
    private var sectorWheelIndex = 9

    var isWheelGoing = false
    var lastBet = uniRandom.nextInt(0, 1)
    var isAlreadyFinished = true
    var isNowFinishing = false

    init {
        genSectors()
    }

    fun playMiner(gameId: Int, context: Context) {
        if (!isAlreadyFinished || isNowFinishing) return
        isAlreadyFinished = false
        lastBet = currentBet.value
        setScore(currentScores.value - currentBet.value, context)
        generateWinField(gameId)
        resetPositions()
        setMinerStateChanged(!minerStateChanged.value)
    }

    fun discoverCard(gameId: Int, index: Int, view: ImageView) {
        if (isAlreadyFinished) return
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
        view.setImageResource(MngImage.getDrawableById(winField[index], gameId))
        if (noMinesMore(gameId)) isNowFinishing = true
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
            prize += currentBet.value * getMultiplier(maxValue, it)
        }
        if (prize > 0) {
            playWin(true)
            setScore(currentScores.value + prize.toInt(), context)
            setLastResult(prize.toInt())
            setWinNumber(winNumber.value + 1, context)
            setLvl(lvl.value, context)
        } else playLose(true)
        lastBet = 0
        isAlreadyFinished = true
        isNowFinishing = false
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

    fun spinGameWheel(
        wheel: ImageView,
        context: Context,
    ) {
        viewModelScope.launch(CatApp.dispatcherMain) {
            if (isWheelGoing && !isAlreadyFinished) return@launch
            isWheelGoing = true
            isAlreadyFinished = false
            lastBet = currentBet.value
            setScore(currentScores.value - currentBet.value, context)
            val fromDegrees = degreeList[sectorWheelIndex].toFloat()
            sectorWheelIndex = uniRandom.nextInt(0, degreeList.size)
            val toDegrees = (360 * degreeList.size).toFloat() + degreeList[sectorWheelIndex]
            val rotateAnimation = RotateAnimation(
                fromDegrees,
                toDegrees,
                RotateAnimation.RELATIVE_TO_SELF,
                0.5f,
                RotateAnimation.RELATIVE_TO_SELF,
                0.5f
            )
            rotateAnimation.run {

                interpolator = AccelerateDecelerateInterpolator()
                fillAfter = true
                duration = spinDurWheel

                setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationStart(animation: Animation?) {} // ...

                    override fun onAnimationEnd(animation: Animation?) {
                        val sectorPrize =
                            wheelPrizeList[wheelPrizeList.size - (sectorWheelIndex + 1)]
                        val isUserWon = sectorPrize != 0.0

                        if (isUserWon) {
                            playWin(true)
                            setScore(
                                currentScores.value + (sectorPrize * currentBet.value).toInt(),
                                context
                            )
                            setWinNumber(winNumber.value + 1, context)
                            setLvl(lvl.value, context)
                        } else playLose(true)

                        setLastResult((sectorPrize * currentBet.value).toInt())

                        isWheelGoing = false
                        isAlreadyFinished = true
                    }

                    override fun onAnimationRepeat(animation: Animation?) {} // ...

                })
            }
            wheel.startAnimation(rotateAnimation)
        }
    }

    fun spinGameSlots(
        recyclers: List<RecyclerView?>,
        context: Context,
        scope: CoroutineScope = viewModelScope,
    ) {
        scope.launch(CatApp.dispatcherMain) {
            if (areSlotsGoing && !isAlreadyFinished) return@launch
            isAlreadyFinished = false
            lastBet = currentBet.value
            areSlotsGoing = true
            setScore(currentScores.value - currentBet.value, context)
            genPos()
            repeat(3) { index ->
                MngView.goTo(
                    recyclers[index],
                    index,
                    context,
                    viewModelScope,
                    positionsSlotGame,
                    latestIndex,
                    attachListener = { attachSlotsListener(recyclers[index], context) }
                )
            }
        }
    }


    private fun attachSlotsListener(column: RecyclerView?, context: Context) {
        viewModelScope.launch(CatApp.dispatcherIO) {
            column?.addOnScrollListener(
                object : RecyclerView.OnScrollListener() {
                    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                            if (!isAlreadyFinished) {
                                calculateSlotGameResult(context)
                                areSlotsGoing = false
                                isAlreadyFinished = true
                            }
                            recyclerView.removeOnScrollListener(this)
                        }
                    }
                }
            )
        }
    }

    private fun calculateSlotGameResult(
        context: Context,
    ) {
        var creditsWon = 0

        val leftTop = getImageCode(positionsSlotGame[0], leftPosSlot)
        val rightTop = getImageCode(positionsSlotGame[2], rightPosSlot)
        val centerTop = getImageCode(positionsSlotGame[1], centerPosSlot)
        val leftCenter = getImageCode(positionsSlotGame[0] + 1, leftPosSlot)
        val rightCenter = getImageCode(positionsSlotGame[2] + 1, rightPosSlot)
        val centerCenter = getImageCode(positionsSlotGame[1] + 1, centerPosSlot)
        val leftBottom = getImageCode(positionsSlotGame[0] + 2, leftPosSlot)
        val rightBottom = getImageCode(positionsSlotGame[2] + 2, rightPosSlot)
        val centerBottom = getImageCode(positionsSlotGame[1] + 2, centerPosSlot)


        if (leftTop == centerTop && centerTop == rightTop) {
            creditsWon += currentBet.value * 10
        }
        if (leftCenter == centerCenter && centerCenter == rightCenter) {
            creditsWon += currentBet.value * 10
        }
        if (leftBottom == centerBottom && centerBottom == rightBottom) {
            creditsWon += currentBet.value * 10
        }
        when {
            creditsWon > 0 -> {
                playWin(true)
                setLastResult(creditsWon)
                setScore(currentScores.value + creditsWon, context)
                setWinNumber(winNumber.value + 1, context)
                setLvl(lvl.value, context)
            }

            else -> playLose(true)
        }

        setLastResult(creditsWon)
    }

    private fun getImageCode(id: Int, slots: MutableList<Slots>): Int {
        var imageId = 0
        slots.forEach {
            if (it.id == id) {
                imageId = it.imageId
                return@forEach
            }
        }
        return imageId
    }

    fun genSlots(amount: Int = 50, gameId: Int) {
        repeat(amount) { id ->
            leftPosSlot.add(Slots(id, MngImage.getRandomImageId(gameId)))
            centerPosSlot.add(Slots(id, MngImage.getRandomImageId(gameId)))
            rightPosSlot.add(Slots(id, MngImage.getRandomImageId(gameId)))
        }
    }

    private fun genPos() {
        var biggestDiff = 0
        repeat(3) { index ->
            val currentPosition = positionsSlotGame[index]
            val newPosition = getTheNewPos(index)
            positionsSlotGame[index] = newPosition
            val diff = (currentPosition - newPosition).absoluteValue
            if (diff > biggestDiff) {
                biggestDiff = diff
                latestIndex = index
            }
        }
    }

    private fun getTheNewPos(index: Int): Int {
        val pos = uniRandom.nextInt(0, leftPosSlot.lastIndex - 1)
        return when {
            positionsSlotGame[index] == pos ||
                    (positionsSlotGame[index] - pos).absoluteValue < 20
            -> getTheNewPos(index)

            else -> pos
        }
    }

    fun setLastResult(value: Int) {
        viewModelScope.launch {
            _lastResult.emit(value)
        }
    }

    fun setScore(value: Int, context: Context) {
        viewModelScope.launch(CatApp.dispatcherIO) {
            _currentScores.emit(
                when {
                    scoreDefault < currentBet.value -> currentBet.value
                    value < currentBet.value && value != 0 -> scoreDefault
                    else -> value
                }
            )
            if (isUserAnonymous) return@launch
            MngData.saveScore(context, value)
        }
    }

    fun playLose(value: Boolean) {
        viewModelScope.launch {
            _playSoundLose.emit(value)
        }
    }

    fun playWin(value: Boolean) {
        viewModelScope.launch {
            _playSoundWin.emit(value)
        }
    }

    private fun setMinerStateChanged(value: Boolean) {
        viewModelScope.launch {
            _minerStateChanged.emit(value)
        }
    }

    fun setBet(value: Int, context: Context) {
        viewModelScope.launch(CatApp.dispatcherIO) {
            _currentBet.emit(value)
            if (isUserAnonymous) return@launch
            MngData.saveCurrentBet(context, value)
        }
    }

    fun setLvl(value: Int, context: Context) {
        viewModelScope.launch(CatApp.dispatcherIO) {
            _lvl.emit(getCurrentLvl())
            if (isUserAnonymous) return@launch
            MngData.saveLvl(context, value)
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
        viewModelScope.launch(CatApp.dispatcherIO) {
            _winNumber.emit(value)
            if (isUserAnonymous) return@launch
            MngData.saveWinNumber(context, value)
        }
    }

    fun makeLessBet(context: Context) {
        if (currentBet.value > theBetDefault && !areSlotsGoing) {
            setBet(currentBet.value - theBetDefault, context)
        }
    }

    fun makeMoreBet(context: Context) {
        if (
            currentBet.value < currentScores.value
            && currentBet.value + theBetDefault <= currentScores.value
            && !areSlotsGoing
        ) {
            setBet(currentBet.value + theBetDefault, context)
        }
    }

    fun resetScore(context: Context) {
        setScore(scoreDefault, context)
        setBet(theBetDefault, context)
    }

    fun signIn(context: Context, email: String) {
        this.email = email
        isUserAnonymous = false
        viewModelScope.launch(CatApp.dispatcherIO) {
            MngData.saveEmail(context, email)
        }
    }

    fun removeAccount(context: Context) {
        setScore(scoreDefault, context)
        setBet(theBetDefault, context)
        setWinNumber(0, context)
        setLvl(0, context)
        email = ""
        privacy = false
        isUserAnonymous = true
        viewModelScope.launch(CatApp.dispatcherIO) {
            MngData.saveEmail(context, email)
            MngData.savePrivacy(context, false)
        }
    }

    fun resetPositions() {
        positionsSlotGame[0] = 0
        positionsSlotGame[1] = 0
        positionsSlotGame[2] = 0
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

    fun resetAllSlots() {
        leftPosSlot.clear()
        centerPosSlot.clear()
        rightPosSlot.clear()
    }

    private fun genSectors() {
        viewModelScope.launch {
            val oneSectorDegree = 360 / wheelPrizeList.size
            repeat(wheelPrizeList.size) {
                degreeList += (it + 1) * oneSectorDegree
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
        return uniRandom.nextInt(1, maxValue + 1)
    }

    fun setup(context: Context) {
        viewModelScope.launch(CatApp.dispatcherIO) {
            val isMusicOn = async { MngData.loadMusicSetting(context) }
            val isVibrationOn = async { MngData.loadVibrationSetting(context) }
            this@CatViewModel.isMusicSetOn = isMusicOn.await()
            this@CatViewModel.isVibroSetOn = isVibrationOn.await()
        }
    }

    fun suddenEnding(context: Context, gameId: Int) {
        when (gameId) {
            0 -> {
                if (isWheelGoing) {
                    setScore(currentScores.value + lastBet, context)
                } else return
                isAlreadyFinished = true
                isWheelGoing = false
                lastBet = 0
            }

            else -> {
                if (areSlotsGoing) {
                    setScore(currentScores.value + lastBet, context)
                } else return
                isAlreadyFinished = true
                areSlotsGoing = false
                lastBet = 0
            }
        }
    }
}