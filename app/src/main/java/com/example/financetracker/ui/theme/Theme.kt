package com.example.financetracker.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(

    primary = Color(0xFF5B8DEF),
    onPrimary = Color.White,

    secondary = Color(0xFF7C8BA1),
    onSecondary = Color.White,

    background = Color(0xFF111827),
    onBackground = Color(0xFFF3F4F6),

    surface = Color(0xFF1A2233),
    onSurface = Color(0xFFF3F4F6),

    surfaceVariant = Color(0xFF253047),
    onSurfaceVariant = Color(0xFFB8C1CC),

    primaryContainer = Color(0xFF2B3D63),
    onPrimaryContainer = Color.White,

    secondaryContainer = Color(0xFF374151),
    onSecondaryContainer = Color.White,

    error = Color(0xFFD96C6C),
    onError = Color.White,

    errorContainer = Color(0xFF5B2C2C),
    onErrorContainer = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun FinanceTrackerTheme(
    darkTheme: Boolean = true,
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