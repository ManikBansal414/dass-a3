package com.atelier.ims.ui.timetable

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.atelier.ims.ImsViewModel
import com.atelier.ims.data.ConflictIssue
import com.atelier.ims.ui.components.AtelierBottomBar
import com.atelier.ims.ui.components.AtelierCard
import com.atelier.ims.ui.components.AtelierTopBar
import com.atelier.ims.ui.components.PrimaryButton
import com.atelier.ims.ui.components.SecondaryButton
import com.atelier.ims.ui.components.SectionLabel
import com.atelier.ims.ui.components.TinyLabel
import com.atelier.ims.ui.theme.AtelierClay
import com.atelier.ims.ui.theme.AtelierGreen
import com.atelier.ims.ui.theme.AtelierInk
import com.atelier.ims.ui.theme.AtelierMuted
import com.atelier.ims.ui.theme.AtelierPanel
import com.atelier.ims.ui.theme.AtelierPaper
import com.atelier.ims.ui.theme.AtelierWarning

@Composable
fun ConflictAlertsScreen(
    viewModel: ImsViewModel,
    onBack: () -> Unit,
    onOpenBuilder: () -> Unit,
    onOpenResolution: () -> Unit
) {
    val conflicts = viewModel.conflictIssues
    val resolvedCount = conflicts.count { it.resolved }

    Scaffold(
        containerColor = AtelierPaper,
        topBar = {
            AtelierTopBar(
                title = "Conflict Alerts",
                showBack = true,
                onBack = onBack,
                trailing = Icons.Outlined.FilterList
            )
        },
        bottomBar = {
            AtelierBottomBar(
                selected = "alerts",
                onHome = onBack,
                onSchedule = onBack,
                showSchedule = false
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(AtelierPaper)
                .padding(padding)
                .padding(horizontal = 22.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            item {
                Text("Schedule Overlaps", style = MaterialTheme.typography.headlineMedium)
                Spacer(Modifier.height(8.dp))
                Text(
                    "Review and resolve room assignments and faculty double-bookings.",
                    color = AtelierMuted
                )
                Spacer(Modifier.height(18.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFF0E6DD))
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TinyLabel("Active Conflicts", AtelierClay)
                    Text(
                        text = (conflicts.size - resolvedCount).coerceAtLeast(0).toString().padStart(2, '0'),
                        color = AtelierClay,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                }
            }
            items(conflicts.size) { index ->
                val conflict = conflicts[index]
                ConflictCard(
                    conflict = conflict,
                    resolved = conflict.resolved,
                    onResolve = {
                        viewModel.openConflictResolution(conflict.id)
                        onOpenResolution()
                    },
                    onOpenBuilder = onOpenBuilder
                )
            }
            item {
                SectionLabel("Recent Resolutions")
                Spacer(Modifier.height(10.dp))
                AtelierCard(containerColor = Color.White) {
                    ResolutionLine("14:02", "History Seminar A/B", "Merged")
                    ResolutionLine("12:45", "Lab Equipment 09", "Reallocated")
                }
            }
        }
    }
}

@Composable
private fun ConflictCard(
    conflict: ConflictIssue,
    resolved: Boolean,
    onResolve: () -> Unit,
    onOpenBuilder: () -> Unit
) {
    AtelierCard(containerColor = if (resolved) AtelierPanel else Color.White) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TinyLabel(
                text = if (resolved) "Resolved" else conflict.severity,
                color = when {
                    resolved -> AtelierGreen
                    conflict.severity.equals("Critical", ignoreCase = true) -> AtelierWarning
                    else -> AtelierGreen
                }
            )
            Text(
                text = if (conflict.severity.equals("Critical", ignoreCase = true)) "09:00 AM" else "",
                color = AtelierMuted,
                fontSize = 11.sp
            )
        }
        Spacer(Modifier.height(10.dp))
        Text(conflict.title, style = MaterialTheme.typography.titleLarge, color = AtelierInk)
        Spacer(Modifier.height(8.dp))
        Text(conflict.detail, color = AtelierMuted, fontSize = 13.sp)
        Spacer(Modifier.height(16.dp))
        if (resolved) {
            Text("Conflict resolved.", color = AtelierGreen, fontWeight = FontWeight.Bold)
        } else if (conflict.severity.equals("Critical", ignoreCase = true)) {
            PrimaryButton(
                text = "Resolve Conflict",
                modifier = Modifier.fillMaxWidth(),
                onClick = onResolve
            )
        } else {
            SecondaryButton(
                text = "Resolve Conflict",
                modifier = Modifier.fillMaxWidth(),
                onClick = onResolve
            )
        }
    }
}

@Composable
private fun ResolutionLine(time: String, title: String, status: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { }
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(time, color = AtelierMuted, fontSize = 11.sp)
            Text(title, color = AtelierInk, fontWeight = FontWeight.SemiBold)
        }
        Text(status.uppercase(), color = AtelierGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}
