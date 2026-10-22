package com.fretwise.app

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import com.fretwise.app.data.FretwiseSettings
import com.fretwise.app.data.SettingsRepository
import com.fretwise.app.music.Fingering
import kotlinx.coroutines.flow.StateFlow

class FretwiseViewModel(
    application: Application,
    private val savedState: SavedStateHandle,
) : AndroidViewModel(application) {
    private val repository = SettingsRepository(application)

    val settings: StateFlow<FretwiseSettings> = repository.settings

    /** The fingering and fret window survive process death, but are not kept across launches. */
    val frets: StateFlow<List<Int>> = savedState.getStateFlow(KEY_FRETS, Fingering.Default.frets)
    val baseFret: StateFlow<Int> = savedState.getStateFlow(KEY_BASE_FRET, MIN_BASE_FRET)

    fun updateSettings(transform: (FretwiseSettings) -> FretwiseSettings) = repository.update(transform)

    fun setFret(string: Int, fret: Int) {
        savedState[KEY_FRETS] = Fingering(frets.value).withFret(string, fret).frets
    }

    fun clearFrets() {
        savedState[KEY_FRETS] = Fingering.Cleared.frets
    }

    fun shiftWindow(delta: Int) {
        savedState[KEY_BASE_FRET] = (baseFret.value + delta).coerceIn(MIN_BASE_FRET, MAX_BASE_FRET)
    }

    companion object {
        const val MIN_BASE_FRET = 1
        const val MAX_BASE_FRET = 12
        const val VISIBLE_FRETS = 6

        private const val KEY_FRETS = "frets"
        private const val KEY_BASE_FRET = "base_fret"
    }
}
