package com.fretwise.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable

/** The design is dark-only, so the scheme does not follow the system theme. */
@Composable
fun FretwiseTheme(content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalFretwiseColors provides FretwiseExtraColors()) {
        MaterialTheme(
            colorScheme = FretwiseColorScheme,
            typography = FretwiseTypography,
            content = content,
        )
    }
}

object FretwiseTheme {
    val colors: FretwiseExtraColors
        @Composable @ReadOnlyComposable get() = LocalFretwiseColors.current
}
