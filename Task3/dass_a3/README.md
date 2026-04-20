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

### 1) Authentication and Session Flow

- Login screen supports role switch: Student (mapped internally to Scholar) and Admin.
- Student credentials: `scholar@atelier.edu` / `atelier123`.
- Admin credentials: `admin@atelier.edu` / `admin123`.
- Invalid credentials show inline error banner and block navigation.
- Successful login clears login route and lands on role home:
    - Student -> `Dashboard`
    - Admin -> `AdminDashboard`
- Logout from either dashboard returns to Login and clears active stack.

### 2) Student Role Flows

- Student Dashboard flow:
    - View profile header, schedule snapshot, critical updates, latest news, active modules.
    - Tap active module to mark it viewed.
    - Open Timetable from shortcuts or bottom navigation.
    - Open Profile from bottom navigation.
    - Use in-dashboard search overlay:
        - Navigate to Home, Schedule, Profile.
        - Open matched course detail overlay.

- Student Timetable flow:
    - Select day from week selector or grid.
    - Filter sessions by subject.
    - View daily cards and student alerts card.
    - Back/Home returns to Student Dashboard.
    - Open Schedule Selection modal route.
    - Open Week Selection route.

- Student Schedule Selection flow:
    - Select available slot (occupied slots are locked).
    - Confirm selection and return to Timetable.

- Student Week Selection flow:
    - Open active academic week and return to Timetable.

- Student Profile flow:
    - View scholar identity and academic summary.
    - Update language and timezone preferences.
    - View announcements feed.
    - Navigate Home/Schedule via bottom navigation.

### 3) Admin Role Flows

- Admin Dashboard flow:
    - View operational stats, critical updates, latest news.
    - Launch operational tasks:
        - Admissions Intake
        - Timetable Builder
        - Schedule Conflicts
        - Review Admissions
    - Open Profile from bottom navigation.
    - Use in-dashboard search overlay:
        - Navigate to Home, Schedule (Builder), Profile.
        - Open matched course detail overlay.

- Admin Timetable Builder flow:
    - Choose target batch.
    - Drag course cards from pool and drop into timetable slots.
    - Undo last placement.
    - Clear draft placements.
    - Save schedule to append timetable entries and return to Admin Dashboard.

- Admin Create Timetable Entry flow:
    - Fill batch, subject, teacher, room, day, time window, seats.
    - Save to create new entry.
    - Cancel returns without save.

- Admin Conflict Alerts flow:
    - View unresolved/resolved conflict cards and counters.
    - Open conflict resolution details for a selected conflict.
    - Open Timetable Builder for manual repair.

- Admin Conflict Resolution flow:
    - View selected conflict severity, details, recommendation, AI summary.
    - Resolve by applying AI recommendation or manual resolve action.
    - Undo last resolved conflict.
    - Open Timetable Builder directly from resolution screen.

- Admin Admissions Intake (4-step wizard):
    - Step 1 Personal Details:
        - Required validations: name, valid past DOB, valid phone, category, address, declaration.
    - Step 2 Guardian Information:
        - Required validations: guardian identity, relationship, valid phones, valid email, emergency contact.
    - Step 3 Academic History:
        - Required validations: institution, board, graduation year, GPA range, supporting documents, declaration.
    - Step 4 Review & Submit:
        - Review all sections.
        - Jump back to edit personal/guardian/academic sections.
        - Submit application to Admission Status.

- Admin Admissions List and Review flow:
    - View admissions list with Pending/Approved/Rejected counts.
    - Select application to open detailed review.
    - Edit admission fields (phone, address, guardian, emergency contact) and save.
    - Decision controls:
        - Approve -> Admission Status
        - Reject -> Admission Status
        - Keep Pending (in-place state update)
        - Undo last status change

- Admin Profile/System Settings flow:
    - Update global config: country, currency, timezone, language, graduation rule, category rule.
    - Toggle features: auto unique ID and SMS alerts.
    - Add Course entries.
    - Add Subject entries.
    - Add Batch entries and transfer rules.

### 4) Shared and Cross-Role Flows

- Admission Status flow (shared route):
    - Reads selected admission and current decision state.
    - Shows next steps based on status (Pending/Approved/Rejected).
    - Back button is role-aware:
        - Admin -> Admissions List
        - Student -> Dashboard

- Role-denied guard flow (`RoleDeniedScreen`):
    - Student blocked from:
        - Timetable Builder
        - Create Timetable Entry
        - Conflict Alerts/Resolution
        - Admission intake steps
        - Admissions list and admin review
    - Admin blocked from Student Timetable view route.

- Undo-capable flows:
    - Undo last admission status decision.
    - Undo last resolved conflict.
    - Undo last timetable draft placement in Builder.

- Navigation behavior rules:
    - `launchSingleTop` is used to prevent duplicate destinations in stack.
    - Home routing is role-aware (`Dashboard` for Student, `AdminDashboard` for Admin).

### 5) Current Scope and Limits (Implemented Behavior)

- Data is local in-memory (stub repository + ViewModel state).
- No backend API integration.
- No persistent login/session token storage across app restarts.
- Credentials are demo credentials hardcoded for assignment prototype usage.

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
