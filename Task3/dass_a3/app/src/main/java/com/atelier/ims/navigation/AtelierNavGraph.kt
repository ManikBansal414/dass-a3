package com.atelier.ims.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.atelier.ims.ImsViewModel
import com.atelier.ims.ui.admission.AdmissionAcademicScreen
import com.atelier.ims.ui.admission.AdmissionGuardianScreen
import com.atelier.ims.ui.admission.AdmissionPersonalScreen
import com.atelier.ims.ui.admission.AdmissionReviewScreen
import com.atelier.ims.ui.admission.AdmissionStatusScreen
import com.atelier.ims.ui.admin.AdminDashboardScreen
import com.atelier.ims.ui.admin.AdmissionReviewDetailScreen
import com.atelier.ims.ui.admin.AdmissionsListScreen
import com.atelier.ims.ui.dashboard.StudentDashboardScreen
import com.atelier.ims.ui.components.RoleDeniedScreen
import com.atelier.ims.ui.login.LoginScreen
import com.atelier.ims.ui.profile.ProfileScreen
import com.atelier.ims.ui.timetable.ConflictAlertsScreen
import com.atelier.ims.ui.timetable.ConflictResolutionScreen
import com.atelier.ims.ui.timetable.CreateTimetableEntryScreen
import com.atelier.ims.ui.timetable.ScheduleSelectionScreen
import com.atelier.ims.ui.timetable.TimetableBuilderScreen
import com.atelier.ims.ui.timetable.TimetableScreen
import com.atelier.ims.ui.timetable.WeekSelectionScreen

@Composable
fun AtelierNavGraph(viewModel: ImsViewModel) {
    val navController = rememberNavController()
    fun homeRoute() = if (viewModel.selectedRole == "Admin") Route.AdminDashboard.path else Route.Dashboard.path
    fun navigateSingleTop(route: String) {
        navController.navigate(route) {
            launchSingleTop = true
        }
    }
    fun logoutToLogin() {
        navController.navigate(Route.Login.path) {
            popUpTo(Route.Login.path) { inclusive = true }
            launchSingleTop = true
        }
    }

    NavHost(
        navController = navController,
        startDestination = Route.Login.path
    ) {
        composable(Route.Login.path) {
            LoginScreen(
                viewModel = viewModel,
                onLoggedIn = { role ->
                    val destination = if (role == "Admin") Route.AdminDashboard.path else Route.Dashboard.path
                    navController.navigate(destination) {
                        popUpTo(Route.Login.path) { inclusive = true }
                    }
                }
            )
        }
        composable(Route.Dashboard.path) {
            StudentDashboardScreen(
                viewModel = viewModel,
                onOpenTimetable = { navigateSingleTop(Route.Timetable.path) },
                onOpenConflicts = { navigateSingleTop(Route.Conflicts.path) },
                onOpenProfile = { navigateSingleTop(Route.Profile.path) },
                onLogout = { logoutToLogin() }
            )
        }
        composable(Route.AdminDashboard.path) {
            AdminDashboardScreen(
                onOpenConflicts = { navigateSingleTop(Route.Conflicts.path) },
                onOpenBuilder = { navigateSingleTop(Route.Builder.path) },
                onOpenAdmissionIntake = { navigateSingleTop(Route.AdmissionPersonal.path) },
                onOpenAdmissions = { navigateSingleTop(Route.AdmissionsList.path) },
                onOpenProfile = { navigateSingleTop(Route.Profile.path) },
                onLogout = { logoutToLogin() }
            )
        }
        composable(Route.Timetable.path) {
            if (viewModel.isAdmin()) {
                RoleDeniedScreen("Timetable view is available only for Student role.") {
                    navController.navigate(Route.AdminDashboard.path) {
                        popUpTo(Route.AdminDashboard.path) { inclusive = false }
                        launchSingleTop = true
                    }
                }
            } else {
                TimetableScreen(
                    viewModel = viewModel,
                    onBackHome = {
                        val destination = homeRoute()
                        navController.navigate(destination) {
                            popUpTo(destination) { inclusive = false }
                            launchSingleTop = true
                        }
                    },
                    onCreateEntry = { navigateSingleTop(Route.CreateEntry.path) },
                    onOpenScheduleSelection = { navigateSingleTop(Route.ScheduleSelection.path) },
                    onOpenWeekSelection = { navigateSingleTop(Route.WeekSelection.path) },
                    onOpenBuilder = { navigateSingleTop(Route.Builder.path) },
                    onOpenConflicts = { navigateSingleTop(Route.Conflicts.path) },
                    onOpenProfile = { navigateSingleTop(Route.Profile.path) }
                )
            }
        }
        composable(Route.Profile.path) {
            ProfileScreen(
                viewModel = viewModel,
                onHome = {
                    val destination = homeRoute()
                    navController.navigate(destination) {
                        popUpTo(destination) { inclusive = false }
                        launchSingleTop = true
                    }
                },
                onSchedule = { navigateSingleTop(Route.Timetable.path) },
                onAlerts = { navigateSingleTop(Route.Conflicts.path) }
            )
        }
        composable(Route.ScheduleSelection.path) {
            ScheduleSelectionScreen(
                onBack = { navController.popBackStack() },
                onConfirmed = {
                    navController.popBackStack(Route.Timetable.path, inclusive = false)
                }
            )
        }
        composable(Route.WeekSelection.path) {
            WeekSelectionScreen(
                onBack = { navController.popBackStack() },
                onOpenWeek = {
                    navController.popBackStack(Route.Timetable.path, inclusive = false)
                }
            )
        }
        composable(Route.Builder.path) {
            if (viewModel.isAdmin()) {
                TimetableBuilderScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onSaved = {
                        navController.navigate(Route.AdminDashboard.path) {
                            popUpTo(Route.AdminDashboard.path) { inclusive = false }
                            launchSingleTop = true
                        }
                    }
                )
            } else {
                RoleDeniedScreen("Only Admin can create or edit timetable schedules.") {
                    navController.popBackStack()
                }
            }
        }
        composable(Route.Conflicts.path) {
            if (viewModel.isAdmin()) {
                ConflictAlertsScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onOpenBuilder = { navigateSingleTop(Route.Builder.path) },
                    onOpenResolution = { navigateSingleTop(Route.ConflictResolution.path) }
                )
            } else {
                RoleDeniedScreen("Only Admin can resolve timetable conflicts.") {
                    navController.popBackStack()
                }
            }
        }
        composable(Route.ConflictResolution.path) {
            if (viewModel.isAdmin()) {
                ConflictResolutionScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onOpenBuilder = { navigateSingleTop(Route.Builder.path) }
                )
            } else {
                RoleDeniedScreen("Only Admin can resolve timetable conflicts.") {
                    navController.popBackStack()
                }
            }
        }
        composable(Route.CreateEntry.path) {
            if (viewModel.isAdmin()) {
                CreateTimetableEntryScreen(
                    viewModel = viewModel,
                    onCancel = { navController.popBackStack() },
                    onSaved = { navController.popBackStack() }
                )
            } else {
                RoleDeniedScreen("Only Admin can create timetable entries.") {
                    navController.popBackStack()
                }
            }
        }
        composable(Route.AdmissionPersonal.path) {
            if (viewModel.isAdmin()) {
                AdmissionPersonalScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onNext = { navigateSingleTop(Route.AdmissionGuardian.path) }
                )
            } else {
                RoleDeniedScreen("Admissions form workflow is available only for Admin role.") {
                    navController.popBackStack()
                }
            }
        }
        composable(Route.AdmissionGuardian.path) {
            if (viewModel.isAdmin()) {
                AdmissionGuardianScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onNext = { navigateSingleTop(Route.AdmissionAcademic.path) }
                )
            } else {
                RoleDeniedScreen("Admissions form workflow is available only for Admin role.") {
                    navController.popBackStack()
                }
            }
        }
        composable(Route.AdmissionAcademic.path) {
            if (viewModel.isAdmin()) {
                AdmissionAcademicScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onNext = { navigateSingleTop(Route.AdmissionReview.path) }
                )
            } else {
                RoleDeniedScreen("Admissions form workflow is available only for Admin role.") {
                    navController.popBackStack()
                }
            }
        }
        composable(Route.AdmissionReview.path) {
            if (viewModel.isAdmin()) {
                AdmissionReviewScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onSubmit = {
                        viewModel.submitAdmission()
                        navigateSingleTop(Route.AdmissionStatus.path)
                    },
                    onEditPersonal = { navigateSingleTop(Route.AdmissionPersonal.path) },
                    onEditGuardian = { navigateSingleTop(Route.AdmissionGuardian.path) },
                    onEditAcademic = { navigateSingleTop(Route.AdmissionAcademic.path) }
                )
            } else {
                RoleDeniedScreen("Admissions form workflow is available only for Admin role.") {
                    navController.popBackStack()
                }
            }
        }
        composable(Route.AdmissionStatus.path) {
            AdmissionStatusScreen(
                viewModel = viewModel,
                onBackHome = {
                    val destination = if (viewModel.isAdmin()) Route.AdmissionsList.path else homeRoute()
                    navController.navigate(destination) {
                        popUpTo(destination) { inclusive = false }
                        launchSingleTop = true
                    }
                }
            )
        }
        composable(Route.AdmissionsList.path) {
            if (viewModel.isAdmin()) {
                AdmissionsListScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onOpenReview = { navigateSingleTop(Route.AdmissionAdminReview.path) }
                )
            } else {
                RoleDeniedScreen("Only Admin can access admission request handling.") {
                    navController.popBackStack()
                }
            }
        }
        composable(Route.AdmissionAdminReview.path) {
            if (viewModel.isAdmin()) {
                AdmissionReviewDetailScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onDecisionFinalized = { navigateSingleTop(Route.AdmissionStatus.path) }
                )
            } else {
                RoleDeniedScreen("Only Admin can approve, reject, or edit admissions.") {
                    navController.popBackStack()
                }
            }
        }
    }
}
