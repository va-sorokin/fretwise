package com.fretwise.app.music

data class ChordType(val suffix: String, val quality: String, val intervals: List<Int>)

data class ChordMatch(val root: Int, val bass: Int, val type: ChordType) {
    val isInversion: Boolean get() = root != bass

    fun name(notation: Notation): String =
        notation.noteName(root) + type.suffix + if (isInversion) "/" + notation.noteName(bass) else ""

    fun description(notation: Notation): String =
        notation.noteName(root) + " " + type.quality +
            if (isInversion) ", " + notation.noteName(bass) + " in the bass" else ""
}

/** What the current fingering reads as. */
sealed interface ChordReading {
    data object Empty : ChordReading

    data class NoChord(val pitchClasses: List<Int>) : ChordReading

    data class Chord(
        val best: ChordMatch,
        val alternates: List<ChordMatch>,
        val pitchClasses: List<Int>,
    ) : ChordReading
}

object ChordIdentifier {
    /**
     * Ordered by preference: when several roots match, the earlier type wins
     * after root position.
     */
    private val TYPES = listOf(
        ChordType("", "major", listOf(0, 4, 7)),
        ChordType("m", "minor", listOf(0, 3, 7)),
        ChordType("5", "power chord", listOf(0, 7)),
        ChordType("7", "dominant 7th", listOf(0, 4, 7, 10)),
        ChordType("maj7", "major 7th", listOf(0, 4, 7, 11)),
        ChordType("m7", "minor 7th", listOf(0, 3, 7, 10)),
        ChordType("sus4", "suspended 4th", listOf(0, 5, 7)),
        ChordType("sus2", "suspended 2nd", listOf(0, 2, 7)),
        ChordType("dim", "diminished", listOf(0, 3, 6)),
        ChordType("aug", "augmented", listOf(0, 4, 8)),
        ChordType("6", "major 6th", listOf(0, 4, 7, 9)),
        ChordType("m6", "minor 6th", listOf(0, 3, 7, 9)),
        ChordType("add9", "major add 9", listOf(0, 2, 4, 7)),
        ChordType("madd9", "minor add 9", listOf(0, 2, 3, 7)),
        ChordType("7sus4", "dominant 7th sus4", listOf(0, 5, 7, 10)),
        ChordType("m7♭5", "half-diminished", listOf(0, 3, 6, 10)),
        ChordType("dim7", "diminished 7th", listOf(0, 3, 6, 9)),
        ChordType("mMaj7", "minor-major 7th", listOf(0, 3, 7, 11)),
        ChordType("9", "dominant 9th", listOf(0, 2, 4, 7, 10)),
        ChordType("maj9", "major 9th", listOf(0, 2, 4, 7, 11)),
        ChordType("m9", "minor 9th", listOf(0, 2, 3, 7, 10)),
        ChordType("6/9", "six-nine", listOf(0, 2, 4, 7, 9)),
        ChordType("7♯5", "augmented 7th", listOf(0, 4, 8, 10)),
        ChordType("7♭9", "dominant 7th flat 9", listOf(0, 1, 4, 7, 10)),
        ChordType("7", "dominant 7th, no 5th", listOf(0, 4, 10)),
        ChordType("maj7", "major 7th, no 5th", listOf(0, 4, 11)),
        ChordType("m7", "minor 7th, no 5th", listOf(0, 3, 10)),
        ChordType("9", "dominant 9th, no 5th", listOf(0, 2, 4, 10)),
    )

    private const val MAX_ALTERNATES = 2

    /** [pitchClasses] are ordered from the lowest sounding note up; the first one is the bass. */
    fun read(pitchClasses: List<Int>, notation: Notation): ChordReading {
        if (pitchClasses.isEmpty()) return ChordReading.Empty
        val unique = pitchClasses.distinct()
        val matches = identify(unique, bass = pitchClasses.first())
        val best = matches.firstOrNull() ?: return ChordReading.NoChord(unique)

        // Alternates are distinct by spelling: "C7" with and without the 5th is one name.
        val bestName = best.name(notation)
        val alternates = matches.drop(1)
            .distinctBy { it.name(notation) }
            .filter { it.name(notation) != bestName }
            .take(MAX_ALTERNATES)
        return ChordReading.Chord(best, alternates, unique)
    }

    private fun identify(unique: List<Int>, bass: Int): List<ChordMatch> {
        if (unique.size < 2) return emptyList()
        data class Scored(val match: ChordMatch, val score: Int)

        val found = unique.flatMap { root ->
            val shape = unique.map { Math.floorMod(it - root, 12) }.sorted()
            TYPES.mapIndexedNotNull { index, type ->
                if (type.intervals != shape) return@mapIndexedNotNull null
                val score = (if (root == bass) 0 else 100) + index
                Scored(ChordMatch(root, bass, type), score)
            }
        }
        return found.sortedBy { it.score }.map { it.match }
    }
}
