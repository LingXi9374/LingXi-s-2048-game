package org.lingxi9374.game2048

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import java.util.Locale

class LocaleManager {
    var locale by mutableStateOf(Locale.getDefault())
}

val LocalLocaleManager = compositionLocalOf { LocaleManager() }