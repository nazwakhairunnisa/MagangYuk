package com.kel5.magangyuk.ui.mitra.lowongan

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.kel5.magangyuk.data.local.entity.JenisLowongan
import com.kel5.magangyuk.data.local.entity.ModeKerja
import com.kel5.magangyuk.ui.common.AppButton
import com.kel5.magangyuk.ui.common.InfoTag
import com.kel5.magangyuk.ui.common.SegmentedToggle
import com.kel5.magangyuk.ui.common.TagTone
import com.kel5.magangyuk.ui.theme.ActionRed
import com.kel5.magangyuk.ui.theme.CreamLight
import com.kel5.magangyuk.ui.theme.CreamYellow
import com.kel5.magangyuk.ui.theme.Navy
import com.kel5.magangyuk.ui.theme.StatusGrayText
import com.kel5.magangyuk.ui.theme.GrayCream
import com.kel5.magangyuk.ui.theme.White
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@Composable
fun FormLowonganScreen(
    viewModel: FormLowonganViewModel,
    onBack: () -> Unit,
    onSelesai: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val judul = if (viewModel.sedangSunting) "Sunting Lowongan" else "Tambah Lowongan"

    LaunchedEffect(state.selesai) {
        if (state.selesai) onSelesai()
    }

    Column(modifier = Modifier.fillMaxSize().background(CreamLight)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Navy)
                .statusBarsPadding()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Kembali",
                    tint = CreamYellow
                )
            }
            Text(
                text = judul,
                style = MaterialTheme.typography.titleMedium,
                color = White
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column {
                Text(
                    text = judul,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Navy
                )
                Text(
                    text = "Lengkapi detail posisi magang yang dibuka untuk mitra industri.",
                    style = MaterialTheme.typography.bodySmall,
                    color = StatusGrayText
                )
            }

            Field("Posisi", required = true, trailing = "Wajib diisi") {
                TextInput(
                    value = state.posisi,
                    onChange = { v -> viewModel.ubah { it.copy(posisi = v) } },
                    placeholder = "Contoh: Business Development",
                    error = state.errors["posisi"]
                )
            }

            Field("Divisi", required = true) {
                TextInput(
                    value = state.divisi,
                    onChange = { v -> viewModel.ubah { it.copy(divisi = v) } },
                    placeholder = "Contoh: Divisi Bisnis & Kemitraan",
                    error = state.errors["divisi"]
                )
            }

            Field("Deskripsi Pekerjaan", required = true) {
                TextInput(
                    value = state.deskripsi,
                    onChange = { v -> viewModel.ubah { it.copy(deskripsi = v) } },
                    placeholder = "Jelaskan tanggung jawab utama dan ekspektasi capaian mahasiswa magang...",
                    error = state.errors["deskripsi"],
                    minLines = 3
                )
            }

            Field("Persyaratan / Kualifikasi", required = true) {
                TextInput(
                    value = state.persyaratan,
                    onChange = { v -> viewModel.ubah { it.copy(persyaratan = v) } },
                    placeholder = "Daftar kualifikasi kandidat, keahlian teknis, dan soft skills yang diharapkan...",
                    error = state.errors["persyaratan"],
                    minLines = 3
                )
            }

            Field("Jurusan yang Dituju", required = false, trailing = "Opsional") {
                TextInput(
                    value = state.jurusan,
                    onChange = { v -> viewModel.ubah { it.copy(jurusan = v) } },
                    placeholder = "Contoh: Teknologi Informasi, Sistem Informasi",
                    error = null
                )
            }

            Field("Kuota", required = true) {
                TextInput(
                    value = state.kuota,
                    onChange = { v -> viewModel.ubah { it.copy(kuota = v.filter(Char::isDigit)) } },
                    placeholder = "0",
                    error = state.errors["kuota"],
                    keyboardType = KeyboardType.Number
                )
                if (state.errors["kuota"] == null) {
                    Text(
                        text = "Tidak boleh lebih kecil dari slot yang sudah terpakai",
                        style = MaterialTheme.typography.bodySmall,
                        color = StatusGrayText,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            Field("Lokasi Penempatan", required = true) {
                TextInput(
                    value = state.lokasi,
                    onChange = { v -> viewModel.ubah { it.copy(lokasi = v) } },
                    placeholder = "Contoh: Medan, Sumatera Utara",
                    error = state.errors["lokasi"],
                    leadingIcon = Icons.Default.LocationOn
                )
            }

            Field("Mode Kerja", required = true) {
                SegmentedToggle(
                    options = listOf("Onsite", "Hybrid", "Online"),
                    selectedIndex = ModeKerja.entries.indexOf(state.mode),
                    onSelect = { i -> viewModel.ubah { it.copy(mode = ModeKerja.entries[i]) } }
                )
            }

            Field("Jenis Kompensasi", required = true) {
                SegmentedToggle(
                    options = listOf("Paid", "Unpaid"),
                    selectedIndex = JenisLowongan.entries.indexOf(state.jenis),
                    onSelect = { i -> viewModel.ubah { it.copy(jenis = JenisLowongan.entries[i]) } }
                )
            }

            Field("Periode Magang", required = true) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Mulai", style = MaterialTheme.typography.bodySmall, color = StatusGrayText)
                        DateInput(
                            value = state.tglMulai,
                            error = state.errors["tglMulai"],
                            onPick = { v -> viewModel.ubah { it.copy(tglMulai = v) } }
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Selesai", style = MaterialTheme.typography.bodySmall, color = StatusGrayText)
                        DateInput(
                            value = state.tglSelesai,
                            error = state.errors["tglSelesai"],
                            onPick = { v -> viewModel.ubah { it.copy(tglSelesai = v) } }
                        )
                    }
                }
                val durasi = hitungDurasi(state.tglMulai, state.tglSelesai)
                if (durasi != null) {
                    InfoTag(
                        text = "Durasi: $durasi bulan",
                        tone = TagTone.PURPLE,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }

            Field("Batas Pendaftaran", required = true) {
                DateInput(
                    value = state.batasDaftar,
                    error = state.errors["batasDaftar"],
                    onPick = { v -> viewModel.ubah { it.copy(batasDaftar = v) } }
                )
            }

            Field("Nama Pembimbing Lapangan", required = true) {
                TextInput(
                    value = state.pembimbingLapangan,
                    onChange = { v -> viewModel.ubah { it.copy(pembimbingLapangan = v) } },
                    placeholder = "Contoh: Budi Santoso, S.Kom.",
                    error = state.errors["pembimbingLapangan"],
                    leadingIcon = Icons.Default.Person
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(White, RoundedCornerShape(16.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Pengaturan Kepatuhan",
                    style = MaterialTheme.typography.titleMedium,
                    color = Navy
                )
                SwitchRow(
                    judul = "Wajib KTM",
                    keterangan = "Memastikan status mahasiswa aktif",
                    checked = state.wajibKtm,
                    onChange = { v -> viewModel.ubah { it.copy(wajibKtm = v) } }
                )
                SwitchRow(
                    judul = "Wajib laporan akhir ke mitra",
                    keterangan = "Penyerahan salinan hard/softcopy logbook",
                    checked = state.wajibLaporanMitra,
                    onChange = { v -> viewModel.ubah { it.copy(wajibLaporanMitra = v) } }
                )
            }

            AppButton(
                text = "Simpan Lowongan",
                onClick = viewModel::simpan,
                enabled = !state.saving,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun Field(
    label: String,
    required: Boolean,
    trailing: String? = null,
    content: @Composable () -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = buildAnnotatedString {
                    append(label)
                    if (required) {
                        withStyle(SpanStyle(color = ActionRed)) { append(" *") }
                    }
                },
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = Navy
            )
            if (trailing != null) {
                Text(
                    text = trailing,
                    style = MaterialTheme.typography.bodySmall,
                    color = StatusGrayText
                )
            }
        }
        content()
    }
}

@Composable
private fun TextInput(
    value: String,
    onChange: (String) -> Unit,
    placeholder: String,
    error: String?,
    minLines: Int = 1,
    keyboardType: KeyboardType = KeyboardType.Text,
    leadingIcon: ImageVector? = null
) {
    Column {
        OutlinedTextField(
            value = value,
            onValueChange = onChange,
            modifier = Modifier.fillMaxWidth(),
            minLines = minLines,
            singleLine = minLines == 1,
            isError = error != null,
            placeholder = {
                Text(
                    text = placeholder,
                    style = MaterialTheme.typography.bodyMedium,
                    color = StatusGrayText
                )
            },
            leadingIcon = if (leadingIcon != null) {
                { Icon(leadingIcon, contentDescription = null, tint = StatusGrayText) }
            } else null,
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = White,
                unfocusedContainerColor = White,
                errorContainerColor = White,
                focusedIndicatorColor = Navy,
                unfocusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                errorIndicatorColor = ActionRed,
                cursorColor = Navy,
                focusedTextColor = Navy,
                unfocusedTextColor = Navy
            )
        )
        if (error != null) {
            Text(
                text = error,
                style = MaterialTheme.typography.bodySmall,
                color = ActionRed,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DateInput(
    value: String,
    error: String?,
    onPick: (String) -> Unit
) {
    var tampil by remember { mutableStateOf(false) }
    val tanggal = runCatching { LocalDate.parse(value) }.getOrNull()

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(White, RoundedCornerShape(12.dp))
                .border(
                    BorderStroke(1.dp, if (error != null) ActionRed else androidx.compose.ui.graphics.Color.Transparent),
                    RoundedCornerShape(12.dp)
                )
                .clickable { tampil = true }
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = tanggal?.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) ?: "dd/mm/yyyy",
                style = MaterialTheme.typography.bodyMedium,
                color = if (tanggal != null) Navy else StatusGrayText
            )
            Icon(Icons.Default.DateRange, contentDescription = null, tint = StatusGrayText)
        }
        if (error != null) {
            Text(
                text = error,
                style = MaterialTheme.typography.bodySmall,
                color = ActionRed,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }

    if (tampil) {
        val pickerState = rememberDatePickerState(
            initialSelectedDateMillis = tanggal?.atStartOfDay(ZoneOffset.UTC)?.toInstant()?.toEpochMilli()
        )
        DatePickerDialog(
            onDismissRequest = { tampil = false },
            confirmButton = {
                TextButton(onClick = {
                    pickerState.selectedDateMillis?.let { millis ->
                        val dipilih = java.time.Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate()
                        onPick(dipilih.toString())
                    }
                    tampil = false
                }) { Text("Pilih", color = Navy) }
            },
            dismissButton = {
                TextButton(onClick = { tampil = false }) { Text("Batal", color = StatusGrayText) }
            }
        ) {
            DatePicker(state = pickerState)
        }
    }
}

@Composable
private fun SwitchRow(
    judul: String,
    keterangan: String,
    checked: Boolean,
    onChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
            Text(judul, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = Navy)
            Text(keterangan, style = MaterialTheme.typography.bodySmall, color = StatusGrayText)
        }
        Switch(
            checked = checked,
            onCheckedChange = onChange,
            colors = SwitchDefaults.colors(
                checkedTrackColor = Navy,
                checkedThumbColor = White,
                uncheckedTrackColor = GrayCream,
                uncheckedThumbColor = White,
                uncheckedBorderColor = GrayCream
            )
        )
    }
}

private fun hitungDurasi(mulai: String, selesai: String): Long? {
    val a = runCatching { LocalDate.parse(mulai) }.getOrNull() ?: return null
    val b = runCatching { LocalDate.parse(selesai) }.getOrNull() ?: return null
    if (!b.isAfter(a)) return null
    return maxOf(ChronoUnit.MONTHS.between(a, b.plusDays(1)), 1L)
}