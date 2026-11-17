package com.kel5.magangyuk.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kel5.magangyuk.ui.theme.*

enum class TagTone(val background: Color) {
    PURPLE(LightPurple),   // Paid, pill jam logbook
    NEUTRAL(GrayCream)     // durasi, mode kerja
}

@Composable
fun InfoTag(
    text: String,
    modifier: Modifier = Modifier,
    tone: TagTone = TagTone.NEUTRAL
) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        color = Navy,
        modifier = modifier
            .background(tone.background, RoundedCornerShape(50.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    )
}

@Preview(showBackground = true)
@Composable
private fun InfoTagPreview() {
    MagangYukTheme {
        Row(modifier = Modifier.padding(16.dp)) {
            InfoTag("Paid", tone = TagTone.PURPLE)
            InfoTag("3 bulan", modifier = Modifier.padding(start = 8.dp))
            InfoTag("Hybrid", modifier = Modifier.padding(start = 8.dp))
        }
    }
}