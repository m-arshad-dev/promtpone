package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = CampusNavyPrimaryDark,
    onPrimary = CampusNavyOnPrimaryDark,
    primaryContainer = CampusNavyPrimaryContainerDark,
    onPrimaryContainer = CampusNavyOnPrimaryContainerDark,
    secondary = CampusGoldSecondaryDark,
    onSecondary = CampusGoldOnSecondaryDark,
    secondaryContainer = CampusGoldSecondaryContainerDark,
    onSecondaryContainer = CampusGoldOnSecondaryContainerDark,
    tertiary = CampusTealTertiaryDark,
    onTertiary = CampusTealOnTertiaryDark,
    tertiaryContainer = CampusTealTertiaryContainerDark,
    onTertiaryContainer = CampusTealOnTertiaryContainerDark,
    background = CampusBackgroundDark,
    onBackground = CampusOnBackgroundDark,
    surface = CampusSurfaceDark,
    onSurface = CampusOnSurfaceDark,
    surfaceVariant = CampusSurfaceVariantDark,
    onSurfaceVariant = CampusOnSurfaceVariantDark
)

private val LightColorScheme = lightColorScheme(
    primary = CampusNavyPrimary,
    onPrimary = CampusNavyOnPrimary,
    primaryContainer = CampusNavyPrimaryContainer,
    onPrimaryContainer = CampusNavyOnPrimaryContainer,
    secondary = CampusGoldSecondary,
    onSecondary = CampusGoldOnSecondary,
    secondaryContainer = CampusGoldSecondaryContainer,
    onSecondaryContainer = CampusGoldOnSecondaryContainer,
    tertiary = CampusTealTertiary,
    onTertiary = CampusTealOnTertiary,
    tertiaryContainer = CampusTealTertiaryContainer,
    onTertiaryContainer = CampusTealOnTertiaryContainer,
    background = CampusBackgroundLight,
    onBackground = CampusOnBackgroundLight,
    surface = CampusSurfaceLight,
    onSurface = CampusOnSurfaceLight,
    surfaceVariant = CampusSurfaceVariantLight,
    onSurfaceVariant = CampusOnSurfaceVariantLight
)

@Composable
fun CampusCompanionTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    CampusCompanionTheme(darkTheme = darkTheme, dynamicColor = dynamicColor, content = content)
}
