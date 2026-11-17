package com.kel5.magangyuk.ui.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kel5.magangyuk.ui.theme.*

enum class AppButtonStyle { PRIMARY, POSITIVE, DESTRUCTIVE, DESTRUCTIVE_OUTLINE, NEUTRAL_OUTLINE }

@Composable
fun AppButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: AppButtonStyle = AppButtonStyle.PRIMARY,
    enabled: Boolean = true
) {
    val shape = RoundedCornerShape(50)   // pill bulat penuh sesuai Figma

    when (style) {
        // Tombol berisi penuh
        AppButtonStyle.PRIMARY, AppButtonStyle.POSITIVE, AppButtonStyle.DESTRUCTIVE -> {
            val container: Color
            val content: Color
            when (style) {
                AppButtonStyle.POSITIVE -> { container = ActionGreen; content = White }
                AppButtonStyle.DESTRUCTIVE -> { container = ActionRed; content = White }
                else -> { container = Navy; content = CreamYellow }
            }
            Button(
                onClick = onClick,
                modifier = modifier,
                enabled = enabled,
                shape = shape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = container,
                    contentColor = content,
                    disabledContainerColor = GrayCream,
                    disabledContentColor = StatusGrayText
                )
            ) { Text(text) }
        }
        // Tombol outline (garis tepi saja)
        else -> {
            val color = if (style == AppButtonStyle.DESTRUCTIVE_OUTLINE) ActionRed else StatusGrayText
            OutlinedButton(
                onClick = onClick,
                modifier = modifier,
                enabled = enabled,
                shape = shape,
                border = BorderStroke(1.dp, color),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = color)
            ) { Text(text) }
        }
    }
}

// FAB krem untuk "Tambah Logbook" dan "Tambah Lowongan"
@Composable
fun AddFab(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ExtendedFloatingActionButton(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(50),
        containerColor = CreamYellow,
        contentColor = Navy,
        icon = { Icon(Icons.Default.Add, contentDescription = null) },
        text = { Text(text) }
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFF6F3EC)
@Composable
private fun AppButtonPreview() {
    MagangYukTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AppButton("Ajukan Lamaran", {})
            AppButton("Terima Tawaran", {}, style = AppButtonStyle.POSITIVE)
            AppButton("Tolak", {}, style = AppButtonStyle.DESTRUCTIVE)
            AppButton("Tolak Tawaran", {}, style = AppButtonStyle.DESTRUCTIVE_OUTLINE)
            AppButton("Batal", {}, style = AppButtonStyle.NEUTRAL_OUTLINE)
            AppButton("Sudah Dilamar", {}, enabled = false)
            AddFab("Tambah Logbook", {})
        }
    }
}