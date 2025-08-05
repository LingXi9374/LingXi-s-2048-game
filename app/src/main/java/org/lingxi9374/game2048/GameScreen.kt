package org.lingxi9374.game2048

import android.annotation.SuppressLint
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import org.lingxi9374.game2048.ui.HybridFontText
import kotlin.math.abs

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun GameScreen(navController: NavController) {
    val application = (LocalContext.current.applicationContext as android.app.Application)
    val factory = GameViewModelFactory(application)
    val gameViewModel: GameViewModel = viewModel(factory = factory)
    val score by gameViewModel.score
    val isGameOver by gameViewModel.isGameOver
    val timeElapsed by gameViewModel.timeElapsed
    val isPaused by gameViewModel.isPaused

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_PAUSE) {
                gameViewModel.pauseGame()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

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

    BoxWithConstraints(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        val isLandscape = maxWidth > maxHeight

        if (isLandscape) {
            // Landscape Layout
            Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                HybridFontText(
                    text = stringResource(R.string.app_name),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.TopStart)
                )

                Column(modifier = Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                    Box {
                        GameBoard(
                            gameViewModel = gameViewModel,
                            modifier = Modifier
                                .fillMaxHeight(0.7f)
                                .aspectRatio(1f)
                                .then(if (isPaused) Modifier else gestureModifier)
                        )
                        if (isGameOver) {
                            Box(
                                modifier = Modifier
                                    .matchParentSize()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)),
                                contentAlignment = Alignment.Center
                            ) {
                                HybridFontText(
                                    stringResource(R.string.game_over),
                                    fontSize = 32.sp,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                        if (isPaused) {
                            Box(
                                modifier = Modifier
                                    .matchParentSize()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.Black.copy(alpha = 0.5f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = stringResource(R.string.game_paused),
                                    tint = Color.White,
                                    modifier = Modifier.size(100.dp)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        IconButton(onClick = { gameViewModel.startGame() }) {
                            Icon(Icons.Filled.Refresh, contentDescription = stringResource(R.string.game_new_game), tint = MaterialTheme.colorScheme.onBackground)
                        }
                        IconButton(onClick = { gameViewModel.togglePause() }, enabled = !isGameOver) {
                            Icon(
                                imageVector = if (isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                                contentDescription = if (isPaused) stringResource(R.string.game_resume) else stringResource(R.string.game_pause),
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }
                        IconButton(onClick = { 
                            gameViewModel.pauseGame()
                            navController.navigate("history") 
                        }) {
                            Icon(Icons.Filled.History, contentDescription = stringResource(R.string.history_title), tint = MaterialTheme.colorScheme.onBackground)
                        }
                        IconButton(onClick = { 
                            gameViewModel.pauseGame()
                            navController.navigate("settings") 
                        }) {
                            Icon(Icons.Filled.Settings, contentDescription = stringResource(R.string.settings_title), tint = MaterialTheme.colorScheme.onBackground)
                        }
                    }
                }

                HybridFontText(
                    text = stringResource(R.string.game_screen_score_label) + score.toString(),
                    fontSize = 24.sp,
                    modifier = Modifier.align(Alignment.BottomStart)
                )
                HybridFontText(
                    text = stringResource(R.string.game_time, formatTime(timeElapsed)),
                    fontSize = 24.sp,
                    modifier = Modifier.align(Alignment.BottomEnd)
                )
            }
        } else {
            // Portrait Layout
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                HybridFontText(stringResource(R.string.app_name), fontSize = 32.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    HybridFontText(stringResource(R.string.game_time, formatTime(timeElapsed)), fontSize = 24.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    HybridFontText(stringResource(R.string.game_screen_score_label) + score.toString(), fontSize = 24.sp)
                }
                Spacer(modifier = Modifier.height(16.dp))

                Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                    GameBoard(
                        gameViewModel = gameViewModel,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .then(if (isPaused) Modifier else gestureModifier)
                    )
                    if (isGameOver) {
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)),
                            contentAlignment = Alignment.Center
                        ) {
                            HybridFontText(
                                stringResource(R.string.game_over),
                                fontSize = 32.sp,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                    if (isPaused) {
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.Black.copy(alpha = 0.5f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = stringResource(R.string.game_paused),
                                tint = Color.White,
                                modifier = Modifier.size(100.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(onClick = { gameViewModel.startGame() }) {
                        Icon(Icons.Filled.Refresh, contentDescription = stringResource(R.string.game_new_game), tint = MaterialTheme.colorScheme.onBackground)
                    }
                    IconButton(onClick = { gameViewModel.togglePause() }, enabled = !isGameOver) {
                        Icon(
                            imageVector = if (isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                            contentDescription = if (isPaused) stringResource(R.string.game_resume) else stringResource(R.string.game_pause),
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    IconButton(onClick = { 
                        gameViewModel.pauseGame()
                        navController.navigate("history") 
                    }) {
                        Icon(Icons.Filled.History, contentDescription = stringResource(R.string.history_title), tint = MaterialTheme.colorScheme.onBackground)
                    }
                    IconButton(onClick = { 
                        gameViewModel.pauseGame()
                        navController.navigate("settings") 
                    }) {
                        Icon(Icons.Filled.Settings, contentDescription = stringResource(R.string.settings_title), tint = MaterialTheme.colorScheme.onBackground)
                    }
                }
            }
        }
    }
}

@SuppressLint("DefaultLocale")
private fun formatTime(milliseconds: Long): String {
    val hours = milliseconds / 3600000
    val minutes = (milliseconds % 3600000) / 60000
    val seconds = (milliseconds % 60000) / 1000
    val millis = milliseconds % 1000
    return String.format("%02d:%02d:%02d.%03d", hours, minutes, seconds, millis)
}


@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun GameBoard(gameViewModel: GameViewModel, modifier: Modifier = Modifier) {
    val boardBackgroundColor = if (isSystemInDarkTheme()) {
        Color(0xFF3A3A3C) // A dark grey for the seams in dark mode
    } else {
        Color(0xFFCCCCCC) // User-requested light grey for the seams
    }
    BoxWithConstraints(
        modifier = modifier
            .background(boardBackgroundColor, RoundedCornerShape(8.dp))
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
    val gridCellColor = if (isSystemInDarkTheme()) {
        Color(0xFF58585A) // A slightly lighter grey for the empty cells in dark mode
    } else {
        Color(0xFFCDC1B4) // Original empty cell color for light mode
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        for (i in 0 until gridSize) {
            for (j in 0 until gridSize) {
                Box(
                    modifier = Modifier
                        .offset(x = tileSize * j, y = tileSize * i)
                        .size(tileSize)
                        .padding(4.dp)
                        .background(gridCellColor, RoundedCornerShape(8.dp))
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
    var popScale by remember { mutableFloatStateOf(if (tile.isNew || tile.isMerged) 0.5f else 1f) }
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

    val tileBackgroundColor = getTileColor(value = tile.value)
    val textColor = if (isSystemInDarkTheme()) {
        if (tile.value > 4) Color.White else Color.Black
    } else {
        if (tile.value > 4) Color.White else Color.Black
    }

    Box(
        modifier = Modifier
            .offset(x = animatedX.value, y = animatedY.value)
            .size(tileSize)
            .padding(4.dp)
            .scale(animatedScale * animatedPopScale)
            .alpha(animatedAlpha)
            .background(tileBackgroundColor, RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
    ) {
        HybridFontText(
            text = tile.value.toString(),
            fontSize = (tileSize.value / 3).sp,
            fontWeight = FontWeight.Bold,
            color = textColor
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
        HybridFontText(
            text = "+${data.value}",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun getTileColor(value: Int): Color {
    val isDark = isSystemInDarkTheme()
    return if (isDark) {
        when (value) {
            2 -> Color(0xFF4E5D6A)
            4 -> Color(0xFF5A6A7A)
            8 -> Color(0xFF6A8A9A)
            16 -> Color(0xFF7AAABF)
            32 -> Color(0xFF8AC5E2)
            64 -> Color(0xFF9BDFFF)
            128 -> Color(0xFF7BC5E2)
            256 -> Color(0xFF5DAABF)
            512 -> Color(0xFF3E8F9A)
            1024 -> Color(0xFF1F757A)
            2048 -> Color(0xFF005F5A)
            else -> Color(0xFF19324A)
        }
    } else {
        when (value) {
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
}
