package com.fretwise.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.fretwise.app.R

val Figtree = FontFamily(
    Font(R.font.figtree_400, FontWeight.Normal),
    Font(R.font.figtree_500, FontWeight.Medium),
    Font(R.font.figtree_600, FontWeight.SemiBold),
    Font(R.font.figtree_700, FontWeight.Bold),
)

/** Display face for chord names, fret marks and headlines. */
val SpaceGrotesk = FontFamily(
    Font(R.font.space_grotesk_500, FontWeight.Medium),
    Font(R.font.space_grotesk_600, FontWeight.SemiBold),
    Font(R.font.space_grotesk_700, FontWeight.Bold),
)

private val Body = TextStyle(fontFamily = Figtree)
private val Display = TextStyle(fontFamily = SpaceGrotesk, fontWeight = FontWeight.SemiBold, lineHeight = 1.05.em)

internal val FretwiseTypography = Typography(
    displayLarge = Display.copy(fontSize = 56.sp),
    displayMedium = Display.copy(fontSize = 44.sp),
    headlineMedium = Display.copy(fontSize = 34.sp, lineHeight = 1.1.em, letterSpacing = (-0.5).sp),
    titleLarge = Body.copy(fontSize = 22.sp, fontWeight = FontWeight.SemiBold, letterSpacing = (-0.2).sp),
    titleMedium = Display.copy(fontSize = 18.sp),
    bodyLarge = Body.copy(fontSize = 16.sp, lineHeight = 24.sp),
    bodyMedium = Body.copy(fontSize = 14.sp, lineHeight = 20.sp),
    bodySmall = Body.copy(fontSize = 13.sp, lineHeight = 18.sp),
    labelLarge = Body.copy(fontSize = 14.sp, fontWeight = FontWeight.SemiBold),
    labelMedium = Body.copy(fontSize = 12.sp, fontWeight = FontWeight.SemiBold),
    labelSmall = Body.copy(fontSize = 12.sp),
)
