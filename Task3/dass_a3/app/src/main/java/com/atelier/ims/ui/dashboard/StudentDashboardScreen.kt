package com.atelier.ims.ui.dashboard

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.atelier.ims.ImsViewModel
import com.atelier.ims.data.ModuleItem
import com.atelier.ims.data.TimetableEntry
import com.atelier.ims.ui.components.AtelierBottomBar
import com.atelier.ims.ui.components.AtelierCard
import com.atelier.ims.ui.components.AtelierTopBar
import com.atelier.ims.ui.components.Avatar
import com.atelier.ims.ui.components.PrimaryButton
import com.atelier.ims.ui.components.SectionLabel
import com.atelier.ims.ui.components.StatusDot
import com.atelier.ims.ui.components.TinyLabel
import com.atelier.ims.ui.theme.AtelierCharcoal
import com.atelier.ims.ui.theme.AtelierClay
import com.atelier.ims.ui.theme.AtelierGreen
import com.atelier.ims.ui.theme.AtelierInk
import com.atelier.ims.ui.theme.AtelierLine
import com.atelier.ims.ui.theme.AtelierMuted
import com.atelier.ims.ui.theme.AtelierPanel
import com.atelier.ims.ui.theme.AtelierPaper

@Composable
fun StudentDashboardScreen(
    viewModel: ImsViewModel,
    onOpenTimetable: () -> Unit,
    onOpenConflicts: () -> Unit,
    onOpenProfile: () -> Unit,
    onLogout: () -> Unit
) {
    val timetable by viewModel.timetable.collectAsState()
    val isAdmin = viewModel.isAdmin()
    val todayEntries = timetable.filter { it.day == "WED" }.sortedBy { it.time }
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
                onSchedule = onOpenTimetable,
                showAlerts = false,
                onAlerts = { if (isAdmin) onOpenConflicts() else onOpenTimetable() },
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
                    HeaderBlock(
                        name = viewModel.student.name,
                        program = viewModel.student.program,
                        cohort = viewModel.student.cohort,
                        onLogout = onLogout
                    )
                }
                item { TodayScheduleCard(entries = todayEntries, onOpenTimetable = onOpenTimetable) }
                item {
                    AtelierCard(containerColor = AtelierCharcoal, borderColor = AtelierCharcoal) {
                        TinyLabel("Critical Updates", Color(0xFFD96654))
                        Spacer(Modifier.height(10.dp))
                        Text("System Maintenance Scheduled", color = Color.White, fontWeight = FontWeight.Bold)
                        Text("Enrollment portal will be offline on Friday 02:00-04:00 AM.", color = Color(0xFFC9C5BD))
                    }
                }
                item {
                    ShortcutGrid(onOpenTimetable = onOpenTimetable)
                }
                item { LatestNewsCard("Campus studio upgraded with 20 new digital workstations.") }
                item { SectionLabel("Active Modules", "View all") }
                items(viewModel.activeModules, key = { it.id }) { module ->
                    ModuleRow(
                        module = module,
                        onClick = { viewModel.dismissModule(module.id) }
                    )
                }
                item { RegistryPulse() }
                item { Spacer(Modifier.height(10.dp)) }
            }

            if (searchExpanded) {
                GlobalSearchPanel(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    onClose = { searchExpanded = false },
                    onHome = {},
                    onSchedule = onOpenTimetable,
                    onProfile = onOpenProfile,
                    onOpenCourse = { name, details ->
                        selectedCourse = name to details
                        searchExpanded = false
                    }
                )
            }

            selectedCourse?.let { (name, details) ->
                CourseDetailsOverlay(
                    courseName = name,
                    details = details,
                    onClose = { selectedCourse = null }
                )
            }
        }
    }
}

@Composable
private fun GlobalSearchPanel(
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
                .align(Alignment.TopCenter),
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
private fun CourseDetailsOverlay(
    courseName: String,
    details: String,
    onClose: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize().background(Color(0x55000000))) {
        AtelierCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.TopCenter),
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
private fun LatestNewsCard(news: String) {
    AtelierCard(containerColor = Color.White) {
        TinyLabel("Latest News", AtelierClay)
        Spacer(Modifier.height(8.dp))
        Text(news, color = AtelierInk, fontSize = 13.sp)
    }
}

@Composable
private fun HeaderBlock(name: String, program: String, cohort: String, onLogout: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Hello Scholar.",
                style = MaterialTheme.typography.headlineMedium,
                fontStyle = FontStyle.Italic
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "You have 3 lectures today and one pending assignment.",
                style = MaterialTheme.typography.bodyMedium
            )
        }
        Avatar(text = name.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString(""))
    }
    Spacer(Modifier.height(10.dp))
    AtelierCard(containerColor = AtelierPanel) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Avatar(text = "IMS", color = Color(0xFFEAE5DC), modifier = Modifier.size(48.dp))
            Column(modifier = Modifier.padding(start = 14.dp)) {
                Text(text = name, fontWeight = FontWeight.Bold, color = AtelierInk)
                Text(text = "$program / $cohort", color = AtelierMuted, fontSize = 13.sp)
            }
        }
    }
    Spacer(Modifier.height(10.dp))
    PrimaryButton("Logout", Modifier.fillMaxWidth(), onLogout)
}

@Composable
private fun TodayScheduleCard(entries: List<TimetableEntry>, onOpenTimetable: () -> Unit) {
    Column {
        AtelierCard(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onOpenTimetable),
            containerColor = AtelierCharcoal,
            borderColor = AtelierCharcoal
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Today's Schedule",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White
                )
                Text(text = "OCT 14", color = Color(0xFFAAA59C), fontSize = 10.sp)
            }
            Spacer(Modifier.height(16.dp))
            entries.take(3).forEach { entry ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 7.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Text(entry.time, color = Color(0xFF9A948B), fontSize = 12.sp, modifier = Modifier.weight(.22f))
                    Column(modifier = Modifier.weight(.78f)) {
                        Text(entry.title, color = Color.White, fontWeight = FontWeight.SemiBold)
                        Text(entry.category, color = Color(0xFFBDB8AE), fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun ShortcutGrid(
    onOpenTimetable: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        TinyLabel("Shortcuts")
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            ShortcutTile(
                title = "View Schedule",
                subtitle = "Today",
                modifier = Modifier.weight(1f),
                onClick = onOpenTimetable
            )
            ShortcutTile(
                title = "Attendance",
                subtitle = "Mark now",
                modifier = Modifier.weight(1f),
                onClick = {}
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            ShortcutTile(
                title = "Apply Leave",
                subtitle = "Submit request",
                modifier = Modifier.weight(1f),
                onClick = {}
            )
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun ShortcutTile(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val icon: ImageVector = if (title.contains("Schedule")) Icons.Outlined.CalendarMonth else Icons.Outlined.Assignment

    AtelierCard(
        modifier = modifier
            .height(92.dp)
            .clickable(onClick = onClick)
    ) {
        Icon(icon, contentDescription = null, tint = AtelierMuted, modifier = Modifier.size(24.dp))
        Spacer(Modifier.height(10.dp))
        Text(title, color = AtelierInk, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
        Text(subtitle, color = AtelierMuted, fontSize = 11.sp)
    }
}

@Composable
private fun ModuleRow(
    module: ModuleItem,
    onClick: () -> Unit
) {
    AtelierCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFE8E5DE)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Outlined.Person, contentDescription = null, tint = AtelierMuted)
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 14.dp)
            ) {
                Text(module.title, color = AtelierInk, fontWeight = FontWeight.SemiBold)
                Text(module.subtitle, color = AtelierMuted, fontSize = 12.sp)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    StatusDot(if (module.status == "Viewed") AtelierGreen else AtelierClay)
                    Text(
                        text = "  ${module.status.uppercase()}",
                        color = AtelierMuted,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Icon(Icons.Outlined.KeyboardArrowRight, contentDescription = null, tint = AtelierMuted)
        }
    }
}

@Composable
private fun RegistryPulse() {
    AtelierCard(containerColor = Color(0xFFE8E6DD), borderColor = Color.Transparent) {
        Text("Registry Pulse", style = MaterialTheme.typography.titleLarge, color = AtelierInk)
        Spacer(Modifier.height(12.dp))
        PulseLine("Lectures today", "03")
        PulseLine("Assignments due", "01")
    }
}

@Composable
private fun PulseLine(label: String, value: String, valueColor: Color = AtelierInk) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 7.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label.uppercase(), color = AtelierMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        Text(value, color = valueColor, fontWeight = FontWeight.SemiBold)
    }
}
