package com.fretwise.app.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fretwise.app.data.FretwiseSettings
import com.fretwise.app.music.Notation
import com.fretwise.app.music.Tuning
import com.fretwise.app.ui.FretwiseIcons
import com.fretwise.app.ui.theme.FretwiseTheme

@Composable
fun SettingsScreen(
    settings: FretwiseSettings,
    onUpdate: ((FretwiseSettings) -> FretwiseSettings) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing),
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(start = 4.dp, end = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onBack, modifier = Modifier.size(48.dp)) {
                Icon(FretwiseIcons.Back, contentDescription = "Back", tint = MaterialTheme.colorScheme.onBackground)
            }
            Text("Settings", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onBackground)
        }

        Column(Modifier.verticalScroll(rememberScrollState())) {
            SettingsSection("Instrument", divided = false, selectable = true) {
                OptionRow(name = "Guitar", detail = "6 strings", selected = true, onClick = {})
                OptionRow(name = "Ukulele", detail = "4 strings · G C E A", selected = false, enabled = false, onClick = {}) {
                    SoonBadge()
                }
            }
            SettingsSection("Tuning", selectable = true) {
                Tuning.entries.forEach { tuning ->
                    OptionRow(
                        name = tuning.label,
                        detail = tuning.openNotesLabel,
                        detailLetterSpacing = true,
                        selected = settings.tuning == tuning,
                        onClick = { onUpdate { it.copy(tuning = tuning) } },
                    )
                }
            }
            SettingsSection("Display") {
                NotationRow(settings.notation, onSelect = { notation -> onUpdate { it.copy(notation = notation) } })
                SwitchRow(
                    name = "Left-handed neck",
                    detail = "Mirror the strings, low E on the right",
                    checked = settings.leftHanded,
                    onCheckedChange = { checked -> onUpdate { it.copy(leftHanded = checked) } },
                )
                SwitchRow(
                    name = "Note names on dots",
                    detail = "Show the note inside each fretted dot",
                    checked = settings.noteNamesOnDots,
                    onCheckedChange = { checked -> onUpdate { it.copy(noteNamesOnDots = checked) } },
                )
            }
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    divided: Boolean = true,
    selectable: Boolean = false,
    content: @Composable ColumnScope.() -> Unit,
) {
    if (divided) {
        HorizontalDivider(Modifier.padding(top = 8.dp), color = FretwiseTheme.colors.divider)
    }
    Column(
        Modifier
            .fillMaxWidth()
            .then(if (selectable) Modifier.selectableGroup() else Modifier),
    ) {
        Text(
            title,
            modifier = Modifier.padding(start = 24.dp, top = 16.dp, end = 24.dp, bottom = 8.dp),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
        )
        content()
    }
}

@Composable
private fun OptionRow(
    name: String,
    detail: String,
    selected: Boolean,
    onClick: () -> Unit,
    enabled: Boolean = true,
    detailLetterSpacing: Boolean = false,
    trailing: @Composable () -> Unit = {},
) {
    Row(
        Modifier
            .fillMaxWidth()
            .height(64.dp)
            .selectable(selected = selected, enabled = enabled, role = Role.RadioButton, onClick = onClick)
            .alpha(if (enabled) 1f else DISABLED_ROW_ALPHA)
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(
            selected = selected,
            onClick = null,
            enabled = enabled,
            colors = RadioButtonDefaults.colors(
                unselectedColor = MaterialTheme.colorScheme.outline,
                disabledUnselectedColor = MaterialTheme.colorScheme.outline,
            ),
        )
        TwoLineLabel(
            name,
            detail,
            Modifier.weight(1f),
            detailLetterSpacing = detailLetterSpacing,
        )
        trailing()
    }
}

@Composable
private fun TwoLineLabel(name: String, detail: String, modifier: Modifier = Modifier, detailLetterSpacing: Boolean = false) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(name, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onBackground)
        Text(
            detail,
            style = MaterialTheme.typography.bodyMedium,
            color = FretwiseTheme.colors.textMuted,
            letterSpacing = if (detailLetterSpacing) 1.5.sp else MaterialTheme.typography.bodyMedium.letterSpacing,
        )
    }
}

@Composable
private fun SoonBadge() {
    Box(
        Modifier
            .height(24.dp)
            .background(MaterialTheme.colorScheme.surfaceContainerHighest, RoundedCornerShape(12.dp))
            .padding(horizontal = 10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text("Soon", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun NotationRow(notation: Notation, onSelect: (Notation) -> Unit) {
    val options = listOf(Notation.Sharps to "♯ Sharps", Notation.Flats to "♭ Flats")
    Row(
        Modifier
            .fillMaxWidth()
            .heightIn(min = 72.dp)
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            "Note names",
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground,
        )
        SingleChoiceSegmentedButtonRow(Modifier.semantics { contentDescription = "Note names" }) {
            options.forEachIndexed { index, (option, label) ->
                SegmentedButton(
                    selected = notation == option,
                    onClick = { onSelect(option) },
                    shape = SegmentedButtonDefaults.itemShape(index, options.size),
                    modifier = Modifier.height(44.dp),
                    colors = SegmentedButtonDefaults.colors(
                        activeBorderColor = MaterialTheme.colorScheme.outline,
                        inactiveBorderColor = MaterialTheme.colorScheme.outline,
                        inactiveContainerColor = MaterialTheme.colorScheme.background,
                        inactiveContentColor = MaterialTheme.colorScheme.onBackground,
                    ),
                    // The design shows the selection by fill alone, without a checkmark.
                    icon = {},
                ) {
                    Text(label, style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }
}

@Composable
private fun SwitchRow(name: String, detail: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .heightIn(min = 72.dp)
            .toggleable(value = checked, role = Role.Switch, onValueChange = onCheckedChange)
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TwoLineLabel(name, detail, Modifier.weight(1f))
        Switch(
            checked = checked,
            onCheckedChange = null,
            colors = SwitchDefaults.colors(uncheckedTrackColor = MaterialTheme.colorScheme.surfaceContainer),
        )
    }
}

private const val DISABLED_ROW_ALPHA = 0.6f

@Preview(widthDp = 412, heightDp = 915)
@Composable
private fun SettingsScreenPreview() {
    FretwiseTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            SettingsScreen(settings = FretwiseSettings(), onUpdate = {}, onBack = {})
        }
    }
}
