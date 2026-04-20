package com.atelier.ims.data

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf

object StubRepository {
    val student = StudentProfile(
        name = "Julian Vane",
        program = "Fine Arts",
        cohort = "Class of 2024",
        avatarText = "JV"
    )

    val shortcuts = listOf(
        ShortcutAction("View Schedule", "Today")
    )

    val activeModules = MutableCollections.modules(
        ModuleItem(
            id = "history",
            title = "History of Modernity",
            subtitle = "Impact of industrial revolution...",
            status = "Tomorrow"
        ),
        ModuleItem(
            id = "sculptural",
            title = "Sculptural Form",
            subtitle = "Exploring spatial relationships...",
            status = "2 days left"
        ),
        ModuleItem(
            id = "portfolio",
            title = "Portfolio Review",
            subtitle = "Submit curated study notes.",
            status = "Pending"
        )
    )

    val alerts = listOf(
        DashboardAlert(
            label = "Immediate Action Required",
            title = "Critical Overlap in Renaissance Studies",
            body = "Professor Elena Vance is double-booked for Hall B and the Gallery Seminar."
        ),
        DashboardAlert(
            label = "Room Conflict",
            title = "Hall C capacity error",
            body = "32 students enrolled, but the hall has 25 seats."
        )
    )

    val studentAlerts = mutableStateListOf(
        "Room changed: History of Modernity moved to Seminar 3 at 14:00.",
        "Workload alert: 3 submissions due within 48 hours.",
        "Faculty notice: Dr. Mira Sen office hours moved to Friday."
    )

    val timetable = listOf(
        TimetableEntry(
            id = "mon-1",
            day = "MON",
            date = 12,
            time = "08:00",
            endTime = "09:30",
            title = "Post-Enlightenment Philosophy",
            category = "Humanities",
            teacher = "Dr. Julian Vane",
            room = "Great Hall",
            seats = 42
        ),
        TimetableEntry(
            id = "mon-2",
            day = "MON",
            date = 12,
            time = "09:00",
            endTime = "10:00",
            title = "Advanced Curatorial Theory",
            category = "Lecture",
            teacher = "Dr. Julian Barnes",
            room = "Atelier 4B",
            seats = 28
        ),
        TimetableEntry(
            id = "wed-1",
            day = "WED",
            date = 14,
            time = "10:30",
            endTime = "12:00",
            title = "Digital Composition II",
            category = "Studio Workshop",
            teacher = "Prof. Elena Rossi",
            room = "Lab 02",
            seats = 18
        ),
        TimetableEntry(
            id = "wed-2",
            day = "WED",
            date = 14,
            time = "14:00",
            endTime = "15:30",
            title = "History of Modernity",
            category = "Seminar",
            teacher = "Dr. Mira Sen",
            room = "Seminar 2",
            seats = 30,
            isNow = true
        ),
        TimetableEntry(
            id = "fri-1",
            day = "FRI",
            date = 16,
            time = "16:30",
            endTime = "17:30",
            title = "Comparative Mythologies",
            category = "Theory",
            teacher = "Dr. Sarah Penton",
            room = "Library East Annex",
            seats = 22
        )
    )

    val admissionApplications = mutableStateListOf(
        AdmissionApplication(
            id = "app-001",
            applicantName = "Eleanor Vance",
            program = "Faculty of Fine Arts",
            submittedOn = "Oct 14, 2023",
            status = "Approved",
            progressLabel = "Official Decision",
            studentId = "SC-2024-0089",
            draft = AdmissionDraft(
                fullName = "Eleanor Vance",
                birthDate = "08/02/2004",
                phone = "+1 415 555 0134",
                category = "Merit Scholar",
                address = "Atelier Street, City Center",
                guardianName = "Harold Vance",
                guardianRelation = "Parent",
                guardianPhone = "+1 415 555 0136",
                guardianEmail = "harold.vance@atelier.edu",
                institution = "Atelier Prep Academy",
                board = "Cambridge International",
                gpa = "3.92",
                submitted = true
            )
        ),
        AdmissionApplication(
            id = "app-002",
            applicantName = "Julian Blackwood",
            program = "Dept. of Architecture",
            submittedOn = "Oct 12, 2023",
            status = "Pending",
            progressLabel = "Committee Review",
            studentId = "SC-2024-0091",
            draft = AdmissionDraft(
                fullName = "Julian Blackwood",
                birthDate = "06/19/2005",
                phone = "+1 415 555 0111",
                category = "Scholar Classification",
                address = "North Quadrant Residency",
                guardianName = "Naomi Blackwood",
                guardianRelation = "Guardian",
                guardianPhone = "+1 415 555 0113",
                guardianEmail = "naomi.blackwood@atelier.edu",
                institution = "Northridge Academy",
                board = "State Board",
                gpa = "3.71",
                submitted = true
            )
        ),
        AdmissionApplication(
            id = "app-003",
            applicantName = "Clara Sterling",
            program = "Dept. of History",
            submittedOn = "Oct 11, 2023",
            status = "Rejected",
            progressLabel = "Screening Complete",
            studentId = "SC-2024-0097",
            draft = AdmissionDraft(
                fullName = "Clara Sterling",
                birthDate = "10/05/2004",
                phone = "+1 415 555 0170",
                category = "International",
                address = "South Atelier Annex",
                guardianName = "Mark Sterling",
                guardianRelation = "Parent",
                guardianPhone = "+1 415 555 0174",
                guardianEmail = "mark.sterling@atelier.edu",
                institution = "Scholars Academy",
                board = "IB",
                gpa = "3.43",
                submitted = true
            )
        )
    )

    val conflictIssues = mutableStateListOf(
        ConflictIssue(
            id = "conf-001",
            severity = "Critical",
            title = "Modern Philosophy vs. Logic 101",
            detail = "Room overlap detected at 09:00 AM. Professors Aris and Kant are assigned to Hall A.",
            recommendedAction = "Move Logic 101 to Atelier 4B at 10:30 AM",
            proposedRoom = "Atelier 4B",
            proposedTime = "10:30 - 12:00",
            proposedFaculty = "Dr. Helena Vance",
            aiSummary = "Shifting Logic 101 resolves the room clash and keeps faculty load within limits."
        ),
        ConflictIssue(
            id = "conf-002",
            severity = "Faculty Limit",
            title = "Dr. Helena Vance Overload",
            detail = "Assigned 18/15 credit hours this week.",
            recommendedAction = "Reassign one seminar to Dr. Mira Sen",
            proposedRoom = "Seminar 2",
            proposedTime = "14:00 - 15:30",
            proposedFaculty = "Dr. Mira Sen",
            aiSummary = "Reallocation balances load and preserves student timetable continuity."
        )
    )

    val adminConfig = mutableStateOf(
        AdminConfig(
            country = "India",
            currency = "INR",
            timezone = "Asia/Kolkata",
            language = "English",
            autoUniqueId = true,
            smsAlertsEnabled = true,
            graduationRule = "Credits + Attendance",
            studentCategoryRule = "Merit, Scholarship, International"
        )
    )

    val courses = mutableStateListOf(
        CourseItem("course-1", "Master of Fine Arts", "Batch 2024", 3),
        CourseItem("course-2", "Digital Restoration", "Batch 2025", 2)
    )

    val subjects = mutableStateListOf(
        SubjectItem("sub-1", "Renaissance Studies", false, 3),
        SubjectItem("sub-2", "Curatorial Theory", true, 2),
        SubjectItem("sub-3", "Digital Composition", false, 4)
    )

    val batches = mutableStateListOf(
        BatchItem("bat-1", "B.Sc Architecture 2026", 120, true),
        BatchItem("bat-2", "Fine Arts Cohort 2024", 96, true)
    )
}
