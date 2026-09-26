package com.shujaa.ops.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColors = darkColorScheme(
    primary = androidx.compose.ui.graphics.Color(0xFF7BC8FF),
    secondary = androidx.compose.ui.graphics.Color(0xFF6A9CFF),
    tertiary = androidx.compose.ui.graphics.Color(0xFFBEE3FF),
    background = androidx.compose.ui.graphics.Color(0xFF101820),
    surface = androidx.compose.ui.graphics.Color(0xFF17212B),
    onPrimary = androidx.compose.ui.graphics.Color(0xFF08151C),
    onSecondary = androidx.compose.ui.graphics.Color(0xFF08151C),
    onBackground = androidx.compose.ui.graphics.Color(0xFFEAF4FF),
    onSurface = androidx.compose.ui.graphics.Color(0xFFEAF4FF)
)

private val LightColors = lightColorScheme(
    primary = androidx.compose.ui.graphics.Color(0xFF0B5D91),
    secondary = androidx.compose.ui.graphics.Color(0xFF2B6CB0),
    tertiary = androidx.compose.ui.graphics.Color(0xFFB6D9ED),
    background = androidx.compose.ui.graphics.Color(0xFFF3F7FB),
    surface = androidx.compose.ui.graphics.Color(0xFFFFFFFF),
    onPrimary = androidx.compose.ui.graphics.Color(0xFFFFFFFF),
    onSecondary = androidx.compose.ui.graphics.Color(0xFFFFFFFF),
    onBackground = androidx.compose.ui.graphics.Color(0xFF10212A),
    onSurface = androidx.compose.ui.graphics.Color(0xFF10212A)
)

@Composable
fun ShujaaOpsTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColors,
        content = content
    )
}
