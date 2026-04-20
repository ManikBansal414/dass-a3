package com.atelier.ims.ui.admin

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
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
import com.atelier.ims.data.AdmissionApplication
import com.atelier.ims.ui.components.AtelierBottomBar
import com.atelier.ims.ui.components.AtelierCard
import com.atelier.ims.ui.components.AtelierTopBar
import com.atelier.ims.ui.components.SectionLabel
import com.atelier.ims.ui.components.TinyLabel
import com.atelier.ims.ui.theme.AtelierClay
import com.atelier.ims.ui.theme.AtelierGreen
import com.atelier.ims.ui.theme.AtelierInk
import com.atelier.ims.ui.theme.AtelierMuted
import com.atelier.ims.ui.theme.AtelierPaper
import com.atelier.ims.ui.theme.AtelierWarning

@Composable
fun AdmissionsListScreen(
    viewModel: ImsViewModel,
    onBack: () -> Unit,
    onOpenReview: () -> Unit
) {
    val applications = viewModel.admissionApplications
    val pending = applications.count { it.status == "Pending" }
    val approved = applications.count { it.status == "Approved" }
    val rejected = applications.count { it.status == "Rejected" }

    Scaffold(
        containerColor = AtelierPaper,
        topBar = {
            AtelierTopBar(
                title = "IMS",
                showBack = true,
                onBack = onBack,
                trailing = Icons.Outlined.Search
            )
        },
        bottomBar = {
            AtelierBottomBar(
                selected = "home",
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
                .padding(horizontal = 18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                TinyLabel("Academic Cycle 2024", AtelierClay)
                Spacer(Modifier.height(6.dp))
                Text("Student Admissions", style = MaterialTheme.typography.headlineMedium)
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    AdmissionsCountCard("Pending", pending.toString(), AtelierClay, Modifier.weight(1f))
                    AdmissionsCountCard("Approved", approved.toString(), AtelierGreen, Modifier.weight(1f))
                    AdmissionsCountCard("Rejected", rejected.toString(), AtelierWarning, Modifier.weight(1f))
                }
            }
            item {
                SectionLabel("Recent Submissions", "Archive")
            }
            items(applications, key = { it.id }) { application ->
                AdmissionListItem(application = application) {
                    viewModel.openAdmissionForReview(application.id)
                    onOpenReview()
                }
            }
        }
    }
}

@Composable
private fun AdmissionsCountCard(
    title: String,
    value: String,
    accent: Color,
    modifier: Modifier = Modifier
) {
    AtelierCard(modifier = modifier, containerColor = Color.White) {
        TinyLabel(title)
        Spacer(Modifier.height(6.dp))
        Text(value, fontSize = 22.sp, color = AtelierInk, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(4.dp))
        Text("Applications", color = accent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun AdmissionListItem(
    application: AdmissionApplication,
    onClick: () -> Unit
) {
    AtelierCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        containerColor = Color.White
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(application.applicantName, color = AtelierInk, fontWeight = FontWeight.Bold)
                Text("Student Applicant", color = AtelierMuted, fontSize = 12.sp)
            }
            Text(
                text = application.status.uppercase(),
                color = statusColor(application.status),
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
            )
        }
        Spacer(Modifier.height(8.dp))
        Text("Submitted ${application.submittedOn}", color = AtelierMuted, fontSize = 12.sp)
    }
}

private fun statusColor(status: String): Color {
    return when (status) {
        "Approved" -> AtelierGreen
        "Rejected" -> AtelierWarning
        else -> AtelierClay
    }
}

