package com.atelier.ims.ui.timetable

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
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
import com.atelier.ims.ui.components.AtelierTopBar
import com.atelier.ims.ui.components.PrimaryButton
import com.atelier.ims.ui.components.SecondaryButton
import com.atelier.ims.ui.components.StatusDot
import com.atelier.ims.ui.components.TinyLabel
import com.atelier.ims.ui.theme.AtelierClay
import com.atelier.ims.ui.theme.AtelierGreen
import com.atelier.ims.ui.theme.AtelierInk
import com.atelier.ims.ui.theme.AtelierMuted
import com.atelier.ims.ui.theme.AtelierPanel
import com.atelier.ims.ui.theme.AtelierPaper
import java.util.UUID

@Composable
fun CreateTimetableEntryScreen(
    viewModel: ImsViewModel,
    onCancel: () -> Unit,
    onSaved: () -> Unit
) {
    var batch by rememberSaveable { mutableStateOf("Classical Studies B") }
    var subject by rememberSaveable { mutableStateOf("Renaissance Pigment Mixing") }
    var teacher by rememberSaveable { mutableStateOf("Dr. Elena Moretti") }
    var studio by rememberSaveable { mutableStateOf("Studio 302B") }
    var day by rememberSaveable { mutableStateOf("MON") }
    var timeWindow by rememberSaveable { mutableStateOf("09:00 - 11:00") }
    var seats by rememberSaveable { mutableStateOf("24") }

    Scaffold(
        containerColor = AtelierPaper,
        topBar = {
            AtelierTopBar(
                title = "IMS",
                showBack = true,
                onBack = onCancel,
                trailing = Icons.Outlined.Search
            )
        },
        bottomBar = {
            AtelierBottomBar(
                selected = "schedule",
                onHome = onCancel,
                onSchedule = {},
                showSchedule = false
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(AtelierPaper)
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 22.dp, vertical = 12.dp)
        ) {
            Text(
                "Create Entry",
                style = MaterialTheme.typography.headlineLarge,
                color = AtelierInk
            )
            Spacer(Modifier.height(12.dp))
            Text(
                "Orchestrate the scholarly rhythm. Define the convergence of mind, subject, and space.",
                style = MaterialTheme.typography.bodyLarge,
                color = AtelierMuted
            )
            Spacer(Modifier.height(22.dp))
            CuratorNote()
            Spacer(Modifier.height(22.dp))
            AtelierCard(containerColor = Color.White) {
                DropdownField(
                    label = "Batch Selection",
                    value = batch,
                    options = listOf("Classical Studies B", "B.Sc Architecture 2026", "Fine Arts Cohort")
                ) { batch = it }
                Spacer(Modifier.height(14.dp))
                DropdownField(
                    label = "Subject Area",
                    value = subject,
                    options = listOf("Renaissance Pigment Mixing", "Advanced Studio Painting", "Digital Composition II")
                ) { subject = it }
                Spacer(Modifier.height(14.dp))
                DropdownField(
                    label = "Leading Scholar",
                    value = teacher,
                    options = listOf("Dr. Elena Moretti", "Prof. Julian Vance", "Dr. Mira Sen")
                ) { teacher = it }
                Spacer(Modifier.height(14.dp))
                DropdownField(
                    label = "Assigned Studio",
                    value = studio,
                    options = listOf("Studio 302B", "Atelier 4B", "Gallery Hall")
                ) { studio = it }
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    StatusDot(AtelierGreen)
                    Text("  Slot available", color = AtelierGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(14.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    DropdownField(
                        label = "Discourse Day",
                        value = day,
                        options = listOf("MON", "TUE", "WED", "THU", "FRI"),
                        modifier = Modifier.weight(1f)
                    ) { day = it }
                    DropdownField(
                        label = "Time Window",
                        value = timeWindow,
                        options = listOf("08:00 - 09:30", "09:00 - 11:00", "10:30 - 12:00", "14:00 - 15:30"),
                        modifier = Modifier.weight(1f)
                    ) { timeWindow = it }
                }
                Spacer(Modifier.height(14.dp))
                OutlinedTextField(
                    value = seats,
                    onValueChange = { seats = it.filter { char -> char.isDigit() }.take(3) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Seat Count") },
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp)
                )
                Spacer(Modifier.height(18.dp))
                SessionPreview(
                    batch = batch,
                    subject = subject,
                    teacher = teacher,
                    studio = studio
                )
            }
            Spacer(Modifier.height(22.dp))
            PrimaryButton(
                text = "Save Entry",
                modifier = Modifier.fillMaxWidth()
            ) {
                val parts = timeWindow.split("-")
                val entry = TimetableEntry(
                    id = UUID.randomUUID().toString(),
                    day = day,
                    date = dayToDate(day),
                    time = parts.first().trim(),
                    endTime = parts.getOrNull(1)?.trim().orEmpty(),
                    title = subject,
                    category = "Studio Practice",
                    teacher = teacher,
                    room = studio,
                    seats = seats.toIntOrNull() ?: 24,
                    isNow = false
                )
                viewModel.addTimetableEntry(entry)
                onSaved()
            }
            Spacer(Modifier.height(10.dp))
            SecondaryButton(
                text = "Cancel",
                modifier = Modifier.fillMaxWidth(),
                onClick = onCancel
            )
            Spacer(Modifier.height(28.dp))
        }
    }
}

@Composable
private fun CuratorNote() {
    AtelierCard(containerColor = Color(0xFFEFECE5)) {
        TinyLabel("Curator's Note", AtelierGreen)
        Spacer(Modifier.height(8.dp))
        Text(
            text = "\"The scheduling of thought requires as much precision as the execution of art.\"",
            color = AtelierMuted,
            fontStyle = FontStyle.Italic
        )
    }
}

@Composable
private fun DropdownField(
    label: String,
    value: String,
    options: List<String>,
    modifier: Modifier = Modifier,
    onSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        TinyLabel(label)
        Spacer(Modifier.height(7.dp))
        Box {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(AtelierPanel)
                    .clickable { expanded = true }
                    .padding(horizontal = 14.dp, vertical = 15.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(value, color = AtelierInk, modifier = Modifier.weight(1f))
                Icon(Icons.Outlined.KeyboardArrowDown, contentDescription = null, tint = AtelierMuted)
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onSelected(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun SessionPreview(
    batch: String,
    subject: String,
    teacher: String,
    studio: String
) {
    AtelierCard(containerColor = AtelierPanel) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TinyLabel("Session Preview", AtelierClay)
            Text(
                text = "Confirmed",
                color = AtelierGreen,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFE3F3E7))
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            )
        }
        Spacer(Modifier.height(14.dp))
        PreviewLine("Batch", batch)
        PreviewLine("Subject", subject)
        PreviewLine("Scholar", teacher)
        PreviewLine("Location", studio)
    }
}

@Composable
private fun PreviewLine(label: String, value: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
    ) {
        TinyLabel(label)
        Text(value, color = AtelierInk, fontSize = 14.sp)
    }
}

private fun dayToDate(day: String): Int {
    return when (day) {
        "MON" -> 12
        "TUE" -> 13
        "WED" -> 14
        "THU" -> 15
        else -> 16
    }
}
