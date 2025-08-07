package org.lingxi9374.game2048.ui

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import org.lingxi9374.game2048.R

// --- Single Font Families ---
// Each variable now holds a FontFamily with only one specific font.
// This gives us explicit control in the HybridFontText composable.

val englishFontFamily = FontFamily(Font(R.font.leigo_regular))
val chineseSimplifiedFontFamily = FontFamily(Font(R.font.han_yi_cu_yuan_jian))
val chineseTraditionalFontFamily = FontFamily(Font(R.font.gensen_rounded_bold))
val japaneseFontFamily = FontFamily(Font(R.font.tsuku_ard_gothic_std))
val koreanFontFamily = FontFamily(Font(R.font.mango_ddobak_bold))
