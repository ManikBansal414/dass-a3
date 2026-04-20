package com.atelier.ims.ui.profile

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
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.atelier.ims.ImsViewModel
import com.atelier.ims.ui.components.AtelierBottomBar
import com.atelier.ims.ui.components.AtelierCard
import com.atelier.ims.ui.components.AtelierTopBar
import com.atelier.ims.ui.components.Avatar
import com.atelier.ims.ui.components.PrimaryButton
import com.atelier.ims.ui.components.SectionLabel
import com.atelier.ims.ui.components.TinyLabel
import com.atelier.ims.ui.theme.AtelierClay
import com.atelier.ims.ui.theme.AtelierGreen
import com.atelier.ims.ui.theme.AtelierInk
import com.atelier.ims.ui.theme.AtelierMuted
import com.atelier.ims.ui.theme.AtelierPaper

@Composable
fun ProfileScreen(
    viewModel: ImsViewModel,
    onHome: () -> Unit,
    onSchedule: () -> Unit,
    onAlerts: () -> Unit
) {
    val isAdmin = viewModel.isAdmin()
    Scaffold(
        containerColor = AtelierPaper,
        topBar = {
            AtelierTopBar(
                title = if (isAdmin) "System Settings" else "Scholar Profile",
                showBack = true,
                onBack = onHome,
                trailing = Icons.Outlined.Person
            )
        },
        bottomBar = {
            AtelierBottomBar(
                selected = "profile",
                onHome = onHome,
                onSchedule = onSchedule,
                showSchedule = !isAdmin,
                showAlerts = isAdmin,
                onAlerts = onAlerts,
                onProfile = {}
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
            if (isAdmin) {
                AdminControlCenter(viewModel)
            } else {
                StudentProfileSettings(viewModel)
            }
        }
    }
}

@Composable
private fun StudentProfileSettings(viewModel: ImsViewModel) {
    var language by remember { mutableStateOf(viewModel.adminConfigState.value.language) }
    var timezone by remember { mutableStateOf(viewModel.adminConfigState.value.timezone) }

    Avatar(text = viewModel.student.avatarText)
    Text(viewModel.student.name, style = MaterialTheme.typography.headlineMedium)
    AtelierCard(containerColor = Color.White, modifier = Modifier.fillMaxWidth()) {
        ProfileLine("Role", "Scholar")
        ProfileLine("Program", viewModel.student.program)
        ProfileLine("Cohort", viewModel.student.cohort)
    }

    SectionLabel("Basic Settings")
    AtelierCard(containerColor = Color.White) {
        OutlinedTextField(
            value = language,
            onValueChange = { language = it },
            label = { Text("Language") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(Modifier.height(10.dp))
        OutlinedTextField(
            value = timezone,
            onValueChange = { timezone = it },
            label = { Text("Time Zone") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(Modifier.height(12.dp))
        PrimaryButton("Save Preferences", Modifier.fillMaxWidth()) {
            viewModel.updateStudentPreferences(language, timezone)
        }
    }

    SectionLabel("Announcements")
    viewModel.studentAlerts.forEach { alert ->
        AtelierCard(containerColor = Color.White) {
            TinyLabel("Latest")
            Spacer(Modifier.height(6.dp))
            Text(alert, color = AtelierInk, fontSize = 13.sp)
        }
    }
}

@Composable
private fun AdminControlCenter(viewModel: ImsViewModel) {
    val config = viewModel.adminConfigState.value

    var country by remember { mutableStateOf(config.country) }
    var currency by remember { mutableStateOf(config.currency) }
    var timezone by remember { mutableStateOf(config.timezone) }
    var language by remember { mutableStateOf(config.language) }
    var autoUniqueId by remember { mutableStateOf(config.autoUniqueId) }
    var smsAlerts by remember { mutableStateOf(config.smsAlertsEnabled) }
    var graduationRule by remember { mutableStateOf(config.graduationRule) }
    var studentCategoryRule by remember { mutableStateOf(config.studentCategoryRule) }

    var newCourse by remember { mutableStateOf("") }
    var newCourseBatch by remember { mutableStateOf("") }
    var newElectiveCount by remember { mutableStateOf("2") }

    var newSubject by remember { mutableStateOf("") }
    var newSubjectWeeklyLimit by remember { mutableStateOf("3") }
    var newSubjectElective by remember { mutableStateOf(false) }

    var newBatch by remember { mutableStateOf("") }
    var newBatchIntake by remember { mutableStateOf("60") }
    var newBatchTransfer by remember { mutableStateOf(true) }

    Text("Preference Control", style = MaterialTheme.typography.headlineMedium)
    AtelierCard(containerColor = Color(0xFF5147E6), borderColor = Color(0xFF5147E6)) {
        TinyLabel("System Banner", Color(0xFFD7D3FF))
        Spacer(Modifier.height(8.dp))
        Text("Full administrative control is enabled for this role.", color = Color.White)
    }

    SectionLabel("General Settings")
    AtelierCard(containerColor = Color.White) {
        OutlinedTextField(country, { country = it }, label = { Text("Country") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(currency, { currency = it }, label = { Text("Currency") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(timezone, { timezone = it }, label = { Text("Time Zone") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(language, { language = it }, label = { Text("Language") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        Spacer(Modifier.height(10.dp))
        ToggleLine("Auto Unique ID", autoUniqueId) { autoUniqueId = it }
        ToggleLine("SMS Alerts", smsAlerts) { smsAlerts = it }
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(graduationRule, { graduationRule = it }, label = { Text("Graduation Rules") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(studentCategoryRule, { studentCategoryRule = it }, label = { Text("Student Categories") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(12.dp))
        PrimaryButton("Save All Changes", Modifier.fillMaxWidth()) {
            viewModel.updateAdminConfig(
                country = country,
                currency = currency,
                timezone = timezone,
                language = language,
                autoUniqueId = autoUniqueId,
                smsAlertsEnabled = smsAlerts,
                graduationRule = graduationRule,
                studentCategoryRule = studentCategoryRule
            )
        }
    }

    SectionLabel("Courses")
    AtelierCard(containerColor = Color.White) {
        viewModel.courses.forEach { course ->
            Text("${course.name} | ${course.batch} | electives ${course.electiveCount}", color = AtelierInk, fontSize = 13.sp)
            Spacer(Modifier.height(4.dp))
        }
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(newCourse, { newCourse = it }, label = { Text("Course Name") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(newCourseBatch, { newCourseBatch = it }, label = { Text("Batch") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(newElectiveCount, { newElectiveCount = it.filter(Char::isDigit) }, label = { Text("Elective Count") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        Spacer(Modifier.height(10.dp))
        PrimaryButton("Add Course", Modifier.fillMaxWidth()) {
            viewModel.addCourse(newCourse, newCourseBatch, newElectiveCount.toIntOrNull() ?: 0)
            newCourse = ""
            newCourseBatch = ""
        }
    }

    SectionLabel("Subjects")
    AtelierCard(containerColor = Color.White) {
        viewModel.subjects.forEach { subject ->
            val type = if (subject.isElective) "Elective" else "Core"
            Text("${subject.name} | $type | limit ${subject.weeklyLimit}/week", color = AtelierInk, fontSize = 13.sp)
            Spacer(Modifier.height(4.dp))
        }
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(newSubject, { newSubject = it }, label = { Text("Subject Name") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(newSubjectWeeklyLimit, { newSubjectWeeklyLimit = it.filter(Char::isDigit) }, label = { Text("Weekly Subject Limit") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        Spacer(Modifier.height(6.dp))
        ToggleLine("Is Elective", newSubjectElective) { newSubjectElective = it }
        Spacer(Modifier.height(10.dp))
        PrimaryButton("Add Subject", Modifier.fillMaxWidth()) {
            viewModel.addSubject(newSubject, newSubjectElective, newSubjectWeeklyLimit.toIntOrNull() ?: 1)
            newSubject = ""
        }
    }

    SectionLabel("Batches & Transfers")
    AtelierCard(containerColor = Color.White) {
        viewModel.batches.forEach { batch ->
            val transfer = if (batch.transferEnabled) "Transfer ON" else "Transfer OFF"
            Text("${batch.name} | intake ${batch.intake} | $transfer", color = AtelierInk, fontSize = 13.sp)
            Spacer(Modifier.height(4.dp))
        }
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(newBatch, { newBatch = it }, label = { Text("Batch Name") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(newBatchIntake, { newBatchIntake = it.filter(Char::isDigit) }, label = { Text("Intake") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        Spacer(Modifier.height(6.dp))
        ToggleLine("Enable Transfer", newBatchTransfer) { newBatchTransfer = it }
        Spacer(Modifier.height(10.dp))
        PrimaryButton("Add Batch", Modifier.fillMaxWidth()) {
            viewModel.addBatch(newBatch, newBatchIntake.toIntOrNull() ?: 1, newBatchTransfer)
            newBatch = ""
        }
    }

    AtelierCard(containerColor = Color(0xFFF5EDEF)) {
        TinyLabel("Registry Health", AtelierClay)
        Spacer(Modifier.height(8.dp))
        Text("Auto ID and grading systems are active.", color = AtelierInk)
        Text("SMS module status: ${if (smsAlerts) "Enabled" else "Disabled"}", color = AtelierGreen, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun ToggleLine(label: String, value: Boolean, onChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = AtelierInk)
        Switch(checked = value, onCheckedChange = onChange)
    }
}

@Composable
private fun ProfileLine(label: String, value: String) {
    Text(label.uppercase(), color = AtelierMuted, fontWeight = FontWeight.Bold)
    Text(value, color = AtelierInk)
    Spacer(Modifier.height(8.dp))
}
