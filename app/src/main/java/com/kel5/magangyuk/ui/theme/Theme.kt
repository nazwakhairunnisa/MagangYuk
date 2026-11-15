package com.kel5.magangyuk.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext

//private val DarkColorScheme = darkColorScheme(
//    primary = Purple80,
//    secondary = PurpleGrey80,
//    tertiary = Pink80
//)

private val MagangYukColorScheme = lightColorScheme(
    primary = Navy,
    onPrimary = CreamYellow,
    secondary = CreamYellow,
    onSecondary = Navy,
    tertiary = LightPurple,
    onTertiary = Navy,
    background = CreamLight,
    onBackground = Navy,
    surface = White,
    onSurface = Navy,
    surfaceVariant = GrayCream,
    onSurfaceVariant = Navy,
    error = ActionRed,
    onError = White
)

@Composable
fun MagangYukTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = MagangYukColorScheme,
        typography = Typography,
        content = content
    )
}