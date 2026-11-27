package com.kel5.magangyuk.ui.mahasiswa.lamaran

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material.icons.filled.WorkOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kel5.magangyuk.MagangYukApp
import com.kel5.magangyuk.ui.common.AppButton
import com.kel5.magangyuk.ui.common.AppTextField
import com.kel5.magangyuk.ui.common.ConfirmDialog
import com.kel5.magangyuk.ui.common.EmptyState
import com.kel5.magangyuk.ui.theme.ActionGreen
import com.kel5.magangyuk.ui.theme.ActionRed
import com.kel5.magangyuk.ui.theme.CreamLight
import com.kel5.magangyuk.ui.theme.CreamYellow
import com.kel5.magangyuk.ui.theme.GrayCream
import com.kel5.magangyuk.ui.theme.Navy
import com.kel5.magangyuk.ui.theme.StatusGrayText
import com.kel5.magangyuk.ui.theme.White
import com.kel5.magangyuk.util.FileTerpilih
import java.util.Locale

// Warna lokal (solid, sesuai spesifikasi A.2 untuk status Revisi/Ditolak) dan banner krem.
// Kalau di ui/theme sudah ada nama untuk warna ini, ganti dengan yang itu.
private val ChipWajibBg = Color(0xFFFDE0E0)
private val ChipWajibText = Color(0xFFB42318)
private val BannerBg = Color(0xFFF5EED0)

// ---------- Route: urus sesi + ViewModel ----------
@Composable
fun FormLamaranRoute(
    lowonganId: Long,
    onBack: () -> Unit,
    onSelesai: () -> Unit      // dipanggil setelah lamaran berhasil terkirim
) {
    val app = LocalContext.current.applicationContext as MagangYukApp
    val session by app.sessionManager.session.collectAsState(initial = null)
    val mahasiswaId = session?.userId

    if (mahasiswaId == null) {
        // Sesi belum terbaca (hanya sekejap)
        Box(Modifier.fillMaxSize().background(CreamLight), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Navy)
        }
        return
    }

    // key membuat ViewModel baru kalau mahasiswa atau lowongannya beda
    val viewModel: FormLamaranViewModel = viewModel(
        key = "form_lamaran_${mahasiswaId}_$lowonganId",
        factory = FormLamaranViewModel.factory(
            app.lamaranRepository, app.fileStorage, mahasiswaId, lowonganId
        )
    )
    val state by viewModel.uiState.collectAsState()

    // Berhasil -> pindah layar sekali. LaunchedEffect(key): jalan lagi hanya saat key berubah.
    LaunchedEffect(state.berhasil) {
        if (state.berhasil) onSelesai()
    }

    // OpenDocument = file picker bawaan Android. Tidak butuh izin penyimpanan.
    val cvPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) {
        viewModel.onCvDipilih(it)
    }
    val ktmPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) {
        viewModel.onKtmDipilih(it)
    }

    FormLamaranScreen(
        state = state,
        onBack = onBack,
        onTeleponChange = viewModel::onTeleponChange,
        onPilihCv = { cvPicker.launch(arrayOf("application/pdf")) },
        onPilihKtm = { ktmPicker.launch(arrayOf("image/*", "application/pdf")) },
        onHapusKtm = viewModel::onHapusKtm,
        onKirim = viewModel::onKirimDitekan
    )

    if (state.tampilDialog) {
        ConfirmDialog(
            title = "Kirim Lamaran",
            message = "Yakin kirim lamaran?",
            onConfirm = viewModel::onKonfirmasiKirim,
            onDismiss = viewModel::onDialogBatal
        )
    }
}

// ---------- Screen: hanya menggambar ----------
@Composable
fun FormLamaranScreen(
    state: FormLamaranUiState,
    onBack: () -> Unit,
    onTeleponChange: (String) -> Unit,
    onPilihCv: () -> Unit,
    onPilihKtm: () -> Unit,
    onHapusKtm: () -> Unit,
    onKirim: () -> Unit
) {
    Column(Modifier.fillMaxSize().background(CreamLight)) {
        val info = state.info

        TopBarLamaran(onBack)

        when {
            state.memuat -> Box(
                Modifier.weight(1f).fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator(color = Navy) }
            info == null -> EmptyState(
                icon = Icons.Default.WorkOff,
                text = "Lowongan tidak ditemukan",
                modifier = Modifier.weight(1f)
            )
            else -> {
                // Judul dua baris sesuai Figma
                Column(
                    Modifier.fillMaxWidth().background(White).padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Text(
                        "Lamar: ${info.posisi}",
                        style = MaterialTheme.typography.titleLarge,
                        color = Navy
                    )
                    Text(
                        info.namaPerusahaan,
                        style = MaterialTheme.typography.bodyMedium,
                        color = StatusGrayText
                    )
                }

                Column(
                    Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .imePadding()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Banner statis (keputusan desain no. 8): tanpa logika verifikasi
                    BannerVerifikasi()

                    // ----- Data Diri -----
                    SectionTitle("Data Diri", "Sesuai SIAKAD")
                    Column(
                        Modifier.fillMaxWidth().background(White, RoundedCornerShape(20.dp)).padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ReadOnlyField("Nama Lengkap", info.nama)
                        ReadOnlyField("NIM", info.nim)
                        ReadOnlyField("Program Studi", info.prodi)
                        ReadOnlyField("Email Kampus", info.email)

                        // Petunjuk kecil di kanan atas label, ditumpuk di atas AppTextField
                        Box {
                            AppTextField(
                                value = state.telepon,
                                onValueChange = onTeleponChange,
                                label = "Nomor Telepon",
                                placeholder = "08xx atau +62",
                                error = state.errors["telepon"],
                                keyboardType = KeyboardType.Phone,
                                imeAction = ImeAction.Done,
                                leadingIcon = Icons.Default.Phone,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Text(
                                "WhatsApp Aktif",
                                style = MaterialTheme.typography.bodySmall,
                                color = StatusGrayText,
                                modifier = Modifier.align(Alignment.TopEnd)
                            )
                        }
                    }

                    // ----- Dokumen -----
                    val jumlahWajib = if (info.wajibKtm) 2 else 1
                    SectionTitle("Dokumen", "$jumlahWajib berkas wajib", aksenMerah = true)

                    DokumenCard(
                        ikon = Icons.Default.Description,
                        judul = "Unggah CV",
                        keterangan = "Format .pdf ukuran maks 5 MB",
                        wajib = true,
                        file = state.cv,
                        error = state.errors["cv"],
                        onPilih = onPilihCv
                    )
                    DokumenCard(
                        ikon = Icons.Default.Badge,
                        judul = "Unggah KTM",
                        keterangan = "JPG/PNG/PDF",
                        wajib = info.wajibKtm,
                        file = state.ktm,
                        error = state.errors["ktm"],
                        onPilih = onPilihKtm,
                        onHapus = onHapusKtm
                    )

                    // Pesan dari Repository (dobel, 3 aktif, ditutup, dst)
                    if (state.pesanGagal != null) {
                        Text(
                            state.pesanGagal,
                            style = MaterialTheme.typography.bodyMedium,
                            color = ActionRed,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(ChipWajibBg, RoundedCornerShape(12.dp))
                                .padding(12.dp)
                        )
                    }

                    AppButton(
                        text = "Kirim Lamaran",
                        onClick = onKirim,
                        trailingIcon = Icons.AutoMirrored.Filled.Send,
                        loading = state.mengirim,
                        modifier = Modifier.fillMaxWidth().height(52.dp)
                    )
                }
            }
        }
    }
}

// ---------- Potongan-potongan layar ----------

// Top bar sendiri (layar ini ada di hidesAppHeader, jadi AppHeader global tidak tampil)
@Composable
private fun TopBarLamaran(onBack: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .shadow(2.dp)             // shadow tipis, sama dengan DetailTopBar
            .background(Navy)         // background dulu, baru statusBarsPadding
            .statusBarsPadding()
            .padding(horizontal = 8.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Kembali", tint = CreamYellow)
        }
        Text("Form Lamaran", style = MaterialTheme.typography.titleLarge, color = CreamYellow)
    }
}

@Composable
private fun BannerVerifikasi() {
    Row(
        Modifier.fillMaxWidth().background(BannerBg, RoundedCornerShape(16.dp)).padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier.size(44.dp).background(CreamYellow, CircleShape),
            contentAlignment = Alignment.Center
        ) { Icon(Icons.Default.Shield, null, tint = Navy) }
        Column(Modifier.padding(start = 12.dp)) {
            Text("Verifikasi Data Kampus Terhubung", style = MaterialTheme.typography.titleSmall, color = Navy)
            Text(
                "Data akademik disinkronkan otomatis dari PDDikti",
                style = MaterialTheme.typography.bodySmall,
                color = Navy
            )
        }
    }
}

@Composable
private fun SectionTitle(judul: String, keterangan: String, aksenMerah: Boolean = false) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(judul, style = MaterialTheme.typography.titleLarge, color = Navy)
        Text(
            keterangan,
            style = MaterialTheme.typography.titleSmall,
            color = if (aksenMerah) ActionRed else StatusGrayText
        )
    }
}

// Field terkunci: latar abu krem + ikon gembok. Bukan input, hanya tampilan.
@Composable
private fun ReadOnlyField(label: String, nilai: String) {
    Column {
        Text(label, style = MaterialTheme.typography.titleSmall, color = StatusGrayText)
        Row(
            Modifier
                .padding(top = 8.dp)
                .fillMaxWidth()
                .background(GrayCream, RoundedCornerShape(16.dp))
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                nilai,
                style = MaterialTheme.typography.bodyLarge,
                color = Navy,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Icon(Icons.Default.Lock, "Tidak bisa diubah", tint = StatusGrayText, modifier = Modifier.size(20.dp))
        }
    }
}

// Kartu unggah: belum ada file -> keterangan format. Sudah ada -> nama file + ukuran.
@Composable
private fun DokumenCard(
    ikon: ImageVector,
    judul: String,
    keterangan: String,
    wajib: Boolean,
    file: FileTerpilih?,
    error: String?,
    onPilih: () -> Unit,
    onHapus: (() -> Unit)? = null     // hanya KTM yang bisa dilepas lagi
) {
    Column(Modifier.fillMaxWidth().background(White, RoundedCornerShape(20.dp)).padding(16.dp)) {
        Row(
            Modifier.fillMaxWidth().clickable(onClick = onPilih),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier.size(48.dp).background(GrayCream, CircleShape),
                contentAlignment = Alignment.Center
            ) { Icon(ikon, null, tint = Navy) }

            Column(Modifier.weight(1f).padding(horizontal = 12.dp)) {
                Text(judul, style = MaterialTheme.typography.titleSmall, color = Navy)
                Text(
                    text = if (file != null) "${file.nama} (${formatUkuran(file.ukuranByte)})" else keterangan,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = if (file != null) ActionGreen else StatusGrayText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Chip Wajib (merah) atau Opsional (abu-abu), sesuai warna status di spesifikasi A.2
            Text(
                if (wajib) "Wajib" else "Opsional",
                style = MaterialTheme.typography.labelMedium,
                color = if (wajib) ChipWajibText else StatusGrayText,
                modifier = Modifier
                    .background(if (wajib) ChipWajibBg else GrayCream, RoundedCornerShape(50))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            )
            Icon(
                if (file != null) Icons.Default.CheckCircle else Icons.Default.UploadFile,
                contentDescription = if (file != null) "Sudah dipilih" else "Pilih file",
                tint = if (file != null) ActionGreen else StatusGrayText,
                modifier = Modifier.padding(start = 8.dp).size(24.dp)
            )
        }

        if (file != null && onHapus != null) {
            Text(
                "Lepas file",
                style = MaterialTheme.typography.labelMedium,
                color = ActionRed,
                modifier = Modifier.padding(top = 8.dp).clickable(onClick = onHapus)
            )
        }
        if (error != null) {
            Text(
                error,
                style = MaterialTheme.typography.bodySmall,
                color = ActionRed,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

private fun formatUkuran(byte: Long): String = when {
    byte < 0 -> "ukuran tidak diketahui"
    byte < 1024 * 1024 -> String.format(Locale("id"), "%.0f KB", byte / 1024.0)
    else -> String.format(Locale("id"), "%.1f MB", byte / (1024.0 * 1024.0))
}