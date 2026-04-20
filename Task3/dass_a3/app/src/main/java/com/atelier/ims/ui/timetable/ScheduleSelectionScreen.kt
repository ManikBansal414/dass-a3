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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.atelier.ims.ui.components.AtelierBottomBar
import com.atelier.ims.ui.components.AtelierTopBar
import com.atelier.ims.ui.components.PrimaryButton
import com.atelier.ims.ui.components.TinyLabel
import com.atelier.ims.ui.theme.AtelierClay
import com.atelier.ims.ui.theme.AtelierInk
import com.atelier.ims.ui.theme.AtelierMuted
import com.atelier.ims.ui.theme.AtelierPanel
import com.atelier.ims.ui.theme.AtelierPaper

private data class SlotItem(val time: String, val title: String, val occupied: Boolean = false)

@Composable
fun ScheduleSelectionScreen(
    onBack: () -> Unit,
    onConfirmed: () -> Unit
) {
    var selectedSlot by rememberSaveable { mutableStateOf("10:00") }
    val morning = listOf(
        SlotItem("08:00", "Reserved by Studio A", occupied = true),
        SlotItem("09:00", "Available Slot"),
        SlotItem("10:00", "Selected Session"),
        SlotItem("11:00", "Available Slot")
    )
    val afternoon = listOf(
        SlotItem("13:00", "Available Slot"),
        SlotItem("14:00", "Workshop in Progress", occupied = true),
        SlotItem("15:00", "Available Slot")
    )

    Scaffold(
        containerColor = AtelierPaper,
        topBar = {
            AtelierTopBar(
                title = "IMS",
                showBack = true,
                onBack = onBack,
                trailing = Icons.Outlined.Notifications
            )
        },
        bottomBar = {
            AtelierBottomBar(
                selected = "schedule",
                onHome = onBack,
                onSchedule = {}
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
                Text("Schedule Selection", style = MaterialTheme.typography.headlineMedium)
                Spacer(Modifier.height(8.dp))
                Text(
                    "Choose your preferred studio time. Our atelier operates with scholarly precision.",
                    color = AtelierMuted
                )
            }
            item { DateRow() }
            item {
                SlotSection(
                    title = "Morning Sessions",
                    slots = morning,
                    selectedSlot = selectedSlot,
                    onSelected = { selectedSlot = it }
                )
            }
            item {
                SlotSection(
                    title = "Afternoon Sessions",
                    slots = afternoon,
                    selectedSlot = selectedSlot,
                    onSelected = { selectedSlot = it }
                )
            }
            item {
                Legend()
                Spacer(Modifier.height(22.dp))
                PrimaryButton(
                    text = "Confirm Selection",
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onConfirmed
                )
            }
        }
    }
}

@Composable
private fun DateRow() {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        listOf("12\nMON", "13\nTUE", "14\nWED", "15\nTHU", "16\nFRI").forEachIndexed { index, text ->
            val active = index == 0
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (active) AtelierClay else Color.White)
                    .padding(vertical = 13.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = text,
                    color = if (active) Color.White else AtelierInk,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun SlotSection(
    title: String,
    slots: List<SlotItem>,
    selectedSlot: String,
    onSelected: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(title, style = MaterialTheme.typography.titleLarge)
        slots.forEach { slot ->
            val selected = selectedSlot == slot.time
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(slot.time, color = if (selected) AtelierClay else AtelierInk, modifier = Modifier.weight(.22f))
                Row(
                    modifier = Modifier
                        .weight(.78f)
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            when {
                                selected -> AtelierClay
                                slot.occupied -> Color(0xFFE7E3DA)
                                else -> Color.White
                            }
                        )
                        .clickable(enabled = !slot.occupied) { onSelected(slot.time) }
                        .padding(horizontal = 18.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = slot.title,
                        color = if (selected) Color.White else AtelierMuted,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                    )
                    Text(
                        text = if (slot.occupied) "LOCKED" else if (selected) "OK" else "+",
                        color = if (selected) Color.White else AtelierClay,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun Legend() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(AtelierPanel)
            .padding(14.dp),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        TinyLabel("Selected", AtelierClay)
        TinyLabel("Available", AtelierMuted)
        TinyLabel("Occupied", Color(0xFFB8B1A5))
    }
}
