package com.atelier.ims.ui.admission

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.atelier.ims.ImsViewModel
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
import com.atelier.ims.ui.theme.AtelierWarning

@Composable
fun AdmissionPersonalScreen(viewModel: ImsViewModel, onBack: () -> Unit, onNext: () -> Unit) {
    var attemptedNext by rememberSaveable { mutableStateOf(false) }

    AdmissionFormScaffold("Personal Details", "Step 1 of 4", onBack) {
        val draft = viewModel.admissionDraft

        val nameError = if (attemptedNext && draft.fullName.isBlank()) "Name is required" else null
        val dobError = if (attemptedNext && !isValidPastDate(draft.birthDate)) "Enter a valid past date (MM/DD/YYYY)" else null
        val phoneError = if (attemptedNext && !isValidPhone(draft.phone)) "Please enter a valid 10-digit number" else null
        val categoryError = if (attemptedNext && draft.category.isBlank()) "Student category is required" else null
        val addressError = if (attemptedNext && draft.address.isBlank()) "Address is required" else null
        val declarationError = if (attemptedNext && !draft.personalDeclarationAccepted) "You must accept the declaration to continue" else null
        val canProceed =
            draft.fullName.isNotBlank() &&
            isValidPastDate(draft.birthDate) &&
            isValidPhone(draft.phone) &&
            draft.category.isNotBlank() &&
            draft.address.isNotBlank() &&
            draft.personalDeclarationAccepted

        PhotoUploadCard(fileName = draft.profilePhotoName)
        AdmissionField("Full Name", draft.fullName, nameError) { viewModel.updateAdmissionDraft(draft.copy(fullName = it)) }
        AdmissionField("Date of Birth", draft.birthDate, dobError) { viewModel.updateAdmissionDraft(draft.copy(birthDate = it)) }
        AdmissionField("Primary Contact", draft.phone, phoneError) { viewModel.updateAdmissionDraft(draft.copy(phone = it)) }
        AdmissionField("Student Category", draft.category, categoryError) { viewModel.updateAdmissionDraft(draft.copy(category = it)) }
        AdmissionField("Residential Address", draft.address, addressError) { viewModel.updateAdmissionDraft(draft.copy(address = it)) }
        AdmissionDeclaration(
            checked = draft.personalDeclarationAccepted,
            error = declarationError
        ) {
            viewModel.updateAdmissionDraft(draft.copy(personalDeclarationAccepted = it))
        }

        Spacer(Modifier.height(18.dp))
        PrimaryButton("Next Step ->", Modifier.fillMaxWidth()) {
            attemptedNext = true
            if (canProceed) {
                onNext()
            }
        }
    }
}

@Composable
fun AdmissionGuardianScreen(viewModel: ImsViewModel, onBack: () -> Unit, onNext: () -> Unit) {
    var attemptedNext by rememberSaveable { mutableStateOf(false) }

    AdmissionFormScaffold("Guardian Information", "Step 2 of 4", onBack) {
        val draft = viewModel.admissionDraft

        val guardianNameError = if (attemptedNext && draft.guardianName.isBlank()) "Guardian name is required" else null
        val relationError = if (attemptedNext && draft.guardianRelation.isBlank()) "Relationship is required" else null
        val guardianPhoneError = if (attemptedNext && !isValidPhone(draft.guardianPhone)) "Please enter a valid 10-digit number" else null
        val guardianEmailError = if (attemptedNext && !isValidEmail(draft.guardianEmail)) "Enter a valid email address" else null
        val emergencyNameError = if (attemptedNext && draft.emergencyContactName.isBlank()) "Emergency contact name is required" else null
        val emergencyPhoneError = if (attemptedNext && !isValidPhone(draft.emergencyContactPhone)) "Emergency phone must be valid" else null
        val canProceed =
            draft.guardianName.isNotBlank() &&
            draft.guardianRelation.isNotBlank() &&
            isValidPhone(draft.guardianPhone) &&
            isValidEmail(draft.guardianEmail) &&
            draft.emergencyContactName.isNotBlank() &&
            isValidPhone(draft.emergencyContactPhone)

        AdmissionField("Guardian Name", draft.guardianName, guardianNameError) { viewModel.updateAdmissionDraft(draft.copy(guardianName = it)) }
        AdmissionField("Relationship", draft.guardianRelation, relationError) { viewModel.updateAdmissionDraft(draft.copy(guardianRelation = it)) }
        AdmissionField("Primary Phone", draft.guardianPhone, guardianPhoneError) { viewModel.updateAdmissionDraft(draft.copy(guardianPhone = it)) }
        AdmissionField("Guardian Email", draft.guardianEmail, guardianEmailError) { viewModel.updateAdmissionDraft(draft.copy(guardianEmail = it)) }
        AdmissionField("Emergency Contact Name", draft.emergencyContactName, emergencyNameError) {
            viewModel.updateAdmissionDraft(draft.copy(emergencyContactName = it))
        }
        AdmissionField("Emergency Contact Phone", draft.emergencyContactPhone, emergencyPhoneError) {
            viewModel.updateAdmissionDraft(draft.copy(emergencyContactPhone = it))
        }

        Spacer(Modifier.height(18.dp))
        PrimaryButton("Save & Continue ->", Modifier.fillMaxWidth()) {
            attemptedNext = true
            if (canProceed) {
                onNext()
            }
        }
        Spacer(Modifier.height(10.dp))
        SecondaryButton("Back", Modifier.fillMaxWidth(), onBack)
    }
}

@Composable
fun AdmissionAcademicScreen(viewModel: ImsViewModel, onBack: () -> Unit, onNext: () -> Unit) {
    var attemptedNext by rememberSaveable { mutableStateOf(false) }

    AdmissionFormScaffold("Academic History", "Step 3 of 4", onBack) {
        val draft = viewModel.admissionDraft

        val institutionError = if (attemptedNext && draft.institution.isBlank()) "Institution name is required" else null
        val boardError = if (attemptedNext && draft.board.isBlank()) "Board is required" else null
        val yearError = if (attemptedNext && !isValidGraduationYear(draft.graduationYear)) "Enter a valid year" else null
        val gpaError = if (attemptedNext && !isValidGpa(draft.gpa)) "Enter GPA between 0.0 and 4.0" else null
        val docsError = if (attemptedNext && draft.supportingDocuments.isBlank()) "Upload at least one supporting document" else null
        val declarationError = if (attemptedNext && !draft.academicDeclarationAccepted) "Academic declaration is required" else null
        val canProceed =
            draft.institution.isNotBlank() &&
            draft.board.isNotBlank() &&
            isValidGraduationYear(draft.graduationYear) &&
            isValidGpa(draft.gpa) &&
            draft.supportingDocuments.isNotBlank() &&
            draft.academicDeclarationAccepted

        AdmissionField("Institution Name", draft.institution, institutionError) { viewModel.updateAdmissionDraft(draft.copy(institution = it)) }
        AdmissionField("Examination Board", draft.board, boardError) { viewModel.updateAdmissionDraft(draft.copy(board = it)) }
        AdmissionField("Graduation Year", draft.graduationYear, yearError) { viewModel.updateAdmissionDraft(draft.copy(graduationYear = it)) }
        AdmissionField("GPA / Grade", draft.gpa, gpaError) { viewModel.updateAdmissionDraft(draft.copy(gpa = it)) }
        AdmissionField("Supporting Documents", draft.supportingDocuments, docsError) {
            viewModel.updateAdmissionDraft(draft.copy(supportingDocuments = it))
        }
        AdmissionDeclaration(
            checked = draft.academicDeclarationAccepted,
            error = declarationError
        ) {
            viewModel.updateAdmissionDraft(draft.copy(academicDeclarationAccepted = it))
        }

        Spacer(Modifier.height(18.dp))
        PrimaryButton("Review Application ->", Modifier.fillMaxWidth()) {
            attemptedNext = true
            if (canProceed) {
                onNext()
            }
        }
        Spacer(Modifier.height(10.dp))
        SecondaryButton("Back", Modifier.fillMaxWidth(), onBack)
    }
}

@Composable
fun AdmissionReviewScreen(
    viewModel: ImsViewModel,
    onBack: () -> Unit,
    onSubmit: () -> Unit,
    onEditPersonal: () -> Unit,
    onEditGuardian: () -> Unit,
    onEditAcademic: () -> Unit
) {
    AdmissionFormScaffold("Review Application", "Final Review", onBack) {
        val draft = viewModel.admissionDraft
        ReviewBlock("Personal Profile", listOf(draft.fullName, draft.birthDate, draft.phone, draft.category, draft.address))
        SecondaryButton("Edit Personal Information", Modifier.fillMaxWidth(), onEditPersonal)
        ReviewBlock(
            "Guardianship",
            listOf(
                draft.guardianName,
                draft.guardianRelation,
                draft.guardianPhone,
                draft.guardianEmail,
                "Emergency: ${draft.emergencyContactName} (${draft.emergencyContactPhone})"
            )
        )
        SecondaryButton("Edit Guardian Information", Modifier.fillMaxWidth(), onEditGuardian)
        ReviewBlock(
            "Academic Record",
            listOf(
                draft.institution,
                draft.board,
                "Graduation Year: ${draft.graduationYear}",
                "GPA ${draft.gpa}",
                "Documents: ${draft.supportingDocuments}"
            )
        )
        SecondaryButton("Edit Academic Information", Modifier.fillMaxWidth(), onEditAcademic)
        Spacer(Modifier.height(18.dp))
        PrimaryButton("Submit Final Application", Modifier.fillMaxWidth(), onSubmit)
        Spacer(Modifier.height(10.dp))
        SecondaryButton("Back", Modifier.fillMaxWidth(), onBack)
    }
}

@Composable
fun AdmissionStatusScreen(viewModel: ImsViewModel, onBackHome: () -> Unit) {
    val application = viewModel.selectedAdmission()
        ?: viewModel.admissionApplications.firstOrNull { it.draft.fullName == viewModel.admissionDraft.fullName }
    val currentStatus = application?.status ?: if (viewModel.admissionDraft.submitted) "Pending" else "Draft"
    val statusTitle = when (currentStatus) {
        "Approved" -> "Application Approved"
        "Rejected" -> "Application Rejected"
        "Pending" -> "Application Under Review"
        else -> "Application Submitted"
    }

    AdmissionFormScaffold("Admission Status", "Application submitted", onBackHome, showBack = false) {
        AtelierCard(containerColor = Color(0xFF11120F), borderColor = Color.Transparent) {
            Text("Official Decision", color = AtelierGreen, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))
            Text(
                text = statusTitle,
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White
            )
            Text("Student ID: ${application?.studentId ?: "SC-2024-0089"}", color = Color(0xFFC9C5BD))
        }
        AtelierCard(containerColor = Color.White) {
            TinyLabel("Process Journal", AtelierGreen)
            Spacer(Modifier.height(8.dp))
            StatusRow("Application Submitted", "Oct 10, 2023", AtelierGreen)
            StatusRow("Committee Review", "Oct 12, 2023", AtelierMuted)
            StatusRow("Admission Granted", "Oct 14, 2023", AtelierClay)
        }
        AtelierCard(containerColor = Color.White) {
            TinyLabel("Next Steps", AtelierClay)
            Spacer(Modifier.height(8.dp))
            when (currentStatus) {
                "Approved" -> {
                    Text("1. Submit enrollment deposit", color = AtelierInk, fontWeight = FontWeight.Bold)
                    Text("2. Download offer documents", color = AtelierMuted)
                    Text("3. Complete onboarding details", color = AtelierMuted)
                }

                "Rejected" -> {
                    Text("1. Review rejection notes", color = AtelierInk, fontWeight = FontWeight.Bold)
                    Text("2. Update profile and documents", color = AtelierMuted)
                    Text("3. Reapply in next cycle", color = AtelierMuted)
                }

                else -> {
                    Text("1. Wait for committee review", color = AtelierInk, fontWeight = FontWeight.Bold)
                    Text("2. Keep documents ready", color = AtelierMuted)
                    Text("3. Monitor status updates", color = AtelierMuted)
                }
            }
        }
        Spacer(Modifier.height(18.dp))
        PrimaryButton(
            text = if (viewModel.isAdmin()) "Back to Admissions" else "Back Home",
            modifier = Modifier.fillMaxWidth(),
            onClick = onBackHome
        )
    }
}

@Composable
private fun AdmissionFormScaffold(
    title: String,
    step: String,
    onBack: () -> Unit,
    showBack: Boolean = true,
    content: @Composable ColumnScope.() -> Unit
) {
    Scaffold(
        containerColor = AtelierPaper,
        topBar = {
            AtelierTopBar(
                title = "Student Admission",
                showBack = showBack,
                onBack = onBack,
                trailing = Icons.Outlined.AccountCircle
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(AtelierPaper)
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(22.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            TinyLabel(step, AtelierClay)
            Text(title, style = MaterialTheme.typography.headlineMedium, fontStyle = FontStyle.Italic)
            content()
        }
    }
}

@Composable
private fun AdmissionField(label: String, value: String, error: String?, onChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(label) },
        shape = RoundedCornerShape(8.dp),
        singleLine = label != "Residential Address" && !label.contains("Documents"),
        isError = error != null
    )
    if (error != null) {
        Spacer(Modifier.height(4.dp))
        Text(error, color = AtelierWarning, fontSize = 12.sp)
    }
}

@Composable
private fun ReviewBlock(title: String, rows: List<String>) {
    AtelierCard(containerColor = Color.White) {
        Text(title, color = AtelierInk, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        rows.forEach { row ->
            Text(row, color = AtelierMuted)
            Spacer(Modifier.height(2.dp))
        }
    }
}

@Composable
private fun AdmissionDeclaration(checked: Boolean, error: String?, onChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(checked = checked, onCheckedChange = onChange)
        Text(
            text = "I certify that the information provided is accurate and can be reviewed for admission decisions.",
            color = AtelierMuted,
            fontSize = 12.sp,
            modifier = Modifier.padding(end = 8.dp)
        )
    }
    if (error != null) {
        Text(error, color = AtelierWarning, fontSize = 12.sp)
    }
}

@Composable
private fun PhotoUploadCard(fileName: String) {
    AtelierCard(containerColor = Color.White) {
        TinyLabel("Upload Profile Photo", AtelierClay)
        Spacer(Modifier.height(8.dp))
        Text("Photo file attached: $fileName", color = AtelierInk)
        Text("PNG/JPG, max 2MB", color = AtelierMuted, fontSize = 12.sp)
    }
}

@Composable
private fun StatusRow(title: String, date: String, color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(title, color = AtelierInk)
        Text(date, color = color, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}

private fun isValidPhone(input: String): Boolean {
    val digits = input.filter { it.isDigit() }
    return digits.length >= 10
}

private fun isValidPastDate(input: String): Boolean {
    val regex = Regex("^(0[1-9]|1[0-2])/(0[1-9]|[12]\\d|3[01])/(19|20)\\d{2}$")
    if (!regex.matches(input)) return false
    val year = input.takeLast(4).toIntOrNull() ?: return false
    return year <= 2026
}

private fun isValidEmail(input: String): Boolean {
    return Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$").matches(input)
}

private fun isValidGraduationYear(input: String): Boolean {
    val year = input.toIntOrNull() ?: return false
    return year in 1990..2026
}

private fun isValidGpa(input: String): Boolean {
    val gpa = input.toDoubleOrNull() ?: return false
    return gpa in 0.0..4.0
}
