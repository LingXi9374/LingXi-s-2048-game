package com.example.lingxis2048

import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.*
import kotlin.math.abs

private val appFontFamily = FontFamily(
    Font(R.font.leigo_regular)
)

@Composable
fun GameScreen(navController: NavController) {
    val context = LocalContext.current
    val factory = GameViewModelFactory((context as Activity).application)
    val gameViewModel: GameViewModel = viewModel(factory = factory)
    val score by gameViewModel.score
    val isGameOver by gameViewModel.isGameOver

    val gestureModifier = Modifier.pointerInput(Unit) {
        var totalDrag by mutableStateOf(Offset.Zero)
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
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isLandscape = maxWidth > maxHeight

        if (isLandscape) {
            // Landscape Layout
            Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                Text(
                    text = "LingXi's 2048",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = appFontFamily,
                    modifier = Modifier.align(Alignment.TopStart)
                )

                GameBoard(
                    gameViewModel = gameViewModel,
                    modifier = Modifier
                        .fillMaxHeight()
                        .aspectRatio(1f)
                        .align(Alignment.Center)
                        .then(gestureModifier)
                )

                Text(
                    text = "Score: $score",
                    fontSize = 24.sp,
                    fontFamily = appFontFamily,
                    modifier = Modifier.align(Alignment.BottomStart)
                )

                Column(modifier = Modifier.align(Alignment.BottomEnd), horizontalAlignment = Alignment.End) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = { gameViewModel.startGame() }) {
                            Text("New Game", fontFamily = appFontFamily)
                        }
                        Button(onClick = { navController.navigate("settings") }) {
                            Text("Settings", fontFamily = appFontFamily)
                        }
                    }
                    AnimatedVisibility(visible = isGameOver) {
                        Text(
                            "Game Over!",
                            fontSize = 32.sp,
                            color = Color.Red,
                            fontFamily = appFontFamily,
                            modifier = Modifier.padding(top = 16.dp)
                        )
                    }
                }
            }
        } else {
            // Portrait Layout
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("LingXi's 2048", fontSize = 32.sp, fontWeight = FontWeight.Bold, fontFamily = appFontFamily)
                Spacer(modifier = Modifier.height(16.dp))
                Text("Score: $score", fontSize = 24.sp, fontFamily = appFontFamily)
                Spacer(modifier = Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { gameViewModel.startGame() }) {
                        Text("New Game", fontFamily = appFontFamily)
                    }
                    Button(onClick = { navController.navigate("settings") }) {
                        Text("Settings", fontFamily = appFontFamily)
                    }
                }

                GameBoard(
                    gameViewModel = gameViewModel,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .aspectRatio(1f)
                        .then(gestureModifier)
                )

                AnimatedVisibility(visible = isGameOver) {
                    Text(
                        "Game Over!",
                        fontSize = 32.sp,
                        color = Color.Red,
                        fontFamily = appFontFamily,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }
            }
        }
    }
}


@Composable
fun GameBoard(gameViewModel: GameViewModel, modifier: Modifier = Modifier) {
    BoxWithConstraints(
        modifier = modifier
            .background(Color.LightGray, RoundedCornerShape(8.dp))
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
            fontSize = (tileSize.value / 3).sp,
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
