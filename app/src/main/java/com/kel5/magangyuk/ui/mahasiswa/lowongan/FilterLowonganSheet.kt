package com.kel5.magangyuk.ui.mahasiswa.lowongan

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kel5.magangyuk.data.local.entity.JenisLowongan
import com.kel5.magangyuk.data.local.entity.ModeKerja
import com.kel5.magangyuk.ui.common.AppButton
import com.kel5.magangyuk.ui.common.AppButtonStyle
import com.kel5.magangyuk.ui.common.FilterChipItem
import com.kel5.magangyuk.ui.theme.Navy
import com.kel5.magangyuk.ui.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterLowonganSheet(
    state: LowonganUiState,
    onDismiss: () -> Unit,
    onJurusan: (String?) -> Unit,
    onLokasi: (String?) -> Unit,
    onJenis: (JenisLowongan?) -> Unit,
    onMode: (ModeKerja?) -> Unit,
    onReset: () -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = White) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(bottom = 24.dp)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                "Filter Lowongan",
                style = MaterialTheme.typography.titleLarge,
                color = Navy
            )

            // Jurusan dan lokasi diambil dari data lowongan yang ada (distinct)
            if (state.opsiJurusan.isNotEmpty()) {
                OpsiGroup("Jurusan", state.opsiJurusan, state.filter.jurusan, { it }, onJurusan)
            }
            if (state.opsiLokasi.isNotEmpty()) {
                OpsiGroup("Lokasi", state.opsiLokasi, state.filter.lokasi, { it }, onLokasi)
            }
            OpsiGroup("Jenis", JenisLowongan.entries, state.filter.jenis, ::labelJenis, onJenis)
            OpsiGroup("Mode kerja", ModeKerja.entries, state.filter.mode, ::labelMode, onMode)

            AppButton(
                text = "Reset Filter",
                onClick = onReset,
                style = AppButtonStyle.NEUTRAL_OUTLINE,
                enabled = state.filter.jumlahAktif > 0,
                modifier = Modifier.fillMaxWidth()
            )
            AppButton(
                text = "Tampilkan Hasil",
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

// Satu kelompok pilihan. <T> = generic: bisa dipakai untuk String, enum, dll.
// Ketuk chip yang sudah terpilih = lepas filter (kirim null).
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun <T> OpsiGroup(
    judul: String,
    opsi: List<T>,
    terpilih: T?,
    label: (T) -> String,
    onPilih: (T?) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(judul, style = MaterialTheme.typography.titleSmall, color = Navy)
        // FlowRow = baris yang otomatis turun ke bawah kalau tidak muat
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            opsi.forEach { o ->
                FilterChipItem(
                    text = label(o),
                    selected = o == terpilih,
                    onClick = { onPilih(if (o == terpilih) null else o) }
                )
            }
        }
    }
}