package com.kel5.magangyuk.ui.notifikasi

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kel5.magangyuk.data.local.entity.NotifikasiEntity
import com.kel5.magangyuk.data.local.entity.TipeNotifikasi
import com.kel5.magangyuk.ui.common.EmptyState
import com.kel5.magangyuk.ui.theme.CreamLight
import com.kel5.magangyuk.ui.theme.MagangYukTheme
import com.kel5.magangyuk.ui.theme.Navy
import com.kel5.magangyuk.ui.theme.StatusGrayText

@Composable
fun NotifikasiScreen(
    state: NotifikasiUiState,
    onItemClick: (NotifikasiEntity) -> Unit,
    onMarkAllRead: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CreamLight)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 8.dp, top = 16.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Notifikasi",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Navy
                )
                Text(
                    text = "Kabar terbaru untuk akunmu.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = StatusGrayText
                )
            }
            TextButton(
                onClick = onMarkAllRead,
                enabled = state.unreadCount > 0
            ) {
                Text(
                    text = "Tandai semua dibaca",
                    color = if (state.unreadCount > 0) Navy else StatusGrayText
                )
            }
        }

        if (state.items.isEmpty()) {
            EmptyState(
                icon = Icons.Default.Notifications,
                text = "Belum ada notifikasi",
                modifier = Modifier.weight(1f)
            )
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.items, key = { it.id }) { item ->
                    NotifikasiCard(
                        item = item,
                        onClick = { onItemClick(item) }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun NotifikasiScreenPreview() {
    val sekarang = System.currentTimeMillis()
    MagangYukTheme {
        NotifikasiScreen(
            state = NotifikasiUiState(
                items = listOf(
                    NotifikasiEntity(
                        id = 1,
                        userId = 1,
                        tipe = TipeNotifikasi.LAMARAN_BARU,
                        judul = "Pelamar baru",
                        pesan = "Budi melamar posisi Android Developer.",
                        refId = 1,
                        dibaca = false,
                        tglBuat = sekarang - 5 * 60_000
                    ),
                    NotifikasiEntity(
                        id = 2,
                        userId = 1,
                        tipe = TipeNotifikasi.LAPORAN_DIUNGGAH,
                        judul = "Laporan diunggah",
                        pesan = "Budi mengunggah laporan akhir.",
                        refId = 1,
                        dibaca = true,
                        tglBuat = sekarang - 3 * 3_600_000
                    )
                ),
                unreadCount = 1
            ),
            onItemClick = {},
            onMarkAllRead = {}
        )
    }
}