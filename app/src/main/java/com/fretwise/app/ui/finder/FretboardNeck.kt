package com.fretwise.app.ui.finder

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.coerceIn
import androidx.compose.ui.unit.dp
import com.fretwise.app.FretwiseViewModel
import com.fretwise.app.music.Fingering
import com.fretwise.app.music.Notation
import com.fretwise.app.music.Tuning
import com.fretwise.app.ui.theme.FretwiseTheme

private val HeadHeight = 56.dp
private val FretLabelWidth = 20.dp
private val BoardPadding = 6.dp
private val MaxRowHeight = 72.dp
private val MinRowHeight = 48.dp
private val FretWireHeight = 3.dp
private val OpenNutHeight = 7.dp
private val InlayDiameter = 16.dp
private val DotDiameter = 40.dp

/** String gauges in dp, lowest string first. */
private val StringWidths = listOf(3.2.dp, 2.7.dp, 2.2.dp, 1.8.dp, 1.4.dp, 1.1.dp)
private val SingleInlayFrets = setOf(3, 5, 7, 9, 15, 17)
private const val DoubleInlayFret = 12

/**
 * The tappable neck: a row of string heads (tap to mute or play open) above a
 * window of [FretwiseViewModel.VISIBLE_FRETS] frets starting at [baseFret].
 * Rows shrink from 72dp on short screens so the whole window stays visible.
 */
@Composable
fun FretboardNeck(
    frets: List<Int>,
    baseFret: Int,
    tuning: Tuning,
    notation: Notation,
    leftHanded: Boolean,
    showNoteNames: Boolean,
    onSetFret: (string: Int, fret: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val strings = (0 until Fingering.STRING_COUNT).toList().let { if (leftHanded) it.reversed() else it }
    val nutHeight = if (baseFret == FretwiseViewModel.MIN_BASE_FRET) OpenNutHeight else FretWireHeight
    val rowCount = FretwiseViewModel.VISIBLE_FRETS

    BoxWithConstraints(modifier.semantics { contentDescription = "Guitar neck" }) {
        val rowHeight = ((maxHeight - HeadHeight - nutHeight) / rowCount).coerceIn(MinRowHeight, MaxRowHeight)
        Column {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(Modifier.width(FretLabelWidth))
                Row(Modifier.weight(1f).padding(horizontal = BoardPadding)) {
                    strings.forEach { string ->
                        StringHead(
                            fret = frets[string],
                            string = string,
                            openNote = notation.noteName(tuning.openStrings[string]),
                            onToggle = { onSetFret(string, if (frets[string] == Fingering.MUTED) 0 else Fingering.MUTED) },
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Column(Modifier.width(FretLabelWidth).padding(top = nutHeight)) {
                    repeat(rowCount) { row ->
                        Box(Modifier.height(rowHeight).fillMaxWidth(), contentAlignment = Alignment.Center) {
                            Text(
                                (baseFret + row).toString(),
                                style = MaterialTheme.typography.labelSmall,
                                color = FretwiseTheme.colors.textMuted,
                            )
                        }
                    }
                }
                Board(
                    frets = frets,
                    baseFret = baseFret,
                    strings = strings,
                    tuning = tuning,
                    notation = notation,
                    showNoteNames = showNoteNames,
                    rowHeight = rowHeight,
                    nutHeight = nutHeight,
                    onSetFret = onSetFret,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun StringHead(fret: Int, string: Int, openNote: String, onToggle: () -> Unit, modifier: Modifier) {
    val scheme = MaterialTheme.colorScheme
    val (mark, color, state) = when (fret) {
        Fingering.MUTED -> Triple("×", scheme.outline, "muted")
        0 -> Triple("O", scheme.onBackground, "open")
        else -> Triple(fret.toString(), scheme.primary, "fret $fret")
    }
    val action = if (fret == Fingering.MUTED) "play open" else "mute"
    Column(
        modifier
            .height(HeadHeight)
            .clickable(onClickLabel = action, role = Role.Button, onClick = onToggle)
            .clearAndSetSemantics {
                contentDescription = "String ${Fingering.STRING_COUNT - string}, $openNote, $state. Tap to $action"
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp, Alignment.CenterVertically),
    ) {
        Text(mark, style = MaterialTheme.typography.titleMedium, color = color)
        Text(openNote, style = MaterialTheme.typography.labelSmall, color = FretwiseTheme.colors.textMuted)
    }
}

@Composable
private fun Board(
    frets: List<Int>,
    baseFret: Int,
    strings: List<Int>,
    tuning: Tuning,
    notation: Notation,
    showNoteNames: Boolean,
    rowHeight: Dp,
    nutHeight: Dp,
    onSetFret: (string: Int, fret: Int) -> Unit,
    modifier: Modifier,
) {
    val colors = FretwiseTheme.colors
    val isOpenPosition = baseFret == FretwiseViewModel.MIN_BASE_FRET
    val rowCount = FretwiseViewModel.VISIBLE_FRETS

    Box(
        modifier
            .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp, bottomStart = 14.dp, bottomEnd = 14.dp))
            .background(colors.wood),
    ) {
        Canvas(Modifier.matchParentSize()) {
            val pad = BoardPadding.toPx()
            val row = rowHeight.toPx()
            val top = nutHeight.toPx()
            val cell = (size.width - 2 * pad) / strings.size

            drawRect(if (isOpenPosition) colors.nut else colors.fretWire, size = Size(size.width, top))
            for (i in 0 until rowCount) {
                val rowTop = top + i * row
                val centerY = rowTop + row / 2
                val fret = baseFret + i
                val inlayXs = when {
                    fret == DoubleInlayFret -> listOf(1f / 3, 2f / 3)
                    fret in SingleInlayFrets -> listOf(0.5f)
                    else -> emptyList()
                }
                inlayXs.forEach { fraction ->
                    drawCircle(
                        colors.inlay,
                        radius = InlayDiameter.toPx() / 2,
                        center = Offset(pad + fraction * (size.width - 2 * pad), centerY),
                    )
                }
                drawRect(
                    colors.fretWire,
                    topLeft = Offset(0f, rowTop + row - FretWireHeight.toPx()),
                    size = Size(size.width, FretWireHeight.toPx()),
                )
            }
            strings.forEachIndexed { column, string ->
                val width = StringWidths[string].toPx()
                drawRect(
                    colors.string,
                    topLeft = Offset(pad + (column + 0.5f) * cell - width / 2, top),
                    size = Size(width, size.height - top),
                )
            }
        }
        Column(Modifier.padding(top = nutHeight, start = BoardPadding, end = BoardPadding)) {
            repeat(rowCount) { i ->
                val fret = baseFret + i
                Row(Modifier.height(rowHeight)) {
                    strings.forEach { string ->
                        val note = notation.noteName(tuning.openStrings[string] + fret)
                        FretCell(
                            selected = frets[string] == fret,
                            label = "String ${Fingering.STRING_COUNT - string}, fret $fret, $note",
                            dotText = if (showNoteNames) note else "",
                            onToggle = { selected -> onSetFret(string, if (selected) fret else Fingering.MUTED) },
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FretCell(
    selected: Boolean,
    label: String,
    dotText: String,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier,
) {
    Box(
        modifier
            .fillMaxHeight()
            .toggleable(
                value = selected,
                interactionSource = null,
                indication = ripple(bounded = false, radius = DotDiameter / 2 + 4.dp),
                role = Role.Checkbox,
                onValueChange = onToggle,
            )
            .semantics { contentDescription = label },
        contentAlignment = Alignment.Center,
    ) {
        if (selected) {
            Box(
                Modifier
                    .size(DotDiameter)
                    .shadow(4.dp, CircleShape)
                    .background(MaterialTheme.colorScheme.primary, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    dotText,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary,
                )
            }
        }
    }
}
