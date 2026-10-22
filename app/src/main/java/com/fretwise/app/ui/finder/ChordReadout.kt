package com.fretwise.app.ui.finder

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fretwise.app.music.ChordReading
import com.fretwise.app.music.Notation
import com.fretwise.app.music.degreeName
import com.fretwise.app.ui.theme.FretwiseTheme

/** Chord names longer than this drop to the smaller display size so they fit one line. */
private const val LONG_NAME_LENGTH = 7

/** The card above the neck: chord name, its quality, and the notes being played. */
@Composable
fun ChordReadout(reading: ChordReading, notation: Notation, modifier: Modifier = Modifier) {
    val scheme = MaterialTheme.colorScheme
    val muted = FretwiseTheme.colors.textMuted
    val typography = MaterialTheme.typography

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .semantics { liveRegion = LiveRegionMode.Polite },
        shape = RoundedCornerShape(28.dp),
        color = scheme.surfaceContainer,
    ) {
        Column(
            Modifier
                .heightIn(min = 172.dp)
                .padding(horizontal = 24.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            when (reading) {
                is ChordReading.Chord -> {
                    val name = reading.best.name(notation)
                    Text(
                        name,
                        style = if (name.length > LONG_NAME_LENGTH) typography.displayLarge.copy(fontSize = 40.sp)
                        else typography.displayLarge,
                        color = scheme.onPrimaryContainer,
                    )
                    Text(reading.best.description(notation), style = typography.bodyLarge, color = scheme.onSurfaceVariant)
                }
                is ChordReading.NoChord -> {
                    Text("No chord", style = typography.displayMedium, color = muted)
                    Text(
                        "These notes don’t form a chord we recognize",
                        style = typography.bodyLarge,
                        color = scheme.onSurfaceVariant,
                    )
                }
                ChordReading.Empty -> {
                    Text("—", style = typography.displayMedium, color = muted)
                    Text("Tap the neck to place notes", style = typography.bodyLarge, color = scheme.onSurfaceVariant)
                }
            }

            NoteChips(reading, notation, Modifier.padding(top = 10.dp))

            if (reading is ChordReading.Chord && reading.alternates.isNotEmpty()) {
                Text(
                    buildAnnotatedString {
                        append("Also reads as ")
                        withStyle(SpanStyle(color = scheme.onBackground, fontWeight = FontWeight.SemiBold)) {
                            append(reading.alternates.joinToString(" · ") { it.name(notation) })
                        }
                    },
                    style = typography.bodyMedium,
                    color = muted,
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun NoteChips(reading: ChordReading, notation: Notation, modifier: Modifier = Modifier) {
    val (pitchClasses, root) = when (reading) {
        is ChordReading.Chord -> reading.pitchClasses to reading.best.root
        is ChordReading.NoChord -> reading.pitchClasses to null
        ChordReading.Empty -> return
    }
    val chipBorder = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    FlowRow(
        modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        pitchClasses.forEach { pitchClass ->
            Row(
                Modifier
                    .height(32.dp)
                    .border(chipBorder, RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    notation.noteName(pitchClass),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                if (root != null) {
                    Text(
                        degreeName(pitchClass - root),
                        style = MaterialTheme.typography.bodySmall,
                        color = FretwiseTheme.colors.textMuted,
                    )
                }
            }
        }
    }
}
