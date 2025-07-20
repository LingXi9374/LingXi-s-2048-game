package org.lingxi9374.game2048

import android.app.Application
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.random.Random

enum class SwipeDirection {
    UP, DOWN, LEFT, RIGHT
}

data class Tile(
    val id: String = UUID.randomUUID().toString(),
    var value: Int,
    var position: Pair<Int, Int>,
    var isNew: Boolean = true,
    var isMerged: Boolean = false,
    var mergedFrom: List<Tile>? = null,
    val isMerging: Boolean = false
)

data class ScoreAnimationData(val value: Int, val position: Pair<Int, Int>, val id: String = UUID.randomUUID().toString())

class GameViewModel(application: Application) : AndroidViewModel(application) {
    val gridSize = 4
    val tiles = mutableStateListOf<Tile>()
    val score = mutableIntStateOf(0)
    val isGameOver = mutableStateOf(false)
    val timeElapsed = mutableLongStateOf(0L)
    val scoreAnimation = mutableStateListOf<ScoreAnimationData>()
    val isPaused = mutableStateOf(false)
    private var timerJob: Job? = null
    private var isGameStarted = false
    private var timerStartTime = 0L
    private var timePaused = 0L
    private val soundManager = SoundManager(application.applicationContext)
    private val historyManager = HistoryManager(application.applicationContext)

    init {
        startGame()
    }

    fun startGame() {
        timerJob?.cancel()
        tiles.clear()
        score.intValue = 0
        isGameOver.value = false
        isGameStarted = false
        isPaused.value = false
        timeElapsed.longValue = 0L
        timerStartTime = 0L
        addRandomTile()
        addRandomTile()
    }

    fun pauseGame() {
        if (isGameOver.value || isPaused.value) return
        isPaused.value = true
        timerJob?.cancel()
        timePaused = System.currentTimeMillis()
    }

    fun togglePause() {
        if (isGameOver.value) return
        isPaused.value = !isPaused.value
        if (isPaused.value) {
            timerJob?.cancel()
            timePaused = System.currentTimeMillis()
        } else {
            if (isGameStarted) {
                timerStartTime += System.currentTimeMillis() - timePaused
                startTimer()
            }
        }
    }

    private fun addRandomTile() {
        val emptyCells = mutableListOf<Pair<Int, Int>>()
        val occupied = tiles.map { it.position }
        for (r in 0 until gridSize) {
            for (c in 0 until gridSize) {
                if (Pair(r, c) !in occupied) {
                    emptyCells.add(Pair(r, c))
                }
            }
        }

        if (emptyCells.isNotEmpty()) {
            val pos = emptyCells.random()
            val value = if (Random.nextFloat() < 0.9f) 2 else 4
            tiles.add(Tile(value = value, position = pos, isNew = true))
        }
    }

    fun onSwipe(direction: SwipeDirection) {
        if (isGameOver.value || isPaused.value) return

        if (!isGameStarted) {
            isGameStarted = true
            timerStartTime = System.currentTimeMillis()
            startTimer()
        }

        val nextTiles = tiles.map { it.copy(isNew = false, isMerged = false, isMerging = false) }.toMutableList()
        var moved = false
        var scoreIncrease = 0

        val (dx, dy) = when (direction) {
            SwipeDirection.UP -> 0 to -1
            SwipeDirection.DOWN -> 0 to 1
            SwipeDirection.LEFT -> -1 to 0
            SwipeDirection.RIGHT -> 1 to 0
        }

        val sortedTiles = nextTiles.toMutableList().sortedWith(compareBy(
            { if (dy == 1) -it.position.first else it.position.first },
            { if (dx == 1) -it.position.second else it.position.second }
        ))

        for (tile in sortedTiles) {
            val currentTile = nextTiles.find { it.id == tile.id } ?: continue
            if (currentTile.isMerging) continue

            var furthest = currentTile.position
            var nextPos = Pair(furthest.first + dy, furthest.second + dx)

            while (nextPos.first in 0 until gridSize && nextPos.second in 0 until gridSize) {
                if (nextTiles.any { it.position == nextPos && !it.isMerging }) {
                    break
                }
                furthest = nextPos
                nextPos = Pair(nextPos.first + dy, nextPos.second + dx)
            }

            val nextTileInList = nextTiles.find { it.position == nextPos && !it.isMerging && !it.isMerged }
            if (nextTileInList != null && nextTileInList.value == currentTile.value) {
                val mergedValue = currentTile.value * 2
                scoreIncrease += currentTile.value

                val mergedTile = Tile(
                    value = mergedValue,
                    position = nextTileInList.position,
                    isMerged = true
                )

                nextTiles.replaceAll { t ->
                    when (t.id) {
                        currentTile.id -> t.copy(position = nextTileInList.position, isMerging = true)
                        nextTileInList.id -> t.copy(isMerging = true) // This one is already at the target
                        else -> t
                    }
                }
                nextTiles.add(mergedTile)
                moved = true
            } else if (currentTile.position != furthest) {
                nextTiles.replaceAll { t ->
                    if (t.id == currentTile.id) t.copy(position = furthest) else t
                }
                moved = true
            }
        }

        if (moved) {
            if (scoreIncrease > 0) {
                soundManager.playSound(SoundManager.MERGE_SOUND)
            } else {
                soundManager.playSound(SoundManager.MOVE_SOUND)
            }
            score.intValue += scoreIncrease

            // 1. Set the state that includes all moving and merging tiles.
            tiles.clear()
            tiles.addAll(nextTiles)

            // 2. Immediately add the new random tile so its animation starts concurrently.
            addRandomTile()

            // 3. Handle animations and cleanup in separate coroutines.
            // Animate the score popup after the merge animation has had time to play.
            if (scoreIncrease > 0) {
                viewModelScope.launch {
                    delay(200) // Delay for visual merge to happen
                    val mergedTiles = nextTiles.filter { it.isMerged }
                    for (mergedTile in mergedTiles) {
                        val animationData = ScoreAnimationData(mergedTile.value, mergedTile.position)
                        scoreAnimation.add(animationData)
                        launch {
                            delay(3000) // Duration of the score popup
                            scoreAnimation.remove(animationData)
                        }
                    }
                }
            }

            // Clean up the tiles that were part of a merge after their fade-out animation.
            viewModelScope.launch {
                delay(150) // Must be >= the alpha animation duration in AnimatedTile
                tiles.removeAll { it.isMerging }
                checkGameOver()
            }
        }
    }

    private fun startTimer() {
        timerJob = viewModelScope.launch {
            while (isActive) {
                timeElapsed.longValue = System.currentTimeMillis() - timerStartTime
                delay(3) // UI refresh rate
            }
        }
    }

    private fun checkGameOver() {
        if (tiles.size < gridSize * gridSize) return

        for (tile in tiles) {
            val (r, c) = tile.position
            if (c < gridSize - 1 && tiles.any { it.position == Pair(r, c + 1) && it.value == tile.value }) return
            if (r < gridSize - 1 && tiles.any { it.position == Pair(r + 1, c) && it.value == tile.value }) return
        }
        isGameOver.value = true
        timerJob?.cancel() // Stop the timer when the game is over

        viewModelScope.launch {
            val maxTile = tiles.maxOfOrNull { it.value } ?: 0
            val historyEntry = HistoryEntry(
                score = score.intValue,
                timeElapsed = timeElapsed.longValue,
                maxTile = maxTile
            )
            historyManager.addHistoryEntry(historyEntry)
        }
    }
    
    override fun onCleared() {
        soundManager.release()
        super.onCleared()
    }
}