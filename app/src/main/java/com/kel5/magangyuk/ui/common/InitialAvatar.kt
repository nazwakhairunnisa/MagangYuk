package com.kel5.magangyuk.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kel5.magangyuk.ui.theme.*

// "PT Abadi Solusi Kreatif" -> "AS", "Aulia Rahma" -> "AR", "Budi" -> "B"
fun initialsOf(name: String): String =
    name.trim()
        .split(" ")
        .filter { it.isNotBlank() && !it.equals("PT", ignoreCase = true) }
        .take(2)
        .map { it.first().uppercaseChar() }
        .joinToString("")

@Composable
fun InitialAvatar(
    name: String,
    modifier: Modifier = Modifier,
    highlighted: Boolean = true,
    size: Dp = 40.dp
) {
    Box(
        modifier = modifier
            .size(size)
            .background(if (highlighted) CreamYellow else GrayCream, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initialsOf(name),
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = Navy
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF6F3EC)
@Composable
private fun InitialAvatarPreview() {
    MagangYukTheme {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            InitialAvatar("PT Sejahtera Digital Nusantara")
            InitialAvatar("PT Abadi Solusi Kreatif")
            InitialAvatar("PT Nusantara Teknologi Medika", highlighted = false)
            InitialAvatar("Aulia Rahma")
        }
    }
}