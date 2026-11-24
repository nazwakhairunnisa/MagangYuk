package com.kel5.magangyuk.ui.mahasiswa.lowongan

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Work
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kel5.magangyuk.MagangYukApp
import com.kel5.magangyuk.data.repository.DetailLowongan
import com.kel5.magangyuk.data.repository.KondisiTombol
import com.kel5.magangyuk.ui.common.AppButton
import com.kel5.magangyuk.ui.common.EmptyState
import com.kel5.magangyuk.ui.common.InfoTag
import com.kel5.magangyuk.ui.common.TagTone
import com.kel5.magangyuk.ui.theme.CreamLight
import com.kel5.magangyuk.ui.theme.CreamYellow
import com.kel5.magangyuk.ui.theme.GrayCream
import com.kel5.magangyuk.ui.theme.Navy
import com.kel5.magangyuk.ui.theme.StatusGrayText
import com.kel5.magangyuk.ui.theme.White
import java.time.LocalDate

// Pintu masuk: tunggu sesi terbaca, lalu buat ViewModel (pola sama dengan LowonganRoute)
@Composable
fun DetailLowonganRoute(
    lowonganId: Long,
    onBack: () -> Unit,
    onAjukanLamaran: (Long) -> Unit
) {
    val app = LocalContext.current.applicationContext as MagangYukApp
    val session by app.sessionManager.session.collectAsState(initial = null)
    val mahasiswaId = session?.userId

    if (mahasiswaId == null) {
        Box(Modifier.fillMaxSize().background(CreamLight), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Navy)
        }
    } else {
        val viewModel: DetailLowonganViewModel = viewModel(
            key = "detail-lowongan-$mahasiswaId-$lowonganId",
            factory = DetailLowonganViewModel.factory(
                mahasiswaId, lowonganId, app.lowonganMahasiswaRepository
            )
        )
        val state by viewModel.uiState.collectAsState()

        DetailLowonganScreen(
            state = state,
            onBack = onBack,
            onBookmark = viewModel::toggleBookmark,
            onAjukan = { onAjukanLamaran(lowonganId) }
        )
    }
}

@Composable
private fun DetailLowonganScreen(
    state: DetailUiState,
    onBack: () -> Unit,
    onBookmark: () -> Unit,
    onAjukan: () -> Unit
) {
    Column(Modifier.fillMaxSize().background(CreamLight)) {
        DetailTopBar(onBack)

        val detail = state.detail
        when {
            state.isLoading -> Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Navy)
            }
            detail == null -> EmptyState(
                icon = Icons.Default.Search,
                text = "Lowongan tidak ditemukan.",
                modifier = Modifier.weight(1f)
            )
            else -> {
                // Konten bisa digulir, tombol tetap menempel di bawah
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Konten(detail, onBookmark)
                }
                Box(Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                    TombolLamar(detail.kondisiTombol, onAjukan)
                }
            }
        }
    }
}

@Composable
private fun DetailTopBar(onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp)
            .background(Navy)
            .statusBarsPadding() // background dulu, supaya navy ikut menutupi area status bar
            .padding(horizontal = 8.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali", tint = CreamYellow)
        }
        Text("Detail Lowongan", style = MaterialTheme.typography.titleLarge, color = CreamYellow)
    }
}

@Composable
private fun Konten(detail: DetailLowongan, onBookmark: () -> Unit) {
    val item = detail.item
    val l = item.lowongan

    // ---- Kartu header ----
    KartuPutih(spasi = 12.dp) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(56.dp).background(CreamLight, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.Work, contentDescription = null, tint = Navy)
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    l.posisi,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Navy
                )
                Text(item.namaPerusahaan, style = MaterialTheme.typography.bodyMedium, color = StatusGrayText)
            }
            // Tombol bookmark bulat abu krem sesuai Figma
            IconButton(
                onClick = onBookmark,
                modifier = Modifier.background(GrayCream, CircleShape)
            ) {
                Icon(
                    imageVector = if (item.dibookmark) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                    contentDescription = if (item.dibookmark) "Hapus bookmark" else "Simpan lowongan",
                    tint = Navy
                )
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            InfoTag(labelJenis(l.jenis), tone = TagTone.PURPLE)
            InfoTag("${durasiBulan(l.tglMulai, l.tglSelesai)} bulan")
            InfoTag(labelMode(l.mode))
        }

        // IntrinsicSize.Min + fillMaxHeight: dua kotak sebaris selalu sama tinggi
        // walau teks salah satunya turun ke baris kedua
        Row(Modifier.height(IntrinsicSize.Min), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            InfoBox("Lokasi", l.lokasi, Modifier.weight(1f).fillMaxHeight())
            InfoBox("Periode", formatPeriode(l.tglMulai, l.tglSelesai), Modifier.weight(1f).fillMaxHeight())
        }
        Row(Modifier.height(IntrinsicSize.Min), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            InfoBox("Batas Pendaftaran", formatTanggal(l.batasDaftar), Modifier.weight(1f).fillMaxHeight())
            InfoBox(
                "Sisa Kuota", "${detail.sisaKuota} / ${l.kuota}",
                Modifier.weight(1f).fillMaxHeight(), showDot = true
            )
        }
    }

    // ---- Deskripsi ----
    Bagian("Deskripsi Pekerjaan") {
        Text(l.deskripsi, style = MaterialTheme.typography.bodyLarge, color = Navy)
    }

    // ---- Persyaratan: satu baris teks = satu poin centang ----
    Bagian("Persyaratan") {
        daftarPersyaratan(l.persyaratan).forEach { PersyaratanItem(it) }
    }

    // ---- Informasi tambahan ----
    Bagian("Informasi Tambahan", spasi = 0.dp) {
        InfoRow("KTM wajib diunggah", if (l.wajibKtm) "Ya" else "Tidak")
        Garis()
        InfoRow("Laporan akhir ke mitra", if (l.wajibLaporanMitra) "Ya" else "Tidak")
        Garis()
        InfoRow("Pembimbing lapangan", l.pembimbingLapangan)
    }
}

// ---- Tombol dengan 5 kondisi (4 disabled + 1 normal) ----
@Composable
private fun TombolLamar(kondisi: KondisiTombol, onClick: () -> Unit) {
    val (teks, aktif) = when (kondisi) {
        KondisiTombol.BISA_MELAMAR -> "Ajukan Lamaran" to true
        KondisiTombol.SUDAH_MELAMAR -> "Sudah Dilamar" to false
        KondisiTombol.BATAS_LAMARAN -> "Batas 3 lamaran aktif tercapai" to false
        KondisiTombol.DITUTUP -> "Lowongan Ditutup" to false
        KondisiTombol.SUDAH_PUNYA_MAGANG -> "Kamu sudah memiliki magang" to false
    }
    AppButton(
        text = teks,
        onClick = onClick,
        enabled = aktif,
        trailingIcon = if (aktif) Icons.AutoMirrored.Filled.Send else null,
        modifier = Modifier.fillMaxWidth()
    )
}

// ---- Komponen kecil ----

@Composable
private fun KartuPutih(
    modifier: Modifier = Modifier,
    spasi: Dp = 0.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = RoundedCornerShape(24.dp)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(1.dp, shape)
            .background(White, shape)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(spasi),
        content = content
    )
}

// Judul bagian di luar kartu + kartu putih di bawahnya
@Composable
private fun Bagian(
    judul: String,
    spasi: Dp = 12.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            judul,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = Navy
        )
        KartuPutih(spasi = spasi, content = content)
    }
}

@Composable
private fun InfoBox(label: String, value: String, modifier: Modifier = Modifier, showDot: Boolean = false) {
    Row(
        modifier = modifier
            .background(CreamLight, RoundedCornerShape(16.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        Column(Modifier.weight(1f)) {
            Text(label, style = MaterialTheme.typography.bodySmall, color = StatusGrayText)
            Text(value, style = MaterialTheme.typography.titleSmall, color = Navy)
        }
        if (showDot) {
            Box(Modifier.size(10.dp).background(CreamYellow, CircleShape))
        }
    }
}

@Composable
private fun PersyaratanItem(teks: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier.size(28.dp).background(GrayCream, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Check, contentDescription = null, tint = Navy, modifier = Modifier.size(16.dp))
        }
        Spacer(Modifier.width(12.dp))
        Text(teks, style = MaterialTheme.typography.bodyLarge, color = Navy)
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = StatusGrayText)
        Text(
            value,
            style = MaterialTheme.typography.titleSmall,
            color = Navy,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1f).padding(start = 16.dp)
        )
    }
}

// Garis pemisah tipis antar baris
@Composable
private fun Garis() {
    Box(Modifier.fillMaxWidth().height(1.dp).background(GrayCream))
}

// ---- Fungsi bantu teks ----

// Pecah teks persyaratan per baris, buang penanda poin ("-", "•", "*") kalau mitra mengetiknya
private fun daftarPersyaratan(teks: String): List<String> =
    teks.lines()
        .map { it.trim().trimStart('-', '•', '*').trim() }
        .filter { it.isNotBlank() }

// Singkatan bulan ditulis sendiri supaya persis "Agt" seperti di spesifikasi
// (format bawaan Java untuk locale Indonesia menulis "Agu")
private val NAMA_BULAN = listOf("Jan", "Feb", "Mar", "Apr", "Mei", "Jun", "Jul", "Agt", "Sep", "Okt", "Nov", "Des")

private fun formatTanggal(teks: String, denganTahun: Boolean = true): String = runCatching {
    val d = LocalDate.parse(teks)
    val bulan = NAMA_BULAN[d.monthValue - 1]
    if (denganTahun) "${d.dayOfMonth} $bulan ${d.year}" else "${d.dayOfMonth} $bulan"
}.getOrDefault(teks)

// "1 Agt - 30 Sep 2026". Tahun mulai hanya ditulis kalau beda tahun dengan tanggal selesai.
private fun formatPeriode(mulai: String, selesai: String): String {
    val m = runCatching { LocalDate.parse(mulai) }.getOrNull()
    val s = runCatching { LocalDate.parse(selesai) }.getOrNull()
    val tahunSama = m != null && s != null && m.year == s.year
    return "${formatTanggal(mulai, denganTahun = !tahunSama)} - ${formatTanggal(selesai)}"
}