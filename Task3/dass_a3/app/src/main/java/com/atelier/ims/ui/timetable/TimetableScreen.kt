package com.atelier.ims.ui.timetable

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.atelier.ims.ImsViewModel
import com.atelier.ims.data.TimetableEntry
import com.atelier.ims.ui.components.AtelierBottomBar
import com.atelier.ims.ui.components.AtelierCard
import com.atelier.ims.ui.components.AtelierFab
import com.atelier.ims.ui.components.AtelierTopBar
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
import kotlinx.coroutines.launch

private data class DayChip(val label: String, val date: Int)

private val Week = listOf(
    DayChip("MON", 12),
    DayChip("TUE", 13),
    DayChip("WED", 14),
    DayChip("THU", 15),
    DayChip("FRI", 16)
)

@Composable
fun TimetableScreen(
    viewModel: ImsViewModel,
    onBackHome: () -> Unit,
    onCreateEntry: () -> Unit,
    onOpenScheduleSelection: () -> Unit,
    onOpenWeekSelection: () -> Unit,
    onOpenBuilder: () -> Unit,
    onOpenConflicts: () -> Unit,
    onOpenProfile: () -> Unit
) {
    val entries by viewModel.timetable.collectAsState()
    val isAdmin = viewModel.isAdmin()
    var selectedDay by remember { mutableStateOf("WED") }
    var selectedSubject by remember { mutableStateOf("All") }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val subjectOptions = remember(entries) {
        listOf("All") + entries.map { it.title }.distinct().sorted()
    }

    val filtered = entries
        .filter { it.day == selectedDay }
        .filter { selectedSubject == "All" || it.title == selectedSubject }
        .sortedBy { it.time }

    val adminEntries = remember(entries, viewModel.batches) {
        val adminBatches = viewModel.batches.map { it.name }.toSet()
        entries.filter { it.category in adminBatches }
    }

    Scaffold(
        containerColor = AtelierPaper,
        topBar = {
            AtelierTopBar(
                title = "IMS",
                showBack = true,
                onBack = onBackHome,
                trailing = Icons.Outlined.Search
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            if (isAdmin) {
                AtelierFab(onClick = onCreateEntry)
            }
        },
        bottomBar = {
            AtelierBottomBar(
                selected = "schedule",
                onHome = onBackHome,
                onSchedule = {},
                showAlerts = false,
                onAlerts = { if (isAdmin) onOpenConflicts() },
                onProfile = onOpenProfile
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(AtelierPaper)
                .padding(padding)
                .padding(horizontal = 18.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Weekly Timetable\nOct 12 - Oct 16",
                        style = MaterialTheme.typography.headlineLarge,
                        fontStyle = FontStyle.Italic,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            item {
                WeeklyTimetableGrid(
                    entries = entries,
                    selectedDay = selectedDay,
                    onSelectDay = { selectedDay = it }
                )
            }
            if (isAdmin) {
                item {
                    TimetableActions(
                        onOpenScheduleSelection = onOpenScheduleSelection,
                        onOpenWeekSelection = onOpenWeekSelection,
                        onOpenBuilder = onOpenBuilder,
                        onOpenConflicts = onOpenConflicts
                    )
                }
            }
            item {
                WeekSelector(
                    selected = selectedDay,
                    onSelect = { selectedDay = it }
                )
            }
            item {
                SubjectFilterRow(
                    options = subjectOptions,
                    selected = selectedSubject,
                    onSelected = { selectedSubject = it }
                )
            }
            if (!isAdmin) {
                item {
                    StudentAlertsCard(alerts = viewModel.studentAlerts)
                }
            }
            item {
                SectionLabel(
                    title = "Daily Timetable",
                    meta = "${filtered.size} sessions"
                )
            }
            if (filtered.isEmpty()) {
                item { EmptyScheduleCard(isAdmin = isAdmin, onCreateEntry = onCreateEntry) }
            } else {
                items(filtered, key = { it.id }) { entry ->
                    TimetableEntryCard(
                        entry = entry,
                        isAdmin = isAdmin,
                        onDelete = {
                            viewModel.removeTimetableEntry(entry.id)
                            scope.launch {
                                snackbarHostState.showSnackbar("Entry deleted. Use review to verify schedule consistency.")
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun WeeklyTimetableGrid(
    entries: List<TimetableEntry>,
    selectedDay: String,
    onSelectDay: (String) -> Unit
) {
    val times = listOf("08:00", "09:00", "10:30", "14:00", "16:30")
    AtelierCard(containerColor = Color.White) {
        SectionLabel("Class Timetable", "Week view")
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("TIME", color = AtelierMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(48.dp))
            Week.forEach { day ->
                Text(
                    text = day.label,
                    color = if (selectedDay == day.label) AtelierClay else AtelierMuted,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onSelectDay(day.label) }
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        times.forEach { time ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(time, color = AtelierMuted, fontSize = 11.sp, modifier = Modifier.width(48.dp))
                Week.forEach { day ->
                    val entry = entries.firstOrNull { it.day == day.label && it.time == time }
                    val selected = selectedDay == day.label
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                when {
                                    entry != null && selected -> Color(0xFFF0E2D8)
                                    entry != null -> Color(0xFFEFECE5)
                                    else -> AtelierPanel
                                }
                            )
                            .clickable { onSelectDay(day.label) },
                        contentAlignment = Alignment.Center
                    ) {
                        if (entry == null) {
                            Text(
                                text = "-",
                                color = AtelierMuted,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Normal
                            )
                        } else {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = entry.title.take(10),
                                    color = AtelierInk,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = entry.room.take(10),
                                    color = AtelierClay,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TimetableActions(
    onOpenScheduleSelection: () -> Unit,
    onOpenWeekSelection: () -> Unit,
    onOpenBuilder: () -> Unit,
    onOpenConflicts: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            ActionTile(
                title = "Select Slot",
                meta = "Studio time",
                modifier = Modifier.weight(1f),
                onClick = onOpenScheduleSelection
            )
            ActionTile(
                title = "Week",
                meta = "Oct 7 - 13",
                modifier = Modifier.weight(1f),
                onClick = onOpenWeekSelection
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            ActionTile(
                title = "Builder",
                meta = "Drag & drop",
                modifier = Modifier.weight(1f),
                onClick = onOpenBuilder
            )
            ActionTile(
                title = "Conflicts",
                meta = "Resolve",
                modifier = Modifier.weight(1f),
                onClick = onOpenConflicts
            )
        }
    }
}

@Composable
private fun ActionTile(
    title: String,
    meta: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    AtelierCard(
        modifier = modifier
            .height(92.dp)
            .clickable(onClick = onClick),
        containerColor = Color.White
    ) {
        Text(title, color = AtelierInk, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        Spacer(Modifier.height(6.dp))
        Text(meta, color = AtelierClay, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun WeekSelector(
    selected: String,
    onSelect: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Week.forEach { day ->
            val active = selected == day.label
            Column(
                modifier = Modifier
                    .width(58.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (active) AtelierCharcoal else Color.Transparent)
                    .clickable { onSelect(day.label) }
                    .padding(vertical = 11.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = day.label,
                    color = if (active) Color.White else AtelierMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(7.dp))
                Text(
                    text = day.date.toString(),
                    color = if (active) Color.White else AtelierInk,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
private fun TimetableEntryCard(entry: TimetableEntry) {
    TimetableEntryCard(entry = entry, isAdmin = false, onDelete = {})
}

@Composable
private fun TimetableEntryCard(
    entry: TimetableEntry,
    isAdmin: Boolean,
    onDelete: () -> Unit
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = entry.time,
                color = if (entry.isNow) AtelierGreen else AtelierClay,
                fontSize = 16.sp
            )
            Box(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .width(1.dp)
                    .height(120.dp)
                    .background(AtelierLine)
            )
        }
        Spacer(Modifier.width(18.dp))
        Card(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (entry.isNow) AtelierPanel else Color.White
            ),
            border = BorderStroke(
                width = 1.dp,
                color = if (entry.isNow) Color(0xFF9DCCB0) else Color.Transparent
            )
        ) {
            Column(Modifier.padding(18.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    TinyBadge(entry.category)
                    TinyBadge(if (entry.isNow) "Now" else "Lecture", if (entry.isNow) AtelierGreen else AtelierMuted)
                }
                Spacer(Modifier.height(10.dp))
                Text(
                    text = entry.title,
                    style = MaterialTheme.typography.titleLarge,
                    color = AtelierInk
                )
                if (isAdmin) {
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFF3ECE4))
                            .clickable(onClick = onDelete)
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Outlined.Delete, contentDescription = null, tint = AtelierClay, modifier = Modifier.size(16.dp))
                        Text("  Delete", color = AtelierClay, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
                Spacer(Modifier.height(14.dp))
                DetailLine(Icons.Outlined.Person, entry.teacher)
                DetailLine(Icons.Outlined.LocationOn, entry.room)
                DetailLine(Icons.Outlined.Groups, "${entry.seats} Students")
                if (entry.isNow) {
                    Spacer(Modifier.height(12.dp))
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(AtelierClay)
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text("Join Session", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailLine(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 3.dp)
    ) {
        Icon(icon, contentDescription = null, tint = AtelierMuted, modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(8.dp))
        Text(text, color = AtelierMuted, fontSize = 13.sp)
    }
}

@Composable
private fun TinyBadge(text: String, color: Color = AtelierMuted) {
    Text(
        text = text.uppercase(),
        color = color,
        fontWeight = FontWeight.Bold,
        fontSize = 9.sp,
        modifier = Modifier
            .clip(RoundedCornerShape(5.dp))
            .background(Color(0xFFF1EEE8))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    )
}

@Composable
private fun EmptyScheduleCard(isAdmin: Boolean, onCreateEntry: () -> Unit) {
    AtelierCard {
        Text(
            "No sessions in this section.",
            style = MaterialTheme.typography.titleMedium,
            color = AtelierInk
        )
        Spacer(Modifier.height(8.dp))
        Text(
            if (isAdmin) "Create a timetable entry to fill this day." else "No class found for this filter/day.",
            style = MaterialTheme.typography.bodyMedium
        )
        if (isAdmin) {
            Spacer(Modifier.height(14.dp))
            Text(
                "Create Entry",
                color = AtelierClay,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable(onClick = onCreateEntry)
            )
        }
    }
}

@Composable
private fun SubjectFilterRow(
    options: List<String>,
    selected: String,
    onSelected: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        options.forEach { option ->
            val active = option == selected
            Text(
                text = option,
                color = if (active) Color.White else AtelierInk,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (active) AtelierClay else Color.White)
                    .clickable { onSelected(option) }
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            )
        }
    }
}

@Composable
private fun StudentAlertsCard(alerts: List<String>) {
    AtelierCard(containerColor = Color.White) {
        SectionLabel("Alerts", "Student")
        Spacer(Modifier.height(8.dp))
        alerts.take(2).forEach { alert ->
            Text(alert, color = AtelierMuted, fontSize = 12.sp)
            Spacer(Modifier.height(6.dp))
        }
    }
}

@Composable
private fun AdminScheduleInfoCard(entries: List<TimetableEntry>) {
    AtelierCard(containerColor = Color.White) {
        SectionLabel("Admin Published Schedule", "Student View")
        Spacer(Modifier.height(8.dp))
        Text("You are viewing timetable slots created by Admin drag-and-drop planning.", color = AtelierMuted, fontSize = 12.sp)
        Spacer(Modifier.height(8.dp))
        Text("Total published entries: ${entries.size}", color = AtelierInk, fontWeight = FontWeight.Bold)
        entries.take(3).forEach { entry ->
            Text("- ${entry.day} ${entry.time} | ${entry.title} | ${entry.room}", color = AtelierMuted, fontSize = 12.sp)
        }
    }
}

@Composable
private fun WeeklyNote() {
    AtelierCard(containerColor = Color(0xFFE9E6DD), borderColor = Color.Transparent) {
        Text("Select Timetable Week", style = MaterialTheme.typography.titleLarge, color = AtelierClay)
        Spacer(Modifier.height(8.dp))
        Text("Academic Quarter: Autumn 2024", color = AtelierMuted)
        Spacer(Modifier.height(14.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            StatusDot(AtelierGreen)
            Text("  Current Active Session", color = AtelierInk, fontWeight = FontWeight.SemiBold)
        }
        Spacer(Modifier.height(8.dp))
        Text(
            "Timetable modifications for future weeks are saved as drafts. Past weeks remain read-only.",
            color = AtelierMuted,
            fontSize = 13.sp
        )
    }
}
