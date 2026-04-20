package com.atelier.ims.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.atelier.ims.ui.components.AtelierBottomBar
import com.atelier.ims.ui.components.AtelierCard
import com.atelier.ims.ui.components.AtelierTopBar
import com.atelier.ims.ui.components.PrimaryButton
import com.atelier.ims.ui.components.SectionLabel
import com.atelier.ims.ui.components.TinyLabel
import com.atelier.ims.ui.theme.AtelierCharcoal
import com.atelier.ims.ui.theme.AtelierClay
import com.atelier.ims.ui.theme.AtelierGreen
import com.atelier.ims.ui.theme.AtelierInk
import com.atelier.ims.ui.theme.AtelierMuted
import com.atelier.ims.ui.theme.AtelierPaper

@Composable
fun AdminDashboardScreen(
    onOpenConflicts: () -> Unit,
    onOpenBuilder: () -> Unit,
    onOpenAdmissionIntake: () -> Unit,
    onOpenAdmissions: () -> Unit,
    onOpenProfile: () -> Unit,
    onLogout: () -> Unit
) {
    var searchExpanded by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedCourse by remember { mutableStateOf<Pair<String, String>?>(null) }

    Scaffold(
        containerColor = AtelierPaper,
        topBar = {
            AtelierTopBar(
                title = "IMS",
                showLeadingIcon = false,
                trailing = Icons.Outlined.Search,
                onSearch = { searchExpanded = !searchExpanded }
            )
        },
        bottomBar = {
            AtelierBottomBar(
                selected = "home",
                onHome = {},
                onSchedule = onOpenBuilder,
                showSchedule = false,
                onAlerts = onOpenConflicts,
                onProfile = onOpenProfile
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(AtelierPaper)
                .padding(padding)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 18.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                item {
                    Text(
                        text = "Welcome back, Professor Sterling.",
                        style = MaterialTheme.typography.headlineMedium,
                        fontStyle = FontStyle.Italic
                    )
                    Spacer(Modifier.height(6.dp))
                    Text("Supervising 14 active colloquia for Fall 2024.", color = AtelierMuted)
                    Spacer(Modifier.height(12.dp))
                    PrimaryButton("Logout", Modifier.fillMaxWidth(), onLogout)
                }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        AdminStat("Total Scholars", "1,284", "+12%", Modifier.weight(1f))
                        AdminStat("Active Batches", "42", "Stable", Modifier.weight(1f))
                    }
                }
                item { AdminStat("Completion Rate", "85%", "+4%", Modifier.fillMaxWidth()) }
                item {
                    AtelierCard(containerColor = AtelierCharcoal, borderColor = AtelierCharcoal) {
                        TinyLabel("Critical Updates", Color(0xFFD96654))
                        Spacer(Modifier.height(10.dp))
                        Text("System Maintenance Scheduled", color = Color.White, fontWeight = FontWeight.Bold)
                        Text("Enrollment portal will be offline on Friday 02:00-04:00 AM.", color = Color(0xFFC9C5BD))
                    }
                }
                item {
                    AtelierCard(containerColor = Color.White) {
                        TinyLabel("Latest News", AtelierClay)
                        Spacer(Modifier.height(8.dp))
                        Text("Admissions panel now supports quick status updates.", color = AtelierInk, fontSize = 13.sp)
                    }
                }
                item { SectionLabel("Operational Tasks") }
                item {
                    AdminTask("Admissions Intake", "Personal, guardian and academic forms", onOpenAdmissionIntake)
                    AdminTask("Timetable Builder", "Create and repair timetable entries", onOpenBuilder)
                    AdminTask("Schedule Conflicts", "Resolve overlaps and capacity issues", onOpenConflicts)
                    AdminTask("Review Admissions", "Track pending student applications", onOpenAdmissions)
                }
            }

            if (searchExpanded) {
                AdminGlobalSearchPanel(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    onClose = { searchExpanded = false },
                    onHome = {},
                    onSchedule = onOpenBuilder,
                    onProfile = onOpenProfile,
                    onOpenCourse = { name, details ->
                        selectedCourse = name to details
                        searchExpanded = false
                    }
                )
            }

            selectedCourse?.let { (name, details) ->
                AdminCourseDetailsOverlay(
                    courseName = name,
                    details = details,
                    onClose = { selectedCourse = null }
                )
            }
        }
    }
}

@Composable
private fun AdminGlobalSearchPanel(
    query: String,
    onQueryChange: (String) -> Unit,
    onClose: () -> Unit,
    onHome: () -> Unit,
    onSchedule: () -> Unit,
    onProfile: () -> Unit,
    onOpenCourse: (String, String) -> Unit
) {
    val trimmed = query.trim()
    val navMatches = if (trimmed.isBlank()) {
        emptyList()
    } else {
        listOf("Home", "Schedule", "Profile").filter { fuzzyMatch(trimmed, it) }
    }
    val courseInfo = mapOf(
        "Course A" to "Faculty: Dr. Elena Rossi\nRoom: Lab 02\nCredits: 4\nSchedule: Mon 08:00-09:00\nDescription: Introductory design studio fundamentals.",
        "Course B" to "Faculty: Dr. Mira Sen\nRoom: Seminar 2\nCredits: 3\nSchedule: Wed 14:00-15:00\nDescription: Modern history and critical discourse.",
        "Course C" to "Faculty: Prof. Julian Vance\nRoom: Atelier 4B\nCredits: 3\nSchedule: Tue 09:00-10:00\nDescription: Curatorial theory and exhibition logic.",
        "Course D" to "Faculty: Dr. Elena Rossi\nRoom: Lab 02\nCredits: 4\nSchedule: Wed 10:30-11:30\nDescription: Digital composition workflows and practice.",
        "Course E" to "Faculty: Dr. Helena Vance\nRoom: Library East\nCredits: 2\nSchedule: Fri 16:30-17:30\nDescription: Comparative mythologies across cultures."
    )
    val matchedCourses = if (trimmed.isBlank()) {
        emptyMap()
    } else {
        courseInfo.filterKeys { fuzzyMatch(trimmed, it) }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color(0x33000000))) {
        AtelierCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(androidx.compose.ui.Alignment.TopCenter),
            containerColor = Color.White
        ) {
            TinyLabel("Global Search", AtelierClay)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("") },
                singleLine = true
            )
            Spacer(Modifier.height(10.dp))
            if (trimmed.isBlank()) {
                Text("Type to search...", color = AtelierMuted, fontSize = 12.sp)
            }
            navMatches.forEach { item ->
                Text(
                    text = item,
                    color = AtelierInk,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            when (item) {
                                "Home" -> onHome()
                                "Schedule" -> onSchedule()
                                "Profile" -> onProfile()
                            }
                            onClose()
                        }
                        .padding(vertical = 6.dp)
                )
            }
            matchedCourses.forEach { (name, details) ->
                Text(
                    text = name,
                    color = AtelierClay,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOpenCourse(name, details) }
                        .padding(vertical = 6.dp)
                )
            }
            Spacer(Modifier.height(8.dp))
            PrimaryButton("Close", Modifier.fillMaxWidth(), onClose)
        }
    }
}

@Composable
private fun AdminCourseDetailsOverlay(
    courseName: String,
    details: String,
    onClose: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize().background(Color(0x55000000))) {
        AtelierCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(androidx.compose.ui.Alignment.TopCenter),
            containerColor = Color.White
        ) {
            TinyLabel("Course Details", AtelierClay)
            Spacer(Modifier.height(8.dp))
            Text(courseName, color = AtelierInk, style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(8.dp))
            Text(details, color = AtelierMuted, fontSize = 13.sp)
            Spacer(Modifier.height(10.dp))
            PrimaryButton("Close", Modifier.fillMaxWidth(), onClose)
        }
    }
}

private fun fuzzyMatch(query: String, target: String): Boolean {
    val q = query.lowercase().replace(" ", "")
    val t = target.lowercase().replace(" ", "")
    if (q.isBlank()) return false
    if (t.contains(q) || q.contains(t)) return true
    if (q.length == t.length) {
        val sortedQ = q.toCharArray().sorted().joinToString("")
        val sortedT = t.toCharArray().sorted().joinToString("")
        if (sortedQ == sortedT) return true
    }
    return false
}

@Composable
private fun AdminStat(title: String, value: String, meta: String, modifier: Modifier) {
    AtelierCard(modifier = modifier, containerColor = Color.White) {
        TinyLabel(title)
        Spacer(Modifier.height(8.dp))
        Text(value, color = AtelierInk, fontSize = 26.sp, fontWeight = FontWeight.Bold)
        Text(meta, color = if (meta.startsWith("+")) AtelierGreen else AtelierMuted, fontSize = 12.sp)
    }
}

@Composable
private fun AdminTask(title: String, subtitle: String, onClick: () -> Unit) {
    AtelierCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp)
            .clickable(onClick = onClick),
        containerColor = Color.White
    ) {
        Text(title, color = AtelierInk, fontWeight = FontWeight.Bold)
        Text(subtitle, color = AtelierMuted, fontSize = 13.sp)
        Text("OPEN", color = AtelierClay, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}
