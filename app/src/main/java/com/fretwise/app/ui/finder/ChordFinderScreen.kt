package com.fretwise.app.ui.finder

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fretwise.app.FretwiseViewModel
import com.fretwise.app.data.FretwiseSettings
import com.fretwise.app.music.ChordIdentifier
import com.fretwise.app.music.Fingering
import com.fretwise.app.ui.FretwiseIcons
import com.fretwise.app.ui.theme.FretwiseTheme

@Composable
fun ChordFinderScreen(
    settings: FretwiseSettings,
    frets: List<Int>,
    baseFret: Int,
    onSetFret: (string: Int, fret: Int) -> Unit,
    onShiftWindow: (delta: Int) -> Unit,
    onClear: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val reading = remember(frets, settings.tuning, settings.notation) {
        ChordIdentifier.read(Fingering(frets).soundingPitchClasses(settings.tuning), settings.notation)
    }

    Column(
        modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing),
    ) {
        FinderTopBar(instrumentLabel = "Guitar · ${settings.tuning.label}", onOpenSettings = onOpenSettings)
        Column(
            Modifier
                .weight(1f)
                .padding(start = 16.dp, top = 4.dp, end = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            ChordReadout(reading = reading, notation = settings.notation)
            FretboardNeck(
                frets = frets,
                baseFret = baseFret,
                tuning = settings.tuning,
                notation = settings.notation,
                leftHanded = settings.leftHanded,
                showNoteNames = settings.noteNamesOnDots,
                onSetFret = onSetFret,
                modifier = Modifier.weight(1f),
            )
        }
        FinderFooter(
            baseFret = baseFret,
            onShiftWindow = onShiftWindow,
            onClear = onClear,
        )
    }
}

@Composable
private fun FinderTopBar(instrumentLabel: String, onOpenSettings: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .height(64.dp)
            .padding(start = 20.dp, end = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            "Fretwise",
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
        )
        OutlinedButton(
            onClick = onOpenSettings,
            modifier = Modifier
                .padding(horizontal = 4.dp)
                .height(32.dp),
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            contentPadding = PaddingValues(horizontal = 12.dp),
        ) {
            Text(
                instrumentLabel,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        IconButton(onClick = onOpenSettings, modifier = Modifier.size(48.dp)) {
            Icon(
                FretwiseIcons.Tune,
                contentDescription = "Settings",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun FinderFooter(baseFret: Int, onShiftWindow: (Int) -> Unit, onClear: () -> Unit) {
    val lastFret = baseFret + FretwiseViewModel.VISIBLE_FRETS - 1
    Row(
        Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 24.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Surface(shape = RoundedCornerShape(28.dp), color = MaterialTheme.colorScheme.surfaceContainer) {
            Row(
                Modifier.padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                StepperButton(
                    icon = FretwiseIcons.Minus,
                    contentDescription = "Move window toward the nut",
                    enabled = baseFret > FretwiseViewModel.MIN_BASE_FRET,
                    onClick = { onShiftWindow(-1) },
                )
                Text(
                    "Frets $baseFret–$lastFret",
                    modifier = Modifier.widthIn(min = 88.dp),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center,
                )
                StepperButton(
                    icon = FretwiseIcons.Plus,
                    contentDescription = "Move window toward the body",
                    enabled = baseFret < FretwiseViewModel.MAX_BASE_FRET,
                    onClick = { onShiftWindow(1) },
                )
            }
        }
        Spacer(Modifier.weight(1f))
        FilledTonalButton(
            onClick = onClear,
            modifier = Modifier.height(48.dp),
            contentPadding = PaddingValues(start = 16.dp, end = 20.dp),
        ) {
            Icon(FretwiseIcons.Reset, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.size(8.dp))
            Text("Clear", style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
private fun StepperButton(
    icon: ImageVector,
    contentDescription: String,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    val container = MaterialTheme.colorScheme.surfaceContainerHigh
    val content = MaterialTheme.colorScheme.onBackground
    FilledIconButton(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.size(44.dp),
        colors = IconButtonDefaults.filledIconButtonColors(
            containerColor = container,
            contentColor = content,
            disabledContainerColor = container.copy(alpha = DISABLED_ALPHA),
            disabledContentColor = content.copy(alpha = DISABLED_ALPHA),
        ),
    ) {
        Icon(icon, contentDescription = contentDescription, modifier = Modifier.size(20.dp))
    }
}

private const val DISABLED_ALPHA = 0.38f

@Preview(widthDp = 412, heightDp = 915)
@Composable
private fun ChordFinderScreenPreview() {
    FretwiseTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            ChordFinderScreen(
                settings = FretwiseSettings(onboarded = true),
                frets = Fingering.Default.frets,
                baseFret = 1,
                onSetFret = { _, _ -> },
                onShiftWindow = {},
                onClear = {},
                onOpenSettings = {},
            )
        }
    }
}
