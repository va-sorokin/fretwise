# Fretwise

Android chord finder: tap notes on a guitar neck, see the chord name.
Kotlin + Jetpack Compose (Material 3), single activity.

## Layout

- `music/`: pure Kotlin chord logic (`Tuning`, `Notation`, `Fingering`, `ChordIdentifier`), unit-tested
- `data/SettingsRepository`: tuning, note spelling, left-handed neck, note names on dots, onboarding flag
- `ui/welcome`, `ui/finder`, `ui/settings`: the three screens
- `ui/theme`: dark amber Material 3 scheme, Figtree (body) and Space Grotesk (display)

The screen is kept awake while the app is in the foreground (`FLAG_KEEP_SCREEN_ON` in `MainActivity`).

## Build

```
./gradlew :app:assembleDebug
./gradlew :app:testDebugUnitTest
```

Fonts are under the SIL Open Font License; see `licenses/`.
