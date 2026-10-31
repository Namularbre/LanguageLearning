package com.sazerat.languagelearning.core.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.sazerat.languagelearning.core.ui.theme.appColors

/**
 * Background of every screen: the theme colour with three large blurred colour blobs, as in the
 * design. The blobs are drawn once and blurred by the GPU (RenderEffect, available since API 31).
 */
@Composable
fun AppBackground(modifier: Modifier = Modifier, content: @Composable BoxScope.() -> Unit) {
    val blobs = MaterialTheme.appColors.blobs
    Box(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Canvas(modifier = Modifier.fillMaxSize().blur(80.dp)) {
            val radius = size.width * 0.38f
            drawCircle(blobs[0].copy(alpha = 0.45f), radius, Offset(0f, size.height * 0.12f))
            drawCircle(blobs[1].copy(alpha = 0.5f), radius, Offset(size.width, size.height * 0.45f))
            drawCircle(blobs[2].copy(alpha = 0.4f), radius * 0.85f, Offset(size.width * 0.1f, size.height * 0.9f))
        }
        content()
    }
}

/**
 * Translucent card with a light border, the "glass" of the design. Real background blur would need
 * an extra library, so the surface is only translucent over the blurred background.
 */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.extraLarge,
    onClick: (() -> Unit)? = null,
    contentPadding: PaddingValues = PaddingValues(20.dp),
    content: @Composable ColumnScope.() -> Unit,
) {
    val colors = MaterialTheme.appColors
    val border = BorderStroke(1.dp, colors.glassBorder)
    val inner: @Composable () -> Unit = { Column(Modifier.padding(contentPadding), content = content) }
    if (onClick != null) {
        Surface(onClick = onClick, modifier = modifier, shape = shape, color = colors.glass, border = border, content = inner)
    } else {
        Surface(modifier = modifier, shape = shape, color = colors.glass, border = border, content = inner)
    }
}

/** Main action of a screen: full width, tall, at the bottom where the thumb is (spec §3). */
@Composable
fun PrimaryButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.fillMaxWidth().heightIn(min = 56.dp),
        shape = MaterialTheme.shapes.medium,
        colors = ButtonDefaults.buttonColors(),
    ) {
        Text(text, style = MaterialTheme.typography.titleMedium)
    }
}

/** Small uppercase label above a card title, e.g. "TO REVIEW". */
@Composable
fun Overline(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onSurfaceVariant,
) {
    Text(text.uppercase(), modifier = modifier, style = MaterialTheme.typography.labelSmall, color = color)
}
