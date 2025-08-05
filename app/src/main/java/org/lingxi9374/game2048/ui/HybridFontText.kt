package org.lingxi9374.game2048.ui

import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.TextUnit

/**
 * A custom Text composable that displays text with a hybrid font strategy.
 * It uses the English font for Latin characters, numbers, and symbols,
 * and falls back to the Chinese font for Chinese characters.
 */
@Composable
fun HybridFontText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current,
    color: Color = style.color,
    fontSize: TextUnit = style.fontSize,
    fontWeight: FontWeight? = style.fontWeight,
) {
    val annotatedString = buildAnnotatedString {
        for (char in text) {
            val isChinese = char.toString().matches(Regex("[\u4e00-\u9fa5]"))
            withStyle(style = SpanStyle(
                fontFamily = if (isChinese) chineseFontFamily else englishFontFamily,
                color = color,
                fontSize = fontSize,
                fontWeight = fontWeight
            )) {
                append(char)
            }
        }
    }
    Text(text = annotatedString, modifier = modifier)
}
