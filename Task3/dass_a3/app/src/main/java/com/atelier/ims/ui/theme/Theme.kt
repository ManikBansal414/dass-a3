package com.atelier.ims.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightScheme = lightColorScheme(
    primary = AtelierClay,
    onPrimary = AtelierPanel,
    secondary = AtelierGreen,
    tertiary = AtelierClayDark,
    background = AtelierPaper,
    onBackground = AtelierInk,
    surface = AtelierPanel,
    onSurface = AtelierInk,
    surfaceVariant = AtelierPaper,
    outline = AtelierLine
)

@Composable
fun AtelierTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightScheme,
        typography = AtelierTypography,
        content = content
    )
}
