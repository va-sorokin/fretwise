package com.fretwise.app.music

/**
 * Fret per string, lowest string first. [MUTED] means the string is not played,
 * 0 means it is played open.
 */
data class Fingering(val frets: List<Int>) {
    init {
        require(frets.size == STRING_COUNT) { "Expected $STRING_COUNT strings, got ${frets.size}" }
    }

    fun withFret(string: Int, fret: Int): Fingering =
        Fingering(frets.toMutableList().also { it[string] = fret })

    /** Pitch classes of the sounding strings, ordered from the lowest pitch up. */
    fun soundingPitchClasses(tuning: Tuning): List<Int> =
        frets.mapIndexedNotNull { string, fret ->
            if (fret == MUTED) null else tuning.openStrings[string] + fret
        }.sorted().map { it % 12 }

    companion object {
        const val STRING_COUNT = 6
        const val MUTED = -1

        /** C major in open position, the shape shown on first launch. */
        val Default = Fingering(listOf(MUTED, 3, 2, 0, 1, 0))
        val Cleared = Fingering(List(STRING_COUNT) { MUTED })
    }
}
