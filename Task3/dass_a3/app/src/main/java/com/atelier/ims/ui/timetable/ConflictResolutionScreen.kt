package com.atelier.ims.ui.timetable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.atelier.ims.ImsViewModel
import com.atelier.ims.ui.components.AtelierBottomBar
import com.atelier.ims.ui.components.AtelierCard
import com.atelier.ims.ui.components.AtelierTopBar
import com.atelier.ims.ui.components.PrimaryButton
import com.atelier.ims.ui.components.SecondaryButton
import com.atelier.ims.ui.components.TinyLabel
import com.atelier.ims.ui.theme.AtelierClay
import com.atelier.ims.ui.theme.AtelierGreen
import com.atelier.ims.ui.theme.AtelierInk
import com.atelier.ims.ui.theme.AtelierMuted
import com.atelier.ims.ui.theme.AtelierPaper

@Composable
fun ConflictResolutionScreen(
    viewModel: ImsViewModel,
    onBack: () -> Unit,
    onOpenBuilder: () -> Unit
) {
    val issue = viewModel.selectedConflict() ?: viewModel.conflictIssues.firstOrNull()

    Scaffold(
        containerColor = AtelierPaper,
        topBar = {
            AtelierTopBar(
                title = "Conflict Resolution",
                showBack = true,
                onBack = onBack,
                trailing = Icons.Outlined.AutoAwesome
            )
        },
        bottomBar = {
            AtelierBottomBar(
                selected = "alerts",
                onHome = onBack,
                onSchedule = onOpenBuilder,
                showSchedule = false,
                onAlerts = {},
                onProfile = onBack
            )
        }
    ) { padding ->
        if (issue == null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(AtelierPaper)
                    .padding(padding)
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text("No conflict selected.", color = AtelierMuted)
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(AtelierPaper)
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            TinyLabel(issue.severity, AtelierClay)
            Text(issue.title, style = MaterialTheme.typography.headlineMedium)
            Text(issue.detail, color = AtelierMuted)

            AtelierCard(containerColor = Color.White) {
                TinyLabel("Resolution Recommendation", AtelierGreen)
                Spacer(Modifier.height(8.dp))
                Text(issue.recommendedAction, color = AtelierInk, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(12.dp))
                ResolutionLine("Proposed Room", issue.proposedRoom)
                ResolutionLine("Proposed Time", issue.proposedTime)
                ResolutionLine("Proposed Faculty", issue.proposedFaculty)
            }

            AtelierCard(containerColor = Color(0xFF11120F), borderColor = Color(0xFF11120F)) {
                TinyLabel("AI Smart Resolution", Color(0xFF39B16B))
                Spacer(Modifier.height(10.dp))
                Text(issue.aiSummary, color = Color(0xFFE3DED6), lineHeight = 20.sp)
                Spacer(Modifier.height(14.dp))
                PrimaryButton("Apply AI Recommendation", Modifier.fillMaxWidth()) {
                    viewModel.resolveSelectedConflict()
                }
            }

            PrimaryButton("Mark Conflict as Resolved", Modifier.fillMaxWidth()) {
                viewModel.resolveSelectedConflict()
            }
            SecondaryButton("Undo Last Resolve", Modifier.fillMaxWidth()) {
                viewModel.undoConflictResolution()
            }
            SecondaryButton("Open Timetable Builder", Modifier.fillMaxWidth(), onOpenBuilder)

            val current = viewModel.selectedConflict()
            if (current?.resolved == true) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFE8F3EA))
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        "Conflict resolved successfully.",
                        color = AtelierGreen,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun ResolutionLine(label: String, value: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        TinyLabel(label)
        Text(value, color = AtelierInk)
        Spacer(Modifier.height(6.dp))
    }
}
