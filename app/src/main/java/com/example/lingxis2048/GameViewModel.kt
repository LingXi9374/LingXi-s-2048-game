package com.example.lingxis2048

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
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

class GameViewModel : ViewModel() {
    val gridSize = 4
    val tiles = mutableStateListOf<Tile>()
    val score = mutableStateOf(0)
    val isGameOver = mutableStateOf(false)
    val scoreAnimation = mutableStateListOf<ScoreAnimationData>()

    init {
        startGame()
    }

    fun startGame() {
        tiles.clear()
        score.value = 0
        isGameOver.value = false
        addRandomTile()
        addRandomTile()
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
        if (isGameOver.value) return

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
            score.value += scoreIncrease

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

    private fun checkGameOver() {
        if (tiles.size < gridSize * gridSize) return

        for (tile in tiles) {
            val (r, c) = tile.position
            if (c < gridSize - 1 && tiles.any { it.position == Pair(r, c + 1) && it.value == tile.value }) return
            if (r < gridSize - 1 && tiles.any { it.position == Pair(r + 1, c) && it.value == tile.value }) return
        }
        isGameOver.value = true
    }
}