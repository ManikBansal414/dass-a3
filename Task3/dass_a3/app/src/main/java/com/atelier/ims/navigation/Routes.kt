package com.atelier.ims.navigation

sealed class Route(val path: String) {
    data object Login : Route("login")
    data object Dashboard : Route("dashboard")
    data object AdminDashboard : Route("admin-dashboard")
    data object Timetable : Route("timetable")
    data object Profile : Route("profile")
    data object ScheduleSelection : Route("schedule-selection")
    data object WeekSelection : Route("week-selection")
    data object Builder : Route("builder")
    data object Conflicts : Route("conflicts")
    data object ConflictResolution : Route("conflict-resolution")
    data object CreateEntry : Route("create-entry")
    data object AdmissionPersonal : Route("admission-personal")
    data object AdmissionGuardian : Route("admission-guardian")
    data object AdmissionAcademic : Route("admission-academic")
    data object AdmissionReview : Route("admission-review")
    data object AdmissionStatus : Route("admission-status")
    data object AdmissionsList : Route("admissions-list")
    data object AdmissionAdminReview : Route("admission-admin-review")
}
