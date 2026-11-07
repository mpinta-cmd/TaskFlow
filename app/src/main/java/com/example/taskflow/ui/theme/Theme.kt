package com.example.taskflow.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

enum class TaskFlowPalette(val label: String) {
    OCEAN("Océano"),
    SUNSET("Atardecer")
}

private val OceanColors = lightColorScheme(
    primary = OceanPrimary,
    onPrimary = Color.White,
    primaryContainer = OceanSoft,
    onPrimaryContainer = OceanDeep,
    background = OceanBackground,
    onBackground = Ink,
    surface = Color.White,
    onSurface = Ink,
    surfaceVariant = OceanSoft,
    onSurfaceVariant = Muted,
    outline = SoftBorder
)

private val SunsetColors = lightColorScheme(
    primary = SunsetPrimary,
    onPrimary = Color.White,
    primaryContainer = SunsetSoft,
    onPrimaryContainer = SunsetDeep,
    background = SunsetBackground,
    onBackground = Ink,
    surface = Color.White,
    onSurface = Ink,
    surfaceVariant = SunsetSoft,
    onSurfaceVariant = Muted,
    outline = SoftBorder
)

@Composable
fun TaskFlowTheme(
    palette: TaskFlowPalette = TaskFlowPalette.OCEAN,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = when (palette) {
            TaskFlowPalette.OCEAN -> OceanColors
            TaskFlowPalette.SUNSET -> SunsetColors
        },
        typography = TaskTypography,
        content = content
    )
}
