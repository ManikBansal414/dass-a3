package com.atelier.ims.data

import androidx.compose.runtime.mutableStateListOf

data class StudentProfile(
    val name: String,
    val program: String,
    val cohort: String,
    val avatarText: String
)

data class ShortcutAction(
    val title: String,
    val subtitle: String
)

data class ModuleItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val status: String
)

data class DashboardAlert(
    val label: String,
    val title: String,
    val body: String
)

data class TimetableEntry(
    val id: String,
    val day: String,
    val date: Int,
    val time: String,
    val endTime: String,
    val title: String,
    val category: String,
    val teacher: String,
    val room: String,
    val seats: Int,
    val isNow: Boolean = false
)

data class AdmissionDraft(
    val fullName: String = "Julian Vane",
    val birthDate: String = "09/14/2005",
    val phone: String = "+1 415 555 0192",
    val category: String = "Scholar Classification",
    val address: String = "Atelier North, Fine Arts Block",
    val profilePhotoName: String = "profile_julian_vane.jpg",
    val personalDeclarationAccepted: Boolean = false,
    val guardianName: String = "Dr. Eleanor Thorne",
    val guardianRelation: String = "Parent / Guardian",
    val guardianPhone: String = "+1 415 555 0188",
    val guardianEmail: String = "eleanor.thorne@atelier.edu",
    val emergencyContactName: String = "Adrian Vane",
    val emergencyContactPhone: String = "+1 415 555 0171",
    val institution: String = "The Belvedere Academy of Fine Arts",
    val board: String = "Cambridge International",
    val graduationYear: String = "2024",
    val gpa: String = "3.94",
    val supportingDocuments: String = "Transcript.pdf, Portfolio.pdf",
    val academicDeclarationAccepted: Boolean = false,
    val submitted: Boolean = false
)

data class AdmissionApplication(
    val id: String,
    val applicantName: String,
    val program: String,
    val submittedOn: String,
    val status: String,
    val progressLabel: String,
    val studentId: String,
    val draft: AdmissionDraft,
    val remarks: String = ""
)

data class ConflictIssue(
    val id: String,
    val severity: String,
    val title: String,
    val detail: String,
    val recommendedAction: String,
    val proposedRoom: String,
    val proposedTime: String,
    val proposedFaculty: String,
    val aiSummary: String,
    val resolved: Boolean = false
)

data class AdminConfig(
    val country: String = "India",
    val currency: String = "INR",
    val timezone: String = "Asia/Kolkata",
    val language: String = "English",
    val autoUniqueId: Boolean = true,
    val smsAlertsEnabled: Boolean = true,
    val graduationRule: String = "Credits + Attendance",
    val studentCategoryRule: String = "Merit, Scholarship, International"
)

data class CourseItem(
    val id: String,
    val name: String,
    val batch: String,
    val electiveCount: Int
)

data class SubjectItem(
    val id: String,
    val name: String,
    val isElective: Boolean,
    val weeklyLimit: Int
)

data class BatchItem(
    val id: String,
    val name: String,
    val intake: Int,
    val transferEnabled: Boolean
)

object MutableCollections {
    fun modules(vararg items: ModuleItem) = mutableStateListOf(*items)
}
