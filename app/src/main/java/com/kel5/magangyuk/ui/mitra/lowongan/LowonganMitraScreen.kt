package com.kel5.magangyuk.ui.mitra.lowongan

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kel5.magangyuk.data.local.entity.JenisLowongan
import com.kel5.magangyuk.data.local.entity.ModeKerja
import com.kel5.magangyuk.data.repository.StatusLowongan
import com.kel5.magangyuk.ui.common.AddFab
import com.kel5.magangyuk.ui.common.ChipTone
import com.kel5.magangyuk.ui.common.ConfirmDialog
import com.kel5.magangyuk.ui.common.ConfirmStyle
import com.kel5.magangyuk.ui.common.EmptyState
import com.kel5.magangyuk.ui.common.InfoTag
import com.kel5.magangyuk.ui.common.SearchBar
import com.kel5.magangyuk.ui.common.StatusChip
import com.kel5.magangyuk.ui.common.TagTone
import com.kel5.magangyuk.ui.theme.ActionRed
import com.kel5.magangyuk.ui.theme.CreamLight
import com.kel5.magangyuk.ui.theme.GrayCream
import com.kel5.magangyuk.ui.theme.Navy
import com.kel5.magangyuk.ui.theme.StatusGrayText
import com.kel5.magangyuk.ui.theme.StatusRedBg
import com.kel5.magangyuk.ui.theme.White
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@Composable
fun LowonganMitraScreen(
    viewModel: LowonganMitraViewModel,
    onTambahClick: () -> Unit,
    onSuntingClick: (Long) -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    var dialogItem by remember { mutableStateOf<LowonganItem?>(null) }

    Box(modifier = Modifier.fillMaxSize().background(CreamLight)) {
        Column(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Lowongan Saya",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Navy
                )
                Text(
                    text = "Kelola lowongan magang perusahaanmu.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = StatusGrayText
                )
                Spacer(modifier = Modifier.height(12.dp))
                SearchBar(
                    query = state.query,
                    onQueryChange = viewModel::onQueryChange,
                    placeholder = "Cari lowongan..."
                )
            }

            if (state.items.isEmpty() && !state.loading) {
                EmptyState(
                    icon = Icons.Default.Info,
                    text = if (state.query.isBlank()) "Belum ada lowongan" else "Lowongan tidak ditemukan"
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 96.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.items, key = { it.lowongan.id }) { item ->
                        LowonganCard(
                            item = item,
                            onSuntingClick = { onSuntingClick(item.lowongan.id) },
                            onAksiClick = { dialogItem = item }
                        )
                    }
                }
            }
        }

        AddFab(
            text = "Tambah Lowongan",
            onClick = onTambahClick,
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
        )
    }

    dialogItem?.let { item ->
        val akanDihapus = item.jumlahPelamar == 0
        ConfirmDialog(
            title = if (akanDihapus) "Hapus Lowongan" else "Tutup Lowongan",
            message = if (akanDihapus) "Yakin hapus lowongan ini?" else "Yakin tutup lowongan ini?",
            style = ConfirmStyle.DESTRUCTIVE,
            onConfirm = {
                viewModel.hapusAtauTutup(item.lowongan) { }
                dialogItem = null
            },
            onDismiss = { dialogItem = null }
        )
    }
}

@Composable
private fun LowonganCard(
    item: LowonganItem,
    onSuntingClick: () -> Unit,
    onAksiClick: () -> Unit
) {
    val l = item.lowongan
    val aktif = item.status == StatusLowongan.AKTIF
    val penuh = item.terisi >= l.kuota

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(verticalAlignment = Alignment.Top) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(CreamLight, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = l.posisi.take(1).uppercase(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Navy
                    )
                }
                Column(modifier = Modifier.weight(1f).padding(horizontal = 12.dp)) {
                    Text(
                        text = l.posisi,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Navy
                    )
                    Text(
                        text = l.divisi,
                        style = MaterialTheme.typography.bodySmall,
                        color = StatusGrayText
                    )
                }
                StatusChip(
                    text = if (aktif) "Aktif" else "Tutup",
                    tone = if (aktif) ChipTone.GREEN else ChipTone.GRAY
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                InfoTag(durasiBulan(l.tglMulai, l.tglSelesai))
                InfoTag(labelMode(l.mode))
                if (l.jenis == JenisLowongan.PAID) InfoTag("Paid", tone = TagTone.PURPLE)
                else InfoTag("Unpaid")
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CreamLight, RoundedCornerShape(10.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row {
                    Text(
                        text = "Kuota ${item.terisi}/${l.kuota}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Navy
                    )
                    if (penuh) {
                        Text(
                            text = " (Penuh)",
                            style = MaterialTheme.typography.bodySmall,
                            color = ActionRed
                        )
                    }
                }
                Text(
                    text = "${item.jumlahPelamar} pelamar",
                    style = MaterialTheme.typography.bodySmall,
                    color = Navy
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AksiButton(
                    text = "Sunting",
                    container = GrayCream,
                    content = Navy,
                    onClick = onSuntingClick,
                    modifier = Modifier.weight(1f)
                )
                if (aktif) {
                    AksiButton(
                        text = if (item.jumlahPelamar == 0) "Hapus" else "Tutup Lowongan",
                        container = StatusRedBg,
                        content = ActionRed,
                        onClick = onAksiClick,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun AksiButton(
    text: String,
    container: Color,
    content: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(50),
        colors = ButtonDefaults.buttonColors(containerColor = container, contentColor = content)
    ) { Text(text) }
}

private fun durasiBulan(mulai: String, selesai: String): String {
    val bulan = runCatching {
        ChronoUnit.MONTHS.between(LocalDate.parse(mulai), LocalDate.parse(selesai).plusDays(1))
    }.getOrDefault(0L)
    return "${maxOf(bulan, 1L)} bulan"
}

private fun labelMode(mode: ModeKerja): String = when (mode) {
    ModeKerja.ONSITE -> "Onsite Only"
    ModeKerja.HYBRID -> "Hybrid"
    ModeKerja.ONLINE -> "Online"
}