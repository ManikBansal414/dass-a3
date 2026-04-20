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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronLeft
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.atelier.ims.ui.components.AtelierCard
import com.atelier.ims.ui.components.AtelierTopBar
import com.atelier.ims.ui.components.PrimaryButton
import com.atelier.ims.ui.components.StatusDot
import com.atelier.ims.ui.theme.AtelierClay
import com.atelier.ims.ui.theme.AtelierGreen
import com.atelier.ims.ui.theme.AtelierInk
import com.atelier.ims.ui.theme.AtelierMuted
import com.atelier.ims.ui.theme.AtelierPaper

@Composable
fun WeekSelectionScreen(
    onBack: () -> Unit,
    onOpenWeek: () -> Unit
) {
    Scaffold(
        containerColor = AtelierPaper,
        topBar = {
            AtelierTopBar(
                title = "IMS",
                showBack = true,
                onBack = onBack
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(AtelierPaper)
                .padding(padding)
                .padding(horizontal = 24.dp, vertical = 18.dp)
        ) {
            Text("Select Timetable Week", style = MaterialTheme.typography.headlineMedium, color = AtelierClay)
            Spacer(Modifier.height(8.dp))
            Text("Academic Quarter: Autumn 2024", color = AtelierMuted)
            Spacer(Modifier.height(28.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("October 2024", color = AtelierInk, fontWeight = FontWeight.Bold)
                Row {
                    Icon(Icons.Outlined.ChevronLeft, contentDescription = null, tint = AtelierMuted)
                    Icon(Icons.Outlined.ChevronRight, contentDescription = null, tint = AtelierMuted)
                }
            }
            Spacer(Modifier.height(20.dp))
            CalendarGrid()
            Spacer(Modifier.height(30.dp))
            Text("Week", color = AtelierMuted, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Text("Oct 07 - Oct 13, 2024", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(24.dp))
            AtelierCard(containerColor = Color(0xFFEFECE5)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    StatusDot(AtelierGreen)
                    Text("  Current Active Session", color = AtelierGreen, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(10.dp))
                Text(
                    "You are currently viewing the Active Attendance Week. Changes made here affect real-time seminar rosters.",
                    color = AtelierMuted
                )
            }
            Spacer(Modifier.weight(1f))
            PrimaryButton(
                text = "View / Edit This Week ->",
                modifier = Modifier.fillMaxWidth(),
                onClick = onOpenWeek
            )
            Spacer(Modifier.height(20.dp))
            Text(
                text = "Timetable modifications for future weeks are saved as draft. Past weeks are available for archival review only.",
                color = AtelierMuted,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun CalendarGrid() {
    val cells = listOf("30", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20")
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        listOf("MO", "TU", "WE", "TH", "FR", "SA", "SU").chunked(7).forEach { header ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                header.forEach {
                    Text(it, color = AtelierMuted, fontSize = 10.sp, modifier = Modifier.weight(1f))
                }
            }
        }
        cells.chunked(7).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                row.forEach { day ->
                    val active = day in listOf("7", "8", "9", "10", "11", "12", "13")
                    Text(
                        text = day,
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (active) Color(0xFFF0E2D8) else Color.Transparent)
                            .clickable { }
                            .padding(vertical = 10.dp),
                        color = if (active) AtelierClay else AtelierInk,
                        fontWeight = if (active) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}
