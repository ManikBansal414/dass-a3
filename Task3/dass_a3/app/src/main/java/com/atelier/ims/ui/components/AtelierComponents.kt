package com.atelier.ims.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.atelier.ims.ui.theme.AtelierCharcoal
import com.atelier.ims.ui.theme.AtelierClay
import com.atelier.ims.ui.theme.AtelierGreen
import com.atelier.ims.ui.theme.AtelierInk
import com.atelier.ims.ui.theme.AtelierLine
import com.atelier.ims.ui.theme.AtelierMuted
import com.atelier.ims.ui.theme.AtelierPanel
import com.atelier.ims.ui.theme.AtelierPaper

@Composable
fun AtelierTopBar(
    title: String = "IMS",
    showLeadingIcon: Boolean = true,
    showBack: Boolean = false,
    onBack: () -> Unit = {},
    onMenu: () -> Unit = {},
    onSearch: () -> Unit = {},
    trailing: ImageVector? = Icons.Outlined.Notifications
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(AtelierPaper)
            .statusBarsPadding()
            .padding(horizontal = 18.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (showLeadingIcon) {
            IconButton(onClick = { if (showBack) onBack() else onMenu() }) {
                Icon(
                    imageVector = if (showBack) Icons.Outlined.ArrowBack else Icons.Outlined.Menu,
                    contentDescription = null,
                    tint = AtelierInk
                )
            }
        } else {
            Spacer(Modifier.width(0.dp))
        }
        Text(
            text = title,
            modifier = Modifier.weight(1f),
            fontFamily = FontFamily.Serif,
            fontStyle = FontStyle.Italic,
            fontSize = 18.sp,
            color = AtelierClay
        )
        if (trailing != null) {
            IconButton(onClick = onSearch) {
                Icon(imageVector = trailing, contentDescription = null, tint = AtelierInk)
            }
        } else {
            Spacer(Modifier.size(48.dp))
        }
    }
}

@Composable
fun AtelierBottomBar(
    selected: String,
    onHome: () -> Unit,
    onSchedule: () -> Unit,
    showSchedule: Boolean = true,
    showAlerts: Boolean = true,
    onAlerts: () -> Unit = {},
    onProfile: () -> Unit = {}
) {
    NavigationBar(
        modifier = Modifier.navigationBarsPadding(),
        containerColor = AtelierPanel,
        tonalElevation = 0.dp
    ) {
        BottomItem(
            label = "HOME",
            selected = selected == "home",
            icon = Icons.Outlined.Home,
            modifier = Modifier.weight(1f),
            onClick = onHome
        )
        if (showSchedule) {
            BottomItem(
                label = "SCHEDULE",
                selected = selected == "schedule",
                icon = Icons.Outlined.CalendarMonth,
                modifier = Modifier.weight(1f),
                onClick = onSchedule
            )
        }
        if (showAlerts) {
            BottomItem(
                label = "ALERTS",
                selected = selected == "alerts",
                icon = Icons.Outlined.Notifications,
                modifier = Modifier.weight(1f),
                onClick = onAlerts
            )
        }
        BottomItem(
            label = "PROFILE",
            selected = selected == "profile",
            icon = Icons.Outlined.Person,
            modifier = Modifier.weight(1f),
            onClick = onProfile
        )
    }
}

@Composable
private fun BottomItem(
    label: String,
    selected: Boolean,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val tint = if (selected) AtelierClay else AtelierMuted

    Column(
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 4.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(icon, contentDescription = label, tint = tint, modifier = Modifier.size(22.dp))
        Spacer(Modifier.height(2.dp))
        Text(
            text = label,
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold,
            color = tint,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun SectionLabel(title: String, meta: String? = null) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = AtelierInk
        )
        if (meta != null) {
            Text(
                text = meta.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = AtelierMuted
            )
        }
    }
}

@Composable
fun AtelierCard(
    modifier: Modifier = Modifier,
    containerColor: Color = AtelierPanel,
    borderColor: Color = AtelierLine,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = BorderStroke(1.dp, borderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp), content = content)
    }
}

@Composable
fun SearchPill(text: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(AtelierPanel)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Outlined.Search, contentDescription = null, tint = AtelierMuted, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(10.dp))
        Text(text, color = AtelierMuted, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
fun TinyLabel(text: String, color: Color = AtelierMuted) {
    Text(
        text = text.uppercase(),
        color = color,
        fontWeight = FontWeight.Bold,
        fontSize = 10.sp,
        letterSpacing = 1.1.sp
    )
}

@Composable
fun Avatar(text: String, modifier: Modifier = Modifier, color: Color = AtelierPaper) {
    Box(
        modifier = modifier
            .size(42.dp)
            .clip(CircleShape)
            .background(color),
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, color = AtelierClay, fontWeight = FontWeight.Bold, fontSize = 13.sp)
    }
}

@Composable
fun PrimaryButton(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(AtelierClay)
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
    }
}

@Composable
fun SecondaryButton(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFFE9E6DE))
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text, color = AtelierInk, fontWeight = FontWeight.Bold, fontSize = 14.sp)
    }
}

@Composable
fun AtelierFab(onClick: () -> Unit) {
    FloatingActionButton(
        onClick = onClick,
        containerColor = AtelierClay,
        contentColor = Color.White,
        shape = CircleShape
    ) {
        Icon(Icons.Outlined.Add, contentDescription = "Add")
    }
}

@Composable
fun StatusDot(color: Color = AtelierGreen) {
    Box(
        modifier = Modifier
            .size(8.dp)
            .clip(CircleShape)
            .background(color)
    )
}

@Composable
fun DarkNotice(
    label: String,
    title: String,
    body: String,
    action: String,
    onClick: () -> Unit
) {
    AtelierCard(
        containerColor = AtelierCharcoal,
        borderColor = AtelierCharcoal
    ) {
        TinyLabel(label, Color(0xFFD96654))
        Spacer(Modifier.height(12.dp))
        Text(title, style = MaterialTheme.typography.headlineMedium, color = Color.White)
        Spacer(Modifier.height(10.dp))
        Text(body, color = Color(0xFFC9C5BD), fontSize = 13.sp, lineHeight = 19.sp)
        Spacer(Modifier.height(16.dp))
        PrimaryButton(text = action, modifier = Modifier.fillMaxWidth(), onClick = onClick)
    }
}

@Composable
fun SettingsGlyph() {
    Icon(Icons.Outlined.Settings, contentDescription = null, tint = AtelierMuted)
}
