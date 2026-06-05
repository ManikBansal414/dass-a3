# DASS Assignment 3 — Institute Management System (IMS)

Brief Jetpack Compose Android prototype for the IMS system required for the DASS Assignment 3.

## Overview
- Purpose: A prototype Android app implementing the Dashboard and selected IMS modules (end-to-end flows simulated with a local stub repository).
- Tech: Kotlin, Jetpack Compose, Gradle (wrapper), single-activity architecture.

## Project layout (key items)
- `Task1/` — design reports and UML artifacts.
- `Task2/` — UI/UX prototypes and Figma notes.
- `Task3/dass_a3/app/` — Android app implementation (main codebase).
- Main app entry points:
  - [app/src/main/java/com/atelier/ims/MainActivity.kt](app/src/main/java/com/atelier/ims/MainActivity.kt#L1)
  - [app/src/main/java/com/atelier/ims/ImsViewModel.kt](app/src/main/java/com/atelier/ims/ImsViewModel.kt#L1)
  - [app/src/main/java/com/atelier/ims/data/StubRepository.kt](app/src/main/java/com/atelier/ims/data/StubRepository.kt#L1)

## Requirements
- Android Studio (recommended) or command-line Gradle via the included wrapper.
- Android SDK and an emulator or device.

## Build & run

Windows (PowerShell) using the included Gradle wrapper:

```
.\gradlew.bat assembleDebug
.\gradlew.bat installDebug
```

Or open the project in Android Studio and run the `app` module.

If you prefer a single command that builds and installs to a running emulator:

```
.\gradlew.bat installDebug
```

## Important: APP_IDENTIFIER BuildConfigField
The assignment requires a `BuildConfigField` named `APP_IDENTIFIER` (String) to be set in your `app` module. Add or update inside the `defaultConfig` block of `app/build.gradle.kts` (or the Groovy equivalent) as follows, replacing the value with your roll number format requested by the course:

```kotlin
android {
  defaultConfig {
    // example - replace with your value: "rollnumber.teamnumber"
    buildConfigField("String", "APP_IDENTIFIER", "\"your_rollnumber.teamnumber\"")
  }
}
```

Set the exact value requested by the assignment (e.g. `"123456.0"` or `"rollnumber.teammates_rollnumber"`).

## Notes for developers
- The app uses a stubbed repository (`StubRepository`) to simulate data flows — no server required.
- UI is implemented with Compose under `app/src/main/java/com/atelier/ims/ui/`.
- Navigation and routes are in `navigation` package.

## How to contribute / modify
- This repository is an assignment submission. For local changes, open in Android Studio and edit code in `app/src/main/java/com/atelier/ims`.

## Troubleshooting
- If builds fail, ensure Android SDK and build tools are installed and that the Gradle wrapper is executable.
- For emulator/device installs, make sure a device is connected or an emulator is running.

## License & notes
- This repository is for the DASS Assignment 3 submission. Contact the project owner for permission before reuse.

---

If you want, I can add screenshots, badges, or expand sections (detailed file map, run examples). What would you like next?
[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-22041afd0340ce965d47ae6ef1cefeee28c7c493a6346c4f15d667ab976d596c.svg)](https://classroom.github.com/a/Ka5kk0bc)
