# OBDForge Android (FOSS)

FOSS-only Gradle/Kotlin app for F-Droid. No Google Play Services or Firebase.

## Repository layout

```text
examples/android/
  app/src/main/
    res/values/strings.xml       # user-visible strings (English default)
    res/values-{lang}/           # add when shipping translations
    java/.../ui/
      theme/                     # GoldenPathTheme, generated Color.kt / Type.kt / Dimens.kt
      components/                # ThemeToggle, etc. — labels via stringResource()
      screens/                   # GoldenPathScreen, etc.
  metadata/en-US/                # F-Droid / Fastlane store listing text
```

**Styles and strings are separate:** theme colors and spacing live in `ui/theme/` (from `design-tokens/`). All copy lives in `strings.xml`, consumed via `stringResource(R.string.*)` in Compose — never `Text("literal")`.

See [`docs/DESIGN_GUIDE.md`](../../docs/DESIGN_GUIDE.md) and [`docs/WEB_PROJECT_LAYOUT.md`](../../docs/WEB_PROJECT_LAYOUT.md) for cross-stack conventions.

## Structure validation (CI)

CI validates Gradle file structure, FOSS compliance markers, and F-Droid metadata via `scripts/verify-fdroid-metadata.sh`.

## Local build (ADB / HUMAN tasks)

```bash
cd examples/android
./gradlew assembleDebug
```

## Reproducible release build (F-Droid)

```bash
bash scripts/build-release-apk.sh
# or with clean rebuild:
bash scripts/build-release-apk.sh --clean
```

Release builds pin `SOURCE_DATE_EPOCH=1700000000` (fixed project epoch). Verify locally:

```bash
bash scripts/verify-reproducible-apk.sh
bash scripts/verify-fdroid-metadata.sh
```

## Emulator checklist

Before running instrumented tests or manual QA:

- 🔲 Android SDK Platform 34+ installed (`sdkmanager "platforms;android-34"`)
- 🔲 Build-tools 34.x installed
- 🔲 System image with Google APIs **not** required (use AOSP image for FOSS parity)
- 🔲 `adb devices` lists emulator or hardware as `device`
- ✅ Set `SOURCE_DATE_EPOCH` for reproducible release builds (default: `1700000000`)
- 🔲 Accept licenses: `sdkmanager --licenses`

## FOSS compliance

- No `com.google.android.gms` dependencies
- No Firebase dependencies
- `SOURCE_DATE_EPOCH` for reproducible builds (see `scripts/build-release-apk.sh`)
- Pinned Gradle wrapper SHA-256 in `gradle/wrapper/gradle-wrapper.properties`

## F-Droid notes

- Store listing text: `metadata/en-US/` — validate with `bash scripts/verify-fdroid-metadata.sh`
- fdroiddata MR draft: `metadata/fdroiddata-handoff.yml`
- Device dry-run: `bash scripts/fdroid-device-dry-run.sh` (`[ADB]`)
