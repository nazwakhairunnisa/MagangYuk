package com.kel5.magangyuk.ui.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.kel5.magangyuk.ui.theme.*

// Gaya tombol konfirmasi: warna latar + warna teksnya.
enum class ConfirmStyle(val container: Color, val content: Color) {
    DEFAULT(Navy, CreamYellow),        // kirim lamaran, simpan, dst.
    POSITIVE(ActionGreen, White),      // Terima, Setujui
    DESTRUCTIVE(ActionRed, White)      // Tolak, Revisi, Hapus
}

@Composable
fun ConfirmDialog(
    title: String,
    message: String,                    // contoh: "Yakin kirim lamaran?"
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    confirmText: String = "Ya",
    dismissText: String = "Batal",
    style: ConfirmStyle = ConfirmStyle.DEFAULT
) {
    // AlertDialog hanya muncul kalau dipanggil. Yang mengatur tampil/tidaknya
    // adalah layar pemanggil (lewat state Boolean), bukan komponen ini.
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = White,
        title = { Text(title, style = MaterialTheme.typography.titleLarge, color = Navy) },
        text = { Text(message, style = MaterialTheme.typography.bodyMedium, color = Navy) },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = style.container,
                    contentColor = style.content
                )
            ) { Text(confirmText) }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                border = BorderStroke(1.dp, StatusGrayText),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = StatusGrayText)
            ) { Text(dismissText) }
        }
    )
}