package com.sazerat.languagelearning.core.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LightColors = lightColorScheme(
    primary = Color(0xFF2F5D8A),
    secondary = Color(0xFF8A5A2F),
    tertiary = Color(0xFF5A6F2F),
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF9CC4F0),
    secondary = Color(0xFFF0C49C),
    tertiary = Color(0xFFC4D99C),
)

/**
 * App theme. Dynamic colour (Material You) is always available since minSdk is 34,
 * so no version check is needed; [dynamicColor] = false keeps the fixed palette (previews, tests).
 */
@Composable
fun LanguageLearningTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && darkTheme -> dynamicDarkColorScheme(LocalContext.current)
        dynamicColor -> dynamicLightColorScheme(LocalContext.current)
        darkTheme -> DarkColors
        else -> LightColors
    }
    MaterialTheme(colorScheme = colorScheme, content = content)
}
