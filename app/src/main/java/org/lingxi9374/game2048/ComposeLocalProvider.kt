package org.lingxi9374.game2048

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import java.util.Locale

@Composable
fun ProvideLocale(content: @Composable () -> Unit) {
    val localeManager = remember { LocaleManager() }
    val context = LocalContext.current
    val config = Configuration(LocalConfiguration.current)
    config.setLocale(localeManager.locale)
    val localizedContext = context.createConfigurationContext(config)

    CompositionLocalProvider(
        LocalLocaleManager provides localeManager,
        LocalContext provides localizedContext,
        content = content
    )
}