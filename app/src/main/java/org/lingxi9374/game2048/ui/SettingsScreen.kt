package org.lingxi9374.game2048.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import org.lingxi9374.game2048.LocalLocaleManager
import org.lingxi9374.game2048.R
import org.lingxi9374.game2048.SettingsManager
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {
    val context = LocalContext.current
    val settingsManager = remember { SettingsManager(context) }

    var soundEnabled by remember { mutableStateOf(settingsManager.isSoundEnabled()) }
    var soundVolume by remember { mutableFloatStateOf(settingsManager.getSoundVolume()) }
    var languageDropdownExpanded by remember { mutableStateOf(false) }
    val localeManager = LocalLocaleManager.current

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                title = { HybridFontText(stringResource(R.string.settings_title)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.settings_back_button))
                    }
                },
                modifier = Modifier.windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Top))
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { languageDropdownExpanded = true },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    HybridFontText(
                        stringResource(R.string.settings_change_language),
                        style = MaterialTheme.typography.bodyLarge,
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = stringResource(R.string.settings_change_language),
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                DropdownMenu(
                    expanded = languageDropdownExpanded,
                    onDismissRequest = { languageDropdownExpanded = false },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                ) {
                    HybridFontText(
                        stringResource(R.string.settings_language_english),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                localeManager.locale = Locale("en")
                                languageDropdownExpanded = false
                            }
                            .padding(16.dp),
                        overrideLocale = Locale("en")
                    )
                    HybridFontText(
                        stringResource(R.string.settings_language_chinese_simplified),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                localeManager.locale = Locale("zh", "CN")
                                languageDropdownExpanded = false
                            }
                            .padding(16.dp),
                        overrideLocale = Locale("zh", "CN")
                    )
                    HybridFontText(
                        stringResource(R.string.settings_language_chinese_traditional_hk),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                localeManager.locale = Locale("zh", "HK")
                                languageDropdownExpanded = false
                            }
                            .padding(16.dp),
                        overrideLocale = Locale("zh", "HK")
                    )
                    HybridFontText(
                        stringResource(R.string.settings_language_chinese_traditional_tw),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                localeManager.locale = Locale("zh", "TW")
                                languageDropdownExpanded = false
                            }
                            .padding(16.dp),
                        overrideLocale = Locale("zh", "TW")
                    )
                    HybridFontText(
                        stringResource(R.string.settings_language_japanese),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                localeManager.locale = Locale("ja")
                                languageDropdownExpanded = false
                            }
                            .padding(16.dp),
                        overrideLocale = Locale("ja")
                    )
                    HybridFontText(
                        stringResource(R.string.settings_language_korean),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                localeManager.locale = Locale("ko")
                                languageDropdownExpanded = false
                            }
                            .padding(16.dp),
                        overrideLocale = Locale("ko")
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                HybridFontText(stringResource(R.string.settings_enable_sound), style = MaterialTheme.typography.bodyLarge)
                Switch(
                    checked = soundEnabled,
                    onCheckedChange = {
                        soundEnabled = it
                        settingsManager.setSoundEnabled(it)
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            HybridFontText(stringResource(R.string.settings_volume, (soundVolume * 100).toInt()), style = MaterialTheme.typography.bodyLarge)
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
                HybridFontText(stringResource(R.string.settings_about_button), style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}