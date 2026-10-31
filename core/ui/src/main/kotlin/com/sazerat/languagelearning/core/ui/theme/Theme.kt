package com.sazerat.languagelearning.core.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Palette of the design canvas: a cool light background, dark navy ink, one blue accent.
private val LightColors = lightColorScheme(
    primary = Color(0xFF2F66E8),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDCE7FF),
    onPrimaryContainer = Color(0xFF1F4CB8),
    secondary = Color(0xFF2BC48A),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFD6FAEA),
    onSecondaryContainer = Color(0xFF0E6B48),
    tertiary = Color(0xFFFF7A45),
    tertiaryContainer = Color(0xFFFFE7C2),
    onTertiaryContainer = Color(0xFF8A4B00),
    error = Color(0xFFA3123A),
    errorContainer = Color(0xFFFFE0E8),
    onErrorContainer = Color(0xFFA3123A),
    background = Color(0xFFEEF0FA),
    onBackground = Color(0xFF1B1F3B),
    surface = Color(0xFFF4F5FB),
    onSurface = Color(0xFF1B1F3B),
    surfaceVariant = Color(0xFFE3E6F4),
    onSurfaceVariant = Color(0xFF4A4F75),
    surfaceContainer = Color(0xFFF4F5FB),
    surfaceContainerHigh = Color(0xFFFFFFFF),
    outline = Color(0x401B1F3B),
    outlineVariant = Color(0x1F1B1F3B),
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF8AAEFF),
    onPrimary = Color(0xFF0B1A4A),
    primaryContainer = Color(0xFF233B7A),
    onPrimaryContainer = Color(0xFFDCE7FF),
    secondary = Color(0xFF5FE0B0),
    onSecondary = Color(0xFF00382A),
    secondaryContainer = Color(0xFF0E4D36),
    onSecondaryContainer = Color(0xFFD6FAEA),
    tertiary = Color(0xFFFF9B73),
    tertiaryContainer = Color(0xFF5C3300),
    onTertiaryContainer = Color(0xFFFFE7C2),
    error = Color(0xFFFFB3C4),
    errorContainer = Color(0xFF6E0C29),
    onErrorContainer = Color(0xFFFFE0E8),
    background = Color(0xFF12142B),
    onBackground = Color(0xFFEEF0FA),
    surface = Color(0xFF181B36),
    onSurface = Color(0xFFEEF0FA),
    surfaceVariant = Color(0xFF262A4D),
    onSurfaceVariant = Color(0xFFB7BCE0),
    surfaceContainer = Color(0xFF1D2040),
    surfaceContainerHigh = Color(0xFF23274A),
    outline = Color(0x40EEF0FA),
    outlineVariant = Color(0x1FEEF0FA),
)

/**
 * Colours that Material 3 has no slot for: the three word statuses (used everywhere a word is
 * shown, spec §3), the "glass" surfaces and the blurred blobs of the background.
 */
@Immutable
data class AppColors(
    /** Underline of a word never met. */
    val newWord: Color,
    /** Highlight behind a word being learnt. */
    val learningWord: Color,
    val onLearningWord: Color,
    /** Translucent card surface and its light border. */
    val glass: Color,
    val glassBorder: Color,
    val blobs: List<Color>,
)

private val LightAppColors = AppColors(
    newWord = Color(0xFF2F66E8),
    learningWord = Color(0xFFFFE48A),
    onLearningWord = Color(0xFF1B1F3B),
    glass = Color(0x8CFFFFFF),
    glassBorder = Color(0xBFFFFFFF),
    blobs = listOf(Color(0xFFFF8A5B), Color(0xFF6E9BFF), Color(0xFF5FE0B0)),
)

private val DarkAppColors = AppColors(
    newWord = Color(0xFF8AAEFF),
    learningWord = Color(0xFF6B5A12),
    onLearningWord = Color(0xFFFFF4C7),
    glass = Color(0x1FFFFFFF),
    glassBorder = Color(0x26FFFFFF),
    blobs = listOf(Color(0xFF9C3D1C), Color(0xFF2B4FA8), Color(0xFF1D7A5A)),
)

private val LocalAppColors = staticCompositionLocalOf { LightAppColors }

/** Extra colours of the current theme, next to `MaterialTheme.colorScheme`. */
val MaterialTheme.appColors: AppColors
    @Composable
    @ReadOnlyComposable
    get() = LocalAppColors.current

/**
 * Serif face for the texts being read (the design uses Literata): reading is the heart of the
 * app, and a serif face sets it apart from the interface. The system serif keeps the APK free of
 * font files; a bundled font can replace it here only.
 */
val ReadingFontFamily: FontFamily = FontFamily.Serif

// The design uses heavy, rounded type for the interface: same sizes as Material, bolder weights.
private val AppTypography = Typography().run {
    copy(
        displaySmall = displaySmall.copy(fontWeight = FontWeight.Black),
        headlineLarge = headlineLarge.copy(fontWeight = FontWeight.Black),
        headlineMedium = headlineMedium.copy(fontWeight = FontWeight.Black),
        headlineSmall = headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
        titleLarge = titleLarge.copy(fontWeight = FontWeight.ExtraBold),
        titleMedium = titleMedium.copy(fontWeight = FontWeight.ExtraBold),
        titleSmall = titleSmall.copy(fontWeight = FontWeight.Bold),
        bodyLarge = bodyLarge.copy(fontWeight = FontWeight.SemiBold),
        bodyMedium = bodyMedium.copy(fontWeight = FontWeight.SemiBold),
        bodySmall = bodySmall.copy(fontWeight = FontWeight.SemiBold),
        labelLarge = labelLarge.copy(fontWeight = FontWeight.ExtraBold),
        labelMedium = labelMedium.copy(fontWeight = FontWeight.ExtraBold),
        labelSmall = labelSmall.copy(fontWeight = FontWeight.ExtraBold, letterSpacing = 0.8.sp),
    )
}

/** Text of a story in the Reader: at least 18 sp with generous line spacing (spec §3). */
val ReadingTextStyle: TextStyle = TextStyle(fontFamily = ReadingFontFamily, fontSize = 20.sp, lineHeight = 35.sp)

private val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(18.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp),
)

/**
 * App theme, with the fixed palette of the design. No dynamic colour (Material You): the
 * word-status colours and the coloured background are part of the design.
 */
@Composable
fun LanguageLearningTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(LocalAppColors provides if (darkTheme) DarkAppColors else LightAppColors) {
        MaterialTheme(
            colorScheme = if (darkTheme) DarkColors else LightColors,
            typography = AppTypography,
            shapes = AppShapes,
            content = content,
        )
    }
}
