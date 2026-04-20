package com.atelier.ims.ui.login

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AlternateEmail
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.atelier.ims.ImsViewModel
import com.atelier.ims.ui.components.PrimaryButton
import com.atelier.ims.ui.components.TinyLabel
import com.atelier.ims.ui.theme.AtelierClay
import com.atelier.ims.ui.theme.AtelierInk
import com.atelier.ims.ui.theme.AtelierLine
import com.atelier.ims.ui.theme.AtelierMuted
import com.atelier.ims.ui.theme.AtelierPanel
import com.atelier.ims.ui.theme.AtelierPaper
import com.atelier.ims.ui.theme.AtelierWarning

@Composable
fun LoginScreen(
    viewModel: ImsViewModel,
    onLoggedIn: (String) -> Unit
) {
    var email by rememberSaveable { mutableStateOf("scholar@atelier.edu") }
    var password by rememberSaveable { mutableStateOf("atelier123") }

    Scaffold(containerColor = AtelierPaper) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(AtelierPaper)
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 26.dp, vertical = 34.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Outlined.MenuBook, contentDescription = null, tint = AtelierClay)
                Text(
                    text = " IMS",
                    fontFamily = FontFamily.Serif,
                    fontStyle = FontStyle.Italic,
                    fontSize = 19.sp,
                    color = AtelierInk
                )
            }
            Spacer(Modifier.height(28.dp))
            Text(
                text = "Welcome to IMS Dashboard",
                style = MaterialTheme.typography.headlineLarge,
                fontStyle = FontStyle.Italic
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Enter your CAS details in this application",
                color = AtelierMuted,
                fontSize = 15.sp
            )
            Spacer(Modifier.height(32.dp))
            RoleSwitch(
                selected = if (viewModel.selectedRole == "Admin") "Admin" else "Student",
                onSelected = { role ->
                    val mappedRole = if (role == "Student") "Scholar" else "Admin"
                    viewModel.selectRole(mappedRole)
                    if (role == "Admin") {
                        email = "admin@atelier.edu"
                        password = "admin123"
                    } else {
                        email = "scholar@atelier.edu"
                        password = "atelier123"
                    }
                }
            )
            Spacer(Modifier.height(24.dp))
            viewModel.loginError?.let { message ->
                ErrorBanner(message = message)
                Spacer(Modifier.height(18.dp))
            }
            Row(modifier = Modifier.fillMaxWidth()) {
            TinyLabel("Academic Email")
            }
            Spacer(Modifier.height(8.dp))
            LoginField(
                value = email,
                onValueChange = { email = it },
                placeholder = "scholar@atelier.edu",
                icon = Icons.Outlined.AlternateEmail,
                keyboardType = KeyboardType.Email
            )
            Spacer(Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                TinyLabel("Password")
            }
            Spacer(Modifier.height(8.dp))
            LoginField(
                value = password,
                onValueChange = { password = it },
                placeholder = "Password",
                icon = Icons.Outlined.Lock,
                isPassword = true
            )
            Spacer(Modifier.height(28.dp))
            PrimaryButton(
                text = "Login",
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    if (viewModel.login(email, password)) onLoggedIn(viewModel.selectedRole)
                }
            )
            Spacer(Modifier.height(20.dp))
        }
    }
}

@Composable
private fun RoleSwitch(
    selected: String,
    onSelected: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(Color(0xFFE7E3DA))
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        listOf("Student", "Admin").forEach { role ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(24.dp))
                    .background(if (selected == role) Color.White else Color.Transparent)
                    .clickable { onSelected(role) }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = role,
                    color = AtelierInk,
                    fontWeight = if (selected == role) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}

@Composable
private fun LoginField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text(placeholder) },
        leadingIcon = { Icon(icon, contentDescription = null) },
        singleLine = true,
        shape = RoundedCornerShape(8.dp),
        visualTransformation = if (isPassword) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = AtelierClay,
            unfocusedBorderColor = AtelierLine,
            focusedContainerColor = AtelierPanel,
            unfocusedContainerColor = AtelierPanel
        )
    )
}

@Composable
private fun ErrorBanner(message: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(Color(0xFFF4E7E3))
            .padding(horizontal = 18.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(AtelierWarning)
        )
        Text(
            text = "  $message",
            color = AtelierWarning,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
