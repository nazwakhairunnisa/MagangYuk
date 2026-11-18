package com.kel5.magangyuk.ui.common

import androidx.compose.foundation.background
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

enum class ChipTone(val background: Color, val content: Color) {
    YELLOW(StatusYellowBg, StatusYellowText),
    GREEN(StatusGreenBg, StatusGreenText),
    RED(StatusRedBg, StatusRedText),
    GRAY(StatusGrayBg, StatusGrayText),
    BLUE(StatusBlueBg, StatusBlueText)
}

@Composable
fun StatusChip(
    text: String,
    tone: ChipTone,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        color = tone.content,
        modifier = modifier
            // urutan penting: background dulu, baru padding,
            // supaya warna latar ikut menutupi area padding
            .background(tone.background, RoundedCornerShape(50))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    )
}

@Preview(showBackground = true)
@Composable
private fun StatusChipPreview() {
    MagangYukTheme {
        androidx.compose.foundation.layout.Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)
        ) {
            StatusChip("Menunggu", ChipTone.YELLOW)
            StatusChip("Diterima", ChipTone.GREEN)
            StatusChip("Ditolak", ChipTone.RED)
            StatusChip("Kedaluwarsa", ChipTone.GRAY)
            StatusChip("Aktif Berjalan", ChipTone.BLUE)
        }
    }
}