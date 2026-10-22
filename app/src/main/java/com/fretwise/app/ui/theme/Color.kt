package com.fretwise.app.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// Warm amber Material 3 dark scheme from the Fretwise design.
internal val FretwiseColorScheme = darkColorScheme(
    primary = Color(0xFFF6BC70),
    onPrimary = Color(0xFF462A00),
    primaryContainer = Color(0xFF643F00),
    onPrimaryContainer = Color(0xFFFFDDB6),
    secondaryContainer = Color(0xFF574432),
    onSecondaryContainer = Color(0xFFFBDEC5),
    background = Color(0xFF17130E),
    onBackground = Color(0xFFECE1D7),
    surface = Color(0xFF17130E),
    onSurface = Color(0xFFECE1D7),
    onSurfaceVariant = Color(0xFFD3C4B4),
    surfaceContainer = Color(0xFF241F1A),
    surfaceContainerHigh = Color(0xFF2E2924),
    surfaceContainerHighest = Color(0xFF3A332C),
    outline = Color(0xFF9C8E80),
    outlineVariant = Color(0xFF4F4539),
)

/** Colors outside the Material scheme: secondary text and the drawn fretboard. */
@Immutable
data class FretwiseExtraColors(
    val textMuted: Color = Color(0xFFB3A595),
    val divider: Color = Color(0xFF2E2924),
    val wood: Color = Color(0xFF2C1E15),
    val fretWire: Color = Color(0xFF8A7F73),
    val fretWireDim: Color = Color(0xFF6E655C),
    val nut: Color = Color(0xFFE6DAC6),
    val string: Color = Color(0xFFCDBFAF),
    val inlay: Color = Color(0xFF5C4838),
)

internal val LocalFretwiseColors = staticCompositionLocalOf { FretwiseExtraColors() }
