package com.atelier.ims

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.atelier.ims.data.AdmissionDraft
import com.atelier.ims.data.BatchItem
import com.atelier.ims.data.ConflictIssue
import com.atelier.ims.data.CourseItem
import com.atelier.ims.data.SubjectItem
import com.atelier.ims.data.StubRepository
import com.atelier.ims.data.TimetableEntry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class ImsViewModel : ViewModel() {
    val student = StubRepository.student
    val shortcuts = StubRepository.shortcuts
    val activeModules = StubRepository.activeModules
    val alerts = StubRepository.alerts
    var loginError by mutableStateOf<String?>(null)
        private set
    var selectedRole by mutableStateOf("Scholar")
        private set
    var admissionDraft by mutableStateOf(AdmissionDraft())
        private set
    var selectedAdmissionId by mutableStateOf(StubRepository.admissionApplications.firstOrNull()?.id)
        private set
    var selectedConflictId by mutableStateOf(StubRepository.conflictIssues.firstOrNull()?.id)
        private set
    private var lastAdmissionStatusChange by mutableStateOf<Pair<String, String>?>(null)
    private var lastResolvedConflictId by mutableStateOf<String?>(null)

    private val _timetable = MutableStateFlow(StubRepository.timetable)
    val timetable: StateFlow<List<TimetableEntry>> = _timetable
    val admissionApplications = StubRepository.admissionApplications
    val conflictIssues = StubRepository.conflictIssues
    val studentAlerts = StubRepository.studentAlerts
    val courses = StubRepository.courses
    val subjects = StubRepository.subjects
    val batches = StubRepository.batches
    val adminConfigState = StubRepository.adminConfig

    fun isAdmin() = selectedRole == "Admin"

    fun isScholar() = selectedRole == "Scholar"

    fun selectRole(role: String) {
        selectedRole = role
        loginError = null
    }

    fun login(email: String, password: String): Boolean {
        val validScholar = selectedRole == "Scholar" &&
            email.trim().equals("scholar@atelier.edu", ignoreCase = true) &&
            password == "atelier123"
        val validAdmin = selectedRole == "Admin" &&
            email.trim().equals("admin@atelier.edu", ignoreCase = true) &&
            password == "admin123"

        return if (validScholar || validAdmin) {
            loginError = null
            true
        } else {
            loginError = "Invalid email or password. Please try again."
            false
        }
    }

    fun addTimetableEntry(entry: TimetableEntry) {
        _timetable.update { current -> current + entry }
    }

    fun updateAdmissionDraft(draft: AdmissionDraft) {
        admissionDraft = draft
    }

    fun submitAdmission() {
        val submittedDraft = admissionDraft.copy(submitted = true)
        admissionDraft = submittedDraft
        val generatedId = nextStudentId()
        val existing = admissionApplications.indexOfFirst { it.draft.fullName == submittedDraft.fullName }
        if (existing >= 0) {
            admissionApplications[existing] = admissionApplications[existing].copy(
                status = "Pending",
                progressLabel = "Committee Review",
                draft = submittedDraft
            )
            selectedAdmissionId = admissionApplications[existing].id
        } else {
            val appId = "app-${(admissionApplications.size + 1).toString().padStart(3, '0')}"
            admissionApplications.add(
                com.atelier.ims.data.AdmissionApplication(
                    id = appId,
                    applicantName = submittedDraft.fullName,
                    program = submittedDraft.category,
                    submittedOn = "Apr 18, 2026",
                    status = "Pending",
                    progressLabel = "Committee Review",
                    studentId = generatedId,
                    draft = submittedDraft
                )
            )
            selectedAdmissionId = appId
        }
    }

    fun openAdmissionForReview(id: String) {
        selectedAdmissionId = id
    }

    fun updateAdmissionStatus(status: String) {
        val currentId = selectedAdmissionId ?: return
        val index = admissionApplications.indexOfFirst { it.id == currentId }
        if (index >= 0) {
            val current = admissionApplications[index]
            lastAdmissionStatusChange = currentId to current.status
            admissionApplications[index] = current.copy(
                status = status,
                progressLabel = when (status) {
                    "Approved" -> "Official Decision"
                    "Rejected" -> "Screening Complete"
                    else -> "Committee Review"
                }
            )
        }
    }

    fun undoAdmissionStatusChange() {
        val snapshot = lastAdmissionStatusChange ?: return
        val (id, previousStatus) = snapshot
        val index = admissionApplications.indexOfFirst { it.id == id }
        if (index >= 0) {
            val current = admissionApplications[index]
            admissionApplications[index] = current.copy(
                status = previousStatus,
                progressLabel = when (previousStatus) {
                    "Approved" -> "Official Decision"
                    "Rejected" -> "Screening Complete"
                    else -> "Committee Review"
                }
            )
            lastAdmissionStatusChange = null
        }
    }

    fun selectedAdmission() = admissionApplications.firstOrNull { it.id == selectedAdmissionId }

    fun updateSelectedAdmissionDraft(transform: (AdmissionDraft) -> AdmissionDraft) {
        val currentId = selectedAdmissionId ?: return
        val index = admissionApplications.indexOfFirst { it.id == currentId }
        if (index >= 0) {
            val current = admissionApplications[index]
            admissionApplications[index] = current.copy(draft = transform(current.draft))
        }
    }

    fun openConflictResolution(id: String) {
        selectedConflictId = id
    }

    fun selectedConflict(): ConflictIssue? = conflictIssues.firstOrNull { it.id == selectedConflictId }

    fun resolveSelectedConflict() {
        val currentId = selectedConflictId ?: return
        val index = conflictIssues.indexOfFirst { it.id == currentId }
        if (index >= 0) {
            val current = conflictIssues[index]
            conflictIssues[index] = current.copy(resolved = true)
            lastResolvedConflictId = currentId
        }
    }

    fun undoConflictResolution() {
        val conflictId = lastResolvedConflictId ?: return
        val index = conflictIssues.indexOfFirst { it.id == conflictId }
        if (index >= 0) {
            val current = conflictIssues[index]
            conflictIssues[index] = current.copy(resolved = false)
            lastResolvedConflictId = null
        }
    }

    fun updateAdminConfig(
        country: String,
        currency: String,
        timezone: String,
        language: String,
        autoUniqueId: Boolean,
        smsAlertsEnabled: Boolean,
        graduationRule: String,
        studentCategoryRule: String
    ) {
        adminConfigState.value = adminConfigState.value.copy(
            country = country,
            currency = currency,
            timezone = timezone,
            language = language,
            autoUniqueId = autoUniqueId,
            smsAlertsEnabled = smsAlertsEnabled,
            graduationRule = graduationRule,
            studentCategoryRule = studentCategoryRule
        )
    }

    fun addCourse(name: String, batch: String, electiveCount: Int) {
        if (name.isBlank() || batch.isBlank()) return
        val id = "course-${courses.size + 1}"
        courses.add(CourseItem(id, name, batch, electiveCount.coerceAtLeast(0)))
    }

    fun addSubject(name: String, isElective: Boolean, weeklyLimit: Int) {
        if (name.isBlank()) return
        val id = "sub-${subjects.size + 1}"
        subjects.add(SubjectItem(id, name, isElective, weeklyLimit.coerceAtLeast(1)))
    }

    fun addBatch(name: String, intake: Int, transferEnabled: Boolean) {
        if (name.isBlank()) return
        val id = "bat-${batches.size + 1}"
        batches.add(BatchItem(id, name, intake.coerceAtLeast(1), transferEnabled))
    }

    fun removeTimetableEntry(entryId: String) {
        if (!isAdmin()) return
        _timetable.update { entries -> entries.filterNot { it.id == entryId } }
    }

    fun nextStudentId(): String {
        val existingMax = admissionApplications
            .mapNotNull { it.studentId.substringAfterLast('-').toIntOrNull() }
            .maxOrNull() ?: 880
        return "SC-2024-${(existingMax + 1).toString().padStart(4, '0')}"
    }

    fun updateStudentPreferences(language: String, timezone: String) {
        val config = adminConfigState.value
        adminConfigState.value = config.copy(language = language, timezone = timezone)
    }

    fun dismissModule(moduleId: String) {
        val modules = activeModules.toMutableList()
        val index = modules.indexOfFirst { it.id == moduleId }
        if (index >= 0) {
            modules[index] = modules[index].copy(status = "Viewed")
            activeModules.clear()
            activeModules.addAll(modules)
        }
    }
}
