package com.kel5.magangyuk.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kel5.magangyuk.ui.common.EmptyState

// SEMENTARA: pengganti layar yang belum dibuat.
// Hapus pemakaiannya satu per satu saat fitur aslinya selesai.
@Composable
fun PlaceholderScreen(title: String, modifier: Modifier = Modifier) {
    EmptyState(
        icon = Icons.Default.Build,
        text = "$title belum dibuat",
        modifier = modifier.fillMaxSize()
    )
}