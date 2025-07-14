package com.example.lingxis2048

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlin.math.abs
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.Font

private val appFontFamily = FontFamily(
    Font(R.font.leigo_regular)
)

@Composable
fun GameScreen(gameViewModel: GameViewModel = viewModel()) {
    val score by gameViewModel.score
    val isGameOver by gameViewModel.isGameOver
    var totalDrag by remember { mutableStateOf(Offset.Zero) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { totalDrag = Offset.Zero },
                    onDrag = { _, dragAmount -> totalDrag += dragAmount },
                    onDragEnd = {
                        val swipeThreshold = 50f
                        val (dx, dy) = totalDrag
                        if (abs(dx) > abs(dy)) {
                            if (abs(dx) > swipeThreshold) {
                                if (dx > 0) gameViewModel.onSwipe(SwipeDirection.RIGHT)
                                else gameViewModel.onSwipe(SwipeDirection.LEFT)
                            }
                        } else {
                            if (abs(dy) > swipeThreshold) {
                                if (dy > 0) gameViewModel.onSwipe(SwipeDirection.DOWN)
                                else gameViewModel.onSwipe(SwipeDirection.UP)
                            }
                        }
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("LingXi's 2048", fontSize = 32.sp, fontWeight = FontWeight.Bold, fontFamily = appFontFamily)
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Score: $score", fontSize = 24.sp, fontFamily = appFontFamily)
                Button(onClick = { gameViewModel.startGame() }) {
                    Text("New Game", fontFamily = appFontFamily)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
            ) {
                val tileSize = maxWidth / gameViewModel.gridSize

                Grid(gameViewModel.gridSize, tileSize)

                gameViewModel.tiles.forEach { tile ->
                    key(tile.id) {
                        AnimatedTile(tile = tile, tileSize = tileSize)
                    }
                }

                gameViewModel.scoreAnimation.forEach { data ->
                    key(data.id) {
                        ScorePopup(data = data, tileSize = tileSize)
                    }
                }
            }

            if (isGameOver) {
                Spacer(modifier = Modifier.height(16.dp))
                Text("Game Over!", fontSize = 32.sp, color = Color.Red, fontFamily = appFontFamily)
            }
        }
    }
}

@Composable
fun Grid(gridSize: Int, tileSize: Dp) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.LightGray, RoundedCornerShape(8.dp))
    ) {
        for (i in 0 until gridSize) {
            for (j in 0 until gridSize) {
                Box(
                    modifier = Modifier
                        .offset(x = tileSize * j, y = tileSize * i)
                        .size(tileSize)
                        .padding(4.dp)
                        .background(Color(0xFFCDC1B4), RoundedCornerShape(8.dp))
                )
            }
        }
    }
}

@Composable
fun AnimatedTile(tile: Tile, tileSize: Dp) {
    val animatedX = animateDpAsState(
        targetValue = tileSize * tile.position.second,
        animationSpec = spring(stiffness = 500f),
        label = "animatedX"
    )
    val animatedY = animateDpAsState(
        targetValue = tileSize * tile.position.first,
        animationSpec = spring(stiffness = 500f),
        label = "animatedY"
    )

    val animatedScale by animateFloatAsState(
        targetValue = if (tile.isNew || tile.isMerged) 1f else 0.95f, // Start slightly smaller and pop
        animationSpec = spring(
            dampingRatio = if (tile.isNew || tile.isMerged) 0.6f else Spring.DampingRatioNoBouncy,
            stiffness = if (tile.isNew || tile.isMerged) 250f else 500f
        ),
        label = "animatedScale"
    )

    val animatedAlpha by animateFloatAsState(
        targetValue = if (tile.isMerging) 0f else 1f,
        animationSpec = tween(durationMillis = 150),
        label = "animatedAlpha"
    )

    // A second scale animation for the pop effect
    var popScale by remember { mutableStateOf(if (tile.isNew || tile.isMerged) 0.5f else 1f) }
    LaunchedEffect(tile.id) {
        if (tile.isNew || tile.isMerged) {
            popScale = 1f
        }
    }
    val animatedPopScale by animateFloatAsState(
        targetValue = popScale,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 250f),
        label = "popScale"
    )

    Box(
        modifier = Modifier
            .offset(x = animatedX.value, y = animatedY.value)
            .size(tileSize)
            .padding(4.dp)
            .scale(animatedScale * animatedPopScale)
            .alpha(animatedAlpha)
            .background(getTileColor(tile.value), RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = tile.value.toString(),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = if (tile.value > 4) Color.White else Color.Black,
            fontFamily = appFontFamily
        )
    }
}

@Composable
fun ScorePopup(data: ScoreAnimationData, tileSize: Dp) {
    var startAnimation by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        startAnimation = true
    }

    val animatedY = animateDpAsState(
        targetValue = if (startAnimation) (tileSize * data.position.first) - 80.dp else (tileSize * data.position.first) - 20.dp,
        animationSpec = tween(durationMillis = 1000, easing = LinearEasing),
        label = "scoreY"
    )
    val alpha by animateFloatAsState(
        targetValue = if (startAnimation) 0f else 1f,
        animationSpec = tween(durationMillis = 1000, easing = LinearEasing),
        label = "scoreAlpha"
    )

    Box(
        modifier = Modifier
            .offset(
                x = tileSize * data.position.second,
                y = animatedY.value
            )
            .size(tileSize)
            .alpha(alpha),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "+${data.value}",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF6d5f57),
            fontFamily = appFontFamily
        )
    }
}

fun getTileColor(value: Int): Color {
    return when (value) {
        2 -> Color(0xFFEEE4DA)
        4 -> Color(0xFFEDE0C8)
        8 -> Color(0xFFF2B179)
        16 -> Color(0xFFF59563)
        32 -> Color(0xFFF67C5F)
        64 -> Color(0xFFF65E3B)
        128 -> Color(0xFFEDCF72)
        256 -> Color(0xFFEDCC61)
        512 -> Color(0xFFEDC850)
        1024 -> Color(0xFFEDC53F)
        2048 -> Color(0xFFEDC22E)
        else -> Color(0xFF3C3A32)
    }
}