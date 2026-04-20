# Atelier IMS (DASS Assignment 3)

Atelier IMS is a native Android prototype built in Kotlin + Jetpack Compose.
It demonstrates a role-based Institute Management System with end-to-end flows for:

- Dashboard (student and admin)
- Timetable management and viewing
- Student admission workflow with admin review states

The project uses a local stub repository (no backend server required), so all flows are demo-ready after build/run.

## Tech Stack

- Language: Kotlin
- UI: Jetpack Compose (Material 3)
- Navigation: `androidx.navigation.compose`
- State: ViewModel (`androidx.lifecycle.viewmodel.compose`)
- Build: Gradle Kotlin DSL
- Android Gradle Plugin: 8.7.3
- Kotlin plugin: 2.0.21
- Compile SDK: 35
- Min SDK: 24
- JVM toolchain: 17

## Project Structure

- `app/src/main/java/com/atelier/ims/MainActivity.kt`: app entry point
- `app/src/main/java/com/atelier/ims/ImsViewModel.kt`: central app state and UI actions
- `app/src/main/java/com/atelier/ims/data/StubRepository.kt`: in-memory demo data source
- `app/src/main/java/com/atelier/ims/navigation/AtelierNavGraph.kt`: route graph and role-based routing
- `app/src/main/java/com/atelier/ims/navigation/Routes.kt`: route constants
- `app/src/main/java/com/atelier/ims/ui/login/LoginScreen.kt`: role-based login
- `app/src/main/java/com/atelier/ims/ui/dashboard/StudentDashboardScreen.kt`: student dashboard
- `app/src/main/java/com/atelier/ims/ui/admin/AdminDashboardScreen.kt`: admin dashboard
- `app/src/main/java/com/atelier/ims/ui/timetable/TimetableScreen.kt`: timetable viewer
- `app/src/main/java/com/atelier/ims/ui/timetable/TimetableBuilderScreen.kt`: drag-and-drop timetable builder
- `app/src/main/java/com/atelier/ims/ui/timetable/ConflictAlertsScreen.kt`: conflict alerts list
- `app/src/main/java/com/atelier/ims/ui/timetable/ConflictResolutionScreen.kt`: conflict resolution flow
- `app/src/main/java/com/atelier/ims/ui/admission/AdmissionScreens.kt`: multi-step student admission screens
- `app/src/main/java/com/atelier/ims/ui/admin/AdmissionsListScreen.kt`: admin admissions overview
- `app/src/main/java/com/atelier/ims/ui/admin/AdmissionReviewDetailScreen.kt`: admin decision screen
- `app/src/main/java/com/atelier/ims/ui/components/AtelierComponents.kt`: reusable top bar, bottom bar, cards, shared UI
- `app/src/main/java/com/atelier/ims/ui/theme`: theme, colors, typography

## Feature Overview

### 1) Login and Role Flow

- Role-based login for Student (Scholar) and Admin
- Role-specific home/dashboard after successful login
- Navigation restrictions for admin-only routes

### 2) Dashboard Module

- Student dashboard:
    - Critical updates
    - Quick shortcuts
    - Search-triggered navigation and discovery
- Admin dashboard:
    - Operational task launchers
    - Conflict and admission management entry points
    - Search-triggered navigation and discovery

### 3) Timetable Module

- Student-facing timetable view
- Day/subject filtering and readable schedule cards
- Admin timetable builder with drag-drop placement
- Conflict alerts and conflict resolution workflow
- Undo support for key resolution/edit actions

### 4) Student Admission Module

- Multi-step student application flow:
    - Personal details
    - Guardian details
    - Academic history
    - Review and submit
- Hard validation for required fields before progression
- Admin review actions: Approve / Pending / Reject
- Status-aware feedback and role-aware return paths

## Prerequisites

Install the following before running:

1. Android Studio (latest stable recommended)
2. Android SDK with API 35
3. JDK 17 or newer (project uses JVM toolchain 17)
4. Android Emulator or physical Android device

## Required Configuration (Important)

Update the assignment identifier in `app/build.gradle.kts` before submission builds:

```kotlin
buildConfigField(
        "String",
        "APP_IDENTIFIER",
        "\"REPLACE_WITH_YOUR_ROLLNUMBER_OR_TEAM_ROLLNUMBERS\""
)
```

Replace the placeholder value with the exact roll number string required by your assignment instructions.

## How To Run (Android Studio)

1. Open the repository folder in Android Studio
2. Allow Gradle sync to complete
3. Select an emulator/device
4. Run the `app` run configuration

## Command-Line Build and Run

Use Gradle wrapper from the project root.

### Windows (PowerShell)

```powershell
.\gradlew.bat clean
.\gradlew.bat :app:assembleDebug
```

If your environment requires explicit JDK selection (common on Windows):

```powershell
$env:JAVA_HOME = "C:\Program Files\Android\Android Studio\jbr"
.\gradlew.bat :app:assembleDebug --stacktrace
```

### macOS/Linux

```bash
./gradlew clean
./gradlew :app:assembleDebug
```

### Install debug APK on connected device/emulator

Windows:

```powershell
.\gradlew.bat :app:installDebug
```

macOS/Linux:

```bash
./gradlew :app:installDebug
```

### Lint and Tests

Windows:

```powershell
.\gradlew.bat :app:lint
.\gradlew.bat test
```

macOS/Linux:

```bash
./gradlew :app:lint
./gradlew test
```

### Generate release APK (unsigned unless signing configured)

Windows:

```powershell
.\gradlew.bat :app:assembleRelease
```

macOS/Linux:

```bash
./gradlew :app:assembleRelease
```

## Output Paths

- Debug APK: `app/build/outputs/apk/debug/`
- Release APK: `app/build/outputs/apk/release/`

## Demo Credentials

- Scholar: `scholar@atelier.edu` / `atelier123`
- Admin: `admin@atelier.edu` / `admin123`

## Task 3 Mapping Files

- Screen/actor mapping: `data/TASK3_SCREEN_ACTOR_MAP.md`
- Detailed report (LaTeX): `REPORT.tex`

## Architecture Notes

- Single-activity Compose app with route-driven navigation
- `ImsViewModel` is the central state holder for cross-screen consistency
- Repository layer is intentionally local/stubbed for assignment prototyping
- Shared components are used for visual and interaction consistency across screens

## Troubleshooting

### 1) Build fails due to wrong Java version

Symptoms:

- AGP/Gradle compatibility errors
- Toolchain mismatch errors

Fix:

1. Use JDK 17+ (Android Studio JBR is recommended)
2. On Windows PowerShell:

```powershell
$env:JAVA_HOME = "C:\Program Files\Android\Android Studio\jbr"
.\gradlew.bat :app:assembleDebug --stacktrace
```

### 2) `Unable to delete directory app/build` or file lock issues (Windows)

Fix:

1. Close Android Studio run sessions using the APK
2. Stop Gradle daemons:

```powershell
.\gradlew.bat --stop
```

3. Retry clean/build:

```powershell
.\gradlew.bat clean
.\gradlew.bat :app:assembleDebug
```

### 3) Gradle sync fails on first open

Fix:

1. Check internet connectivity for dependency download
2. Ensure SDK API 35 is installed
3. In Android Studio, run "Sync Project with Gradle Files"

### 4) App installs but does not launch expected screen

Fix:

1. Confirm login role selection (Scholar vs Admin)
2. Rebuild and reinstall debug APK
3. Clear app data on emulator/device and relaunch

## Notes for Evaluation

- This is a prototype implementation focused on UX flow and module behavior, not production backend integration.
- Data persistence is intentionally local/in-memory for rapid demonstration.
- The role guard and module boundaries are implemented to match assignment scope.
