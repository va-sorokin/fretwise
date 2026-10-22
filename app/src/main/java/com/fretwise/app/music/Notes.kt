package com.fretwise.app.music

/** How accidentals are spelled in note and chord names. */
enum class Notation {
    Sharps,
    Flats;

    fun noteName(pitchClass: Int): String = when (this) {
        Sharps -> SHARP_NAMES
        Flats -> FLAT_NAMES
    }[Math.floorMod(pitchClass, 12)]

    private companion object {
        val SHARP_NAMES = listOf("C", "C♯", "D", "D♯", "E", "F", "F♯", "G", "G♯", "A", "A♯", "B")
        val FLAT_NAMES = listOf("C", "D♭", "D", "E♭", "E", "F", "G♭", "G", "A♭", "A", "B♭", "B")
    }
}

/** Open-string pitches as MIDI note numbers, lowest string first. */
enum class Tuning(val label: String, val openNotesLabel: String, val openStrings: List<Int>) {
    Standard("Standard", "E A D G B E", listOf(40, 45, 50, 55, 59, 64)),
    DropD("Drop D", "D A D G B E", listOf(38, 45, 50, 55, 59, 64)),
    OpenG("Open G", "D G D G B D", listOf(38, 43, 50, 55, 59, 62)),
    Dadgad("DADGAD", "D A D G A D", listOf(38, 45, 50, 55, 57, 62)),
}

/** Interval name of a pitch class relative to a chord root. */
fun degreeName(semitonesFromRoot: Int): String = DEGREES[Math.floorMod(semitonesFromRoot, 12)]

private val DEGREES = listOf("R", "♭9", "9", "♭3", "3", "4", "♭5", "5", "♯5", "6", "♭7", "7")
