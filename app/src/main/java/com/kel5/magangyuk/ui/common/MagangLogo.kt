package com.kel5.magangyuk.ui.common

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.kel5.magangyuk.ui.theme.CreamYellow
import com.kel5.magangyuk.ui.theme.PlusJakartaSans

@Composable
fun MagangLogo(
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 36.sp
) {
    Text(
        text = "MagangYuk!",
        modifier = modifier,
        color = CreamYellow,
        fontFamily = PlusJakartaSans,
        fontWeight = FontWeight.Bold,
        fontSize = fontSize,
        letterSpacing = (-0.5).sp
    )
}