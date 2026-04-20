package com.atelier.ims.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.atelier.ims.ImsViewModel
import com.atelier.ims.data.AdmissionApplication
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
import com.atelier.ims.ui.theme.AtelierPaper
import com.atelier.ims.ui.theme.AtelierWarning

@Composable
fun AdmissionReviewDetailScreen(
    viewModel: ImsViewModel,
    onBack: () -> Unit,
    onDecisionFinalized: () -> Unit
) {
    val application = viewModel.selectedAdmission() ?: viewModel.admissionApplications.firstOrNull()

    Scaffold(
        containerColor = AtelierPaper,
        topBar = {
            AtelierTopBar(
                title = "Admission Review",
                showBack = true,
                onBack = onBack,
                trailing = Icons.Outlined.Notifications
            )
        }
    ) { padding ->
        if (application == null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(AtelierPaper)
                    .padding(padding)
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text("No admission application selected.", color = AtelierMuted)
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
            TinyLabel(application.progressLabel, statusColor(application.status))
            Text(application.applicantName, style = MaterialTheme.typography.headlineMedium)
            Text("${application.studentId} | ${application.program}", color = AtelierMuted)

            var editablePhone by remember(application.id) { mutableStateOf(application.draft.phone) }
            var editableAddress by remember(application.id) { mutableStateOf(application.draft.address) }
            var editableGuardian by remember(application.id) { mutableStateOf(application.draft.guardianName) }
            var editableEmergency by remember(application.id) { mutableStateOf(application.draft.emergencyContactPhone) }

            AtelierCard(containerColor = Color.White) {
                TinyLabel("Edit Student Data", AtelierClay)
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = editablePhone,
                    onValueChange = { editablePhone = it },
                    label = { Text("Primary Contact") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = editableAddress,
                    onValueChange = { editableAddress = it },
                    label = { Text("Address") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = editableGuardian,
                    onValueChange = { editableGuardian = it },
                    label = { Text("Guardian") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = editableEmergency,
                    onValueChange = { editableEmergency = it },
                    label = { Text("Emergency Contact") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(Modifier.height(10.dp))
                PrimaryButton("Save Edited Data", Modifier.fillMaxWidth()) {
                    viewModel.updateSelectedAdmissionDraft {
                        it.copy(
                            phone = editablePhone,
                            address = editableAddress,
                            guardianName = editableGuardian,
                            emergencyContactPhone = editableEmergency
                        )
                    }
                }
            }

            AdminReviewBlock(
                title = "Personal Details",
                rows = listOf(
                    "Full Name: ${application.draft.fullName}",
                    "Date of Birth: ${application.draft.birthDate}",
                    "Contact: ${application.draft.phone}",
                    "Category: ${application.draft.category}",
                    "Address: ${application.draft.address}"
                )
            )

            AdminReviewBlock(
                title = "Guardian Information",
                rows = listOf(
                    "Guardian: ${application.draft.guardianName}",
                    "Relationship: ${application.draft.guardianRelation}",
                    "Phone: ${application.draft.guardianPhone}",
                    "Email: ${application.draft.guardianEmail}",
                    "Emergency: ${application.draft.emergencyContactName} (${application.draft.emergencyContactPhone})"
                )
            )

            AdminReviewBlock(
                title = "Academic Record",
                rows = listOf(
                    "Institution: ${application.draft.institution}",
                    "Board: ${application.draft.board}",
                    "Graduation Year: ${application.draft.graduationYear}",
                    "GPA: ${application.draft.gpa}",
                    "Documents: ${application.draft.supportingDocuments}"
                )
            )

            SectionLabel("Decision Controls")
            PrimaryButton("Approve Application", Modifier.fillMaxWidth()) {
                viewModel.updateAdmissionStatus("Approved")
                onDecisionFinalized()
            }
            SecondaryButton("Keep Pending", Modifier.fillMaxWidth()) {
                viewModel.updateAdmissionStatus("Pending")
            }
            SecondaryButton("Reject Application", Modifier.fillMaxWidth()) {
                viewModel.updateAdmissionStatus("Rejected")
                onDecisionFinalized()
            }
            SecondaryButton("Undo Last Status Change", Modifier.fillMaxWidth()) {
                viewModel.undoAdmissionStatusChange()
            }
            Spacer(Modifier.height(16.dp))
            Text(
                text = "Current Status: ${viewModel.selectedAdmission()?.status ?: application.status}",
                color = statusColor(viewModel.selectedAdmission()?.status ?: application.status),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun AdminReviewBlock(title: String, rows: List<String>) {
    AtelierCard(containerColor = Color.White) {
        Text(title, color = AtelierInk, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        rows.forEach { row ->
            Text(row, color = AtelierMuted, fontSize = 13.sp)
            Spacer(Modifier.height(4.dp))
        }
    }
}

private fun statusColor(status: String): Color {
    return when (status) {
        "Approved" -> AtelierGreen
        "Rejected" -> AtelierWarning
        else -> AtelierClay
    }
}
