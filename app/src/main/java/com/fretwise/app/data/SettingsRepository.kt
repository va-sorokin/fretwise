package com.fretwise.app.data

import android.content.Context
import androidx.core.content.edit
import com.fretwise.app.music.Notation
import com.fretwise.app.music.Tuning
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class FretwiseSettings(
    val tuning: Tuning = Tuning.Standard,
    val notation: Notation = Notation.Sharps,
    val leftHanded: Boolean = false,
    val noteNamesOnDots: Boolean = true,
    val onboarded: Boolean = false,
)

/** Small, synchronous store: the start screen depends on [FretwiseSettings.onboarded] at launch. */
class SettingsRepository(context: Context) {
    private val prefs = context.getSharedPreferences("fretwise_settings", Context.MODE_PRIVATE)
    private val state = MutableStateFlow(load())

    val settings: StateFlow<FretwiseSettings> = state.asStateFlow()

    fun update(transform: (FretwiseSettings) -> FretwiseSettings) {
        state.update(transform)
        save(state.value)
    }

    private fun load(): FretwiseSettings {
        val defaults = FretwiseSettings()
        return FretwiseSettings(
            tuning = enumOrDefault(prefs.getString(KEY_TUNING, null), defaults.tuning),
            notation = enumOrDefault(prefs.getString(KEY_NOTATION, null), defaults.notation),
            leftHanded = prefs.getBoolean(KEY_LEFT_HANDED, defaults.leftHanded),
            noteNamesOnDots = prefs.getBoolean(KEY_NOTE_NAMES, defaults.noteNamesOnDots),
            onboarded = prefs.getBoolean(KEY_ONBOARDED, defaults.onboarded),
        )
    }

    private fun save(settings: FretwiseSettings) = prefs.edit {
        putString(KEY_TUNING, settings.tuning.name)
        putString(KEY_NOTATION, settings.notation.name)
        putBoolean(KEY_LEFT_HANDED, settings.leftHanded)
        putBoolean(KEY_NOTE_NAMES, settings.noteNamesOnDots)
        putBoolean(KEY_ONBOARDED, settings.onboarded)
    }

    private inline fun <reified E : Enum<E>> enumOrDefault(name: String?, default: E): E =
        enumValues<E>().firstOrNull { it.name == name } ?: default

    private companion object {
        const val KEY_TUNING = "tuning"
        const val KEY_NOTATION = "notation"
        const val KEY_LEFT_HANDED = "left_handed"
        const val KEY_NOTE_NAMES = "note_names_on_dots"
        const val KEY_ONBOARDED = "onboarded"
    }
}
