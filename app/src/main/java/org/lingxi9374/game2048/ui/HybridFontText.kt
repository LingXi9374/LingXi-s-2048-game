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
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.TextUnit
import org.lingxi9374.game2048.R

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
) {
    val language = LocalContext.current.resources.configuration.locales[0].language

    val annotatedString = buildAnnotatedString {
        for (char in text) {
            // Unicode ranges for Japanese (Hiragana, Katakana) and Chinese/Japanese (CJK Unified Ideographs)
            val isKana = char.toString().matches(Regex("[\u3040-\u30ff]"))
            val isKanjiOrHanzi = char.toString().matches(Regex("[\u4e00-\u9fa5]"))

            val fontFamily = when {
                // If the character is Japanese Kana, always use the Japanese font.
                isKana -> japaneseFontFamily
                // If the character is a CJK ideograph, decide the font based on the current app language.
                isKanjiOrHanzi -> {
                    if (language == "ja") {
                        // For Japanese, create a fallback chain: Japanese font first, then Chinese font.
                        FontFamily(
                            Font(R.font.tsuku_ard_gothic_std),
                            Font(R.font.han_yi_cu_yuan_jian)
                        )
                    } else {
                        // Defaults to Chinese font for CJK characters if language is not Japanese.
                        // This maintains the original behavior for Chinese.
                        chineseFontFamily
                    }
                }
                // For all other characters (Latin, numbers, symbols), use the English font.
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