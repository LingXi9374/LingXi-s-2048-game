package org.lingxi9374.game2048.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.TextUnit
import java.util.Locale

/**
 * A custom Text composable that displays text with a hybrid font strategy.
 * It uses the English font for Latin characters, numbers, and symbols,
 * and falls back to the Chinese or Japanese font for their respective characters.
 * The font for CJK Unified Ideographs (Kanji/Hanzi) is chosen based on the current app language.
 */
@RequiresApi(Build.VERSION_CODES.N)
@Composable
fun HybridFontText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current,
    color: Color = style.color,
    fontSize: TextUnit = style.fontSize,
    fontWeight: FontWeight? = style.fontWeight,
    overrideLocale: Locale? = null
) {
    val effectiveLocale = overrideLocale ?: LocalContext.current.resources.configuration.locales[0]
    val language = effectiveLocale.language
    val country = effectiveLocale.country

    val annotatedString = buildAnnotatedString {
        for (char in text) {
            val isKana = char.toString().matches(Regex("[\u3040-\u30ff]"))
            val isCjkSymbol = char.toString().matches(Regex("[\u1100-\ud7ff]"))
            val isHanzi = char.toString().matches(Regex("[\u4e00-\u9fa5]"))

            val fontFamily = when {
                isKana -> japaneseFontFamily
                isHanzi || isCjkSymbol -> {
                    when (language) {
                        "zh" -> {
                            when (country) {
                                "HK", "TW" -> chineseTraditionalFontFamily
                                else -> chineseSimplifiedFontFamily
                            }
                        }
                        "ja" -> japaneseFontFamily
                        "ko" -> koreanFontFamily
                        else -> englishFontFamily
                    }
                }
                else -> englishFontFamily
            }

            withStyle(
                style = SpanStyle(
                    fontFamily = fontFamily,
                    color = color,
                    fontSize = fontSize,
                    fontWeight = fontWeight
                )
            ) {
                append(char)
            }
        }
    }
    Text(text = annotatedString, modifier = modifier)
}