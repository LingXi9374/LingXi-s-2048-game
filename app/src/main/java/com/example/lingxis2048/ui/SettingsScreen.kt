package com.example.lingxis2048.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.lingxis2048.SettingsManager

@Composable
fun SettingsScreen(navController: NavController) {
    val context = LocalContext.current
    val settingsManager = remember { SettingsManager(context) }

    var soundEnabled by remember { mutableStateOf(settingsManager.isSoundEnabled()) }
    var soundVolume by remember { mutableStateOf(settingsManager.getSoundVolume()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Enable Sound", style = MaterialTheme.typography.bodyLarge)
            Switch(
                checked = soundEnabled,
                onCheckedChange = {
                    soundEnabled = it
                    settingsManager.setSoundEnabled(it)
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Volume: ${(soundVolume * 100).toInt()}%", style = MaterialTheme.typography.bodyLarge)
        Slider(
            value = soundVolume,
            onValueChange = {
                soundVolume = it
                settingsManager.setSoundVolume(it)
            },
            steps = 9
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(onClick = { navController.navigate("about") }) {
            Text("About This Game", style = MaterialTheme.typography.bodyLarge)
        }
    }
}
