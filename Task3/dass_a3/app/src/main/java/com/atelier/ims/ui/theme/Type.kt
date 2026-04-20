package com.atelier.ims.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val AtelierTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.Normal,
        fontSize = 42.sp,
        lineHeight = 46.sp,
        color = AtelierInk
    ),
    headlineLarge = TextStyle(
        fontFamily = FontFamily.Serif,
        fontSize = 34.sp,
        lineHeight = 38.sp,
        color = AtelierInk
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.Serif,
        fontSize = 26.sp,
        lineHeight = 31.sp,
        color = AtelierInk
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.Serif,
        fontStyle = FontStyle.Italic,
        fontSize = 22.sp,
        lineHeight = 27.sp,
        color = AtelierInk
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        color = AtelierInk
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        color = AtelierInk
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        color = AtelierMuted
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 10.sp,
        letterSpacing = 1.2.sp,
        color = AtelierMuted
    )
)
