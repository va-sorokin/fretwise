package com.fretwise.app.music

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ChordIdentifierTest {
    private val m = Fingering.MUTED

    private fun read(vararg frets: Int, tuning: Tuning = Tuning.Standard, notation: Notation = Notation.Sharps) =
        ChordIdentifier.read(Fingering(frets.toList()).soundingPitchClasses(tuning), notation)

    private fun bestName(vararg frets: Int, tuning: Tuning = Tuning.Standard, notation: Notation = Notation.Sharps): String {
        val reading = read(*frets, tuning = tuning, notation = notation)
        assertTrue("Expected a chord, got $reading", reading is ChordReading.Chord)
        return (reading as ChordReading.Chord).best.name(notation)
    }

    @Test fun openShapes() {
        assertEquals("C", bestName(m, 3, 2, 0, 1, 0))
        assertEquals("G", bestName(3, 2, 0, 0, 0, 3))
        assertEquals("Am", bestName(m, 0, 2, 2, 1, 0))
        assertEquals("E7", bestName(0, 2, 0, 1, 0, 0))
        assertEquals("Dmaj7", bestName(m, m, 0, 2, 2, 2))
        assertEquals("E5", bestName(0, 2, 2, m, m, m))
    }

    @Test fun inversionNamesTheBass() {
        val reading = read(0, 3, 2, 0, 1, 0) as ChordReading.Chord
        assertEquals("C/E", reading.best.name(Notation.Sharps))
        assertEquals("C major, E in the bass", reading.best.description(Notation.Sharps))
    }

    @Test fun ambiguousShapeOffersAlternates() {
        // A C E G in root position on A: Am7, also C6.
        val reading = read(m, 0, 2, 0, 1, 0) as ChordReading.Chord
        assertEquals("Am7", reading.best.name(Notation.Sharps))
        assertEquals(listOf("C6/A"), reading.alternates.map { it.name(Notation.Sharps) })
    }

    @Test fun flatsAndTunings() {
        assertEquals("B♭", bestName(m, 1, 3, 3, 3, 1, notation = Notation.Flats))
        assertEquals("A♯", bestName(m, 1, 3, 3, 3, 1))
        assertEquals("G/D", bestName(0, 0, 0, 0, 0, 0, tuning = Tuning.OpenG))
        assertEquals("G", bestName(m, 0, 0, 0, 0, 0, tuning = Tuning.OpenG))
    }

    @Test fun emptyAndNonChords() {
        assertEquals(ChordReading.Empty, read(m, m, m, m, m, m))
        assertTrue(read(m, m, m, m, m, 0) is ChordReading.NoChord)
        assertTrue(read(m, m, m, 4, 5, 6) is ChordReading.NoChord) // B, E, A♯: no match
    }
}
