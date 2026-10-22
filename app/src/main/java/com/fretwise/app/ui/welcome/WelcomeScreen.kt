package com.fretwise.app.ui.welcome

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fretwise.app.ui.theme.FretwiseTheme

@Composable
fun WelcomeScreen(onGetStarted: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(start = 24.dp, top = 24.dp, end = 24.dp, bottom = 32.dp),
    ) {
        Column(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(28.dp),
        ) {
            HeroCard()
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    "Fret it. We’ll name it.",
                    style = MaterialTheme.typography.headlineMedium.copy(lineBreak = LineBreak.Heading),
                    color = MaterialTheme.colorScheme.onBackground,
                )
                Text(
                    "Place your fingers on a virtual neck and see the chord name instantly — " +
                        "or find out it isn’t a chord at all.",
                    style = MaterialTheme.typography.bodyLarge.copy(lineBreak = LineBreak.Paragraph),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            InstrumentPicker()
        }
        Button(
            onClick = onGetStarted,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
                .height(56.dp),
            shape = RoundedCornerShape(28.dp),
        ) {
            Text("Get started", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun HeroCard() {
    Box(
        Modifier
            .fillMaxWidth()
            .height(380.dp)
            .background(MaterialTheme.colorScheme.surfaceContainer, RoundedCornerShape(32.dp))
            .clearAndSetSemantics {},
        contentAlignment = Alignment.Center,
    ) {
        GShapeIllustration(Modifier.padding(top = 24.dp))
        Box(
            Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 24.dp, bottom = 20.dp)
                .size(72.dp)
                .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(24.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                "G",
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }
    }
}

/** A G major shape on a stylized neck: six strings, four frets, three fretted dots. */
@Composable
private fun GShapeIllustration(modifier: Modifier = Modifier) {
    val colors = FretwiseTheme.colors
    val dotColor = MaterialTheme.colorScheme.primary
    val stringSpacing = 40.dp
    val fretSpacing = 60.dp
    val stringWidths = listOf(3.dp, 3.dp, 2.dp, 2.dp, 1.5.dp, 1.5.dp)
    // (string, fret) of each fretted note: low E fret 3, A fret 2, high E fret 3.
    val dots = listOf(0 to 3, 1 to 2, 5 to 3)
    val openStrings = listOf(2, 3, 4)

    Box(modifier.size(width = 200.dp, height = 240.dp)) {
        Canvas(Modifier.matchParentSize()) {
            val overhang = 8.dp.toPx()
            drawRect(
                colors.nut,
                topLeft = Offset(-overhang, 0f),
                size = Size(size.width + 2 * overhang, 8.dp.toPx()),
            )
            for (fret in 1..4) {
                drawRect(
                    colors.fretWireDim,
                    topLeft = Offset(-overhang, fret * fretSpacing.toPx()),
                    size = Size(size.width + 2 * overhang, 3.dp.toPx()),
                )
            }
            stringWidths.forEachIndexed { string, width ->
                drawRect(
                    colors.string,
                    topLeft = Offset(string * stringSpacing.toPx(), 0f),
                    size = Size(width.toPx(), size.height + overhang),
                )
            }
        }
        openStrings.forEach { string ->
            Text(
                "O",
                modifier = Modifier
                    .offset(x = string * stringSpacing - 12.dp, y = (-36).dp)
                    .width(24.dp),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
            )
        }
        dots.forEach { (string, fret) ->
            Box(
                Modifier
                    .offset(
                        x = string * stringSpacing + 1.dp - 18.dp,
                        y = fret * fretSpacing - fretSpacing / 2 - 18.dp,
                    )
                    .size(36.dp)
                    .shadow(4.dp, CircleShape)
                    .background(dotColor, CircleShape),
            )
        }
    }
}

@Composable
private fun InstrumentPicker() {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            "Your instrument",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Row(
            Modifier
                .selectableGroup()
                .semantics { contentDescription = "Your instrument" },
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            InstrumentCard("Guitar", "6 strings", selected = true, enabled = true, Modifier.weight(1f))
            InstrumentCard("Ukulele", "Coming soon", selected = false, enabled = false, Modifier.weight(1f))
        }
    }
}

@Composable
private fun InstrumentCard(
    name: String,
    detail: String,
    selected: Boolean,
    enabled: Boolean,
    modifier: Modifier = Modifier,
) {
    val scheme = MaterialTheme.colorScheme
    Surface(
        modifier = modifier
            .height(76.dp)
            .clip(RoundedCornerShape(16.dp))
            // Only one instrument exists today, so selecting is a no-op.
            .selectable(selected = selected, enabled = enabled, role = Role.RadioButton, onClick = {}),
        shape = RoundedCornerShape(16.dp),
        color = if (selected) scheme.surfaceContainerHigh else Color.Transparent,
        contentColor = if (enabled) scheme.onBackground else scheme.outline,
        border = if (selected) BorderStroke(2.dp, scheme.primary) else BorderStroke(1.dp, scheme.outlineVariant),
    ) {
        Column(
            Modifier.fillMaxSize().padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp, Alignment.CenterVertically),
        ) {
            Text(name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
            Text(
                detail,
                style = MaterialTheme.typography.bodySmall,
                color = if (enabled) scheme.onSurfaceVariant else scheme.outline,
            )
        }
    }
}

@Preview(widthDp = 412, heightDp = 915)
@Composable
private fun WelcomeScreenPreview() {
    FretwiseTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            WelcomeScreen(onGetStarted = {})
        }
    }
}
