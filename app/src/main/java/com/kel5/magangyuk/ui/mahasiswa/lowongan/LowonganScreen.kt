package com.kel5.magangyuk.ui.mahasiswa.lowongan

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kel5.magangyuk.MagangYukApp
import com.kel5.magangyuk.ui.common.EmptyState
import com.kel5.magangyuk.ui.common.FilterChipItem
import com.kel5.magangyuk.ui.common.SearchBar
import com.kel5.magangyuk.ui.theme.CreamLight
import com.kel5.magangyuk.ui.theme.CreamYellow
import com.kel5.magangyuk.ui.theme.Navy
import com.kel5.magangyuk.ui.theme.StatusGrayText

@Composable
fun LowonganRoute(
    onDetailClick: (Long) -> Unit
) {
    val app = LocalContext.current.applicationContext as MagangYukApp
    // collectAsState(initial = null): sebelum sesi terbaca, nilainya null
    val session by app.sessionManager.session.collectAsState(initial = null)
    val mahasiswaId = session?.userId

    if (mahasiswaId == null) {
        Box(Modifier.fillMaxSize().background(CreamLight), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Navy)
        }
    } else {
        LowonganContent(mahasiswaId, onDetailClick)
    }
}

@Composable
private fun LowonganContent(
    mahasiswaId: Long,
    onDetailClick: (Long) -> Unit
) {
    val app = LocalContext.current.applicationContext as MagangYukApp
    val viewModel: LowonganViewModel = viewModel(
        // key = id: kalau akun lain login di HP yang sama, ViewModel dibuat baru, bukan pakai yang lama
        key = "lowongan-$mahasiswaId",
        factory = LowonganViewModel.factory(mahasiswaId, app.lowonganMahasiswaRepository)
    )
    val state by viewModel.uiState.collectAsState()

    LowonganScreen(
        state = state,
        onKataKunci = viewModel::ubahKataKunci,
        onJurusan = viewModel::pilihJurusan,
        onLokasi = viewModel::pilihLokasi,
        onJenis = viewModel::pilihJenis,
        onMode = viewModel::pilihMode,
        onReset = viewModel::resetFilter,
        onBookmark = viewModel::toggleBookmark,
        onDetailClick = onDetailClick
    )
}

@Composable
private fun LowonganScreen(
    state: LowonganUiState,
    onKataKunci: (String) -> Unit,
    onJurusan: (String?) -> Unit,
    onLokasi: (String?) -> Unit,
    onJenis: (com.kel5.magangyuk.data.local.entity.JenisLowongan?) -> Unit,
    onMode: (com.kel5.magangyuk.data.local.entity.ModeKerja?) -> Unit,
    onReset: () -> Unit,
    onBookmark: (Long, Boolean) -> Unit,
    onDetailClick: (Long) -> Unit
) {
    // rememberSaveable: status sheet bertahan saat layar diputar
    var tampilSheet by rememberSaveable { mutableStateOf(false) }

    Column(
        Modifier
            .fillMaxSize()
            .background(CreamLight)
            .imePadding()
    ) {
        // ---- Bagian tetap di atas: judul, search, chip aktif ----
        Column(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "Cari Lowongan",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = Navy
                    )
                    // Badge statis mengikuti Figma
                    Text(
                        "Mulai Kariermu",
                        style = MaterialTheme.typography.labelMedium,
                        color = Navy,
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .background(CreamYellow, RoundedCornerShape(50))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
                Text(
                    "Temukan kesempatan magang terbaik dan bangun portofolio profesionalmu.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = StatusGrayText
                )
            }

            SearchBar(
                query = state.filter.kataKunci,
                onQueryChange = onKataKunci,
                onFilterClick = { tampilSheet = true }
            )

            ChipFilterAktif(state, onJurusan, onLokasi, onJenis, onMode)
        }

        // ---- Daftar (scroll biasa, tanpa pagination) ----
        Box(Modifier.weight(1f)) {
            when {
                state.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Navy)
                }
                !state.adaLowonganAktif -> EmptyState(
                    icon = Icons.Default.Search,
                    text = "Belum ada lowongan yang dibuka."
                )
                state.daftar.isEmpty() -> EmptyState(
                    icon = Icons.Default.Search,
                    text = "Tidak ada lowongan yang cocok dengan pencarianmu."
                )
                else -> LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // key = id: Compose tahu item mana yang berubah, jadi animasi dan scroll stabil
                    items(state.daftar, key = { it.lowongan.id }) { item ->
                        LowonganCard(
                            item = item,
                            onDetailClick = { onDetailClick(item.lowongan.id) },
                            onBookmarkClick = { onBookmark(item.lowongan.id, !item.dibookmark) }
                        )
                    }
                }
            }
        }
    }

    if (tampilSheet) {
        FilterLowonganSheet(
            state = state,
            onDismiss = { tampilSheet = false },
            onJurusan = onJurusan,
            onLokasi = onLokasi,
            onJenis = onJenis,
            onMode = onMode,
            onReset = onReset
        )
    }
}

// Chip filter aktif (krem). Ketuk chip = lepas filter itu. Tidak tampil kalau tidak ada filter.
@Composable
private fun ChipFilterAktif(
    state: LowonganUiState,
    onJurusan: (String?) -> Unit,
    onLokasi: (String?) -> Unit,
    onJenis: (com.kel5.magangyuk.data.local.entity.JenisLowongan?) -> Unit,
    onMode: (com.kel5.magangyuk.data.local.entity.ModeKerja?) -> Unit
) {
    val f = state.filter
    if (f.jumlahAktif == 0) return

    Row(
        modifier = Modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        f.jurusan?.let { FilterChipItem("Jurusan ($it)", selected = true, onClick = { onJurusan(null) }) }
        f.lokasi?.let { FilterChipItem("Lokasi ($it)", selected = true, onClick = { onLokasi(null) }) }
        f.jenis?.let { FilterChipItem(labelJenis(it), selected = true, onClick = { onJenis(null) }) }
        f.mode?.let { FilterChipItem(labelMode(it), selected = true, onClick = { onMode(null) }) }
    }
}