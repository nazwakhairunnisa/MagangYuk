package com.kel5.magangyuk.ui.mahasiswa.lowongan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.kel5.magangyuk.data.local.dao.LowonganUntukMahasiswa
import com.kel5.magangyuk.data.local.entity.JenisLowongan
import com.kel5.magangyuk.data.local.entity.ModeKerja
import com.kel5.magangyuk.data.repository.FilterLowongan
import com.kel5.magangyuk.data.repository.LowonganMahasiswaRepository
import com.kel5.magangyuk.data.repository.kotaDari
import com.kel5.magangyuk.data.repository.terapkan
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Semua yang dibutuhkan layar, dalam satu objek
data class LowonganUiState(
    val isLoading: Boolean = true,
    val daftar: List<LowonganUntukMahasiswa> = emptyList(), // sudah disaring
    val filter: FilterLowongan = FilterLowongan(),
    // Pilihan untuk bottom sheet filter, diambil dari data yang ada (distinct)
    val opsiJurusan: List<String> = emptyList(),
    val opsiLokasi: List<String> = emptyList(),
    // Membedakan empty state: "belum ada lowongan" vs "tidak ada yang cocok dengan filter"
    val adaLowonganAktif: Boolean = false
)

class LowonganViewModel(
    private val mahasiswaId: Long,
    private val repository: LowonganMahasiswaRepository,
    kataKunciAwal: String = "" // untuk search dari Beranda (nanti)
) : ViewModel() {

    // Filter yang sedang dipilih. MutableStateFlow = kotak nilai yang bisa diubah dan diamati.
    private val filter = MutableStateFlow(FilterLowongan(kataKunci = kataKunciAwal))

    val uiState: StateFlow<LowonganUiState> =
        combine(repository.observeAktif(mahasiswaId), filter) { semua, f ->
            LowonganUiState(
                isLoading = false,
                daftar = semua.terapkan(f),
                filter = f,
                opsiJurusan = semua.mapNotNull { it.lowongan.jurusan?.takeIf { j -> j.isNotBlank() } }
                    .distinct().sorted(),
                opsiLokasi = semua.map { kotaDari(it.lowongan.lokasi) }.distinct().sorted(),
                adaLowonganAktif = semua.isNotEmpty()
            )
        }.stateIn(
            scope = viewModelScope,
            // Berhenti mengamati 5 detik setelah layar tidak tampil (hemat, aman saat rotasi)
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = LowonganUiState()
        )

    // ---- Aksi dari layar ----
    fun ubahKataKunci(teks: String) = filter.update { it.copy(kataKunci = teks) }
    fun pilihJurusan(jurusan: String?) = filter.update { it.copy(jurusan = jurusan) }
    fun pilihLokasi(lokasi: String?) = filter.update { it.copy(lokasi = lokasi) }
    fun pilihJenis(jenis: JenisLowongan?) = filter.update { it.copy(jenis = jenis) }
    fun pilihMode(mode: ModeKerja?) = filter.update { it.copy(mode = mode) }

    // Reset semua filter tapi kata kunci tetap
    fun resetFilter() = filter.update { FilterLowongan(kataKunci = it.kataKunci) }

    fun toggleBookmark(lowonganId: Long, simpan: Boolean) {
        // launch = jalankan di coroutine, karena akses database tidak boleh di thread utama
        viewModelScope.launch {
            repository.setBookmark(mahasiswaId, lowonganId, simpan)
        }
    }

    companion object {
        // Pola sama seperti LoginViewModel/ProfilViewModel: dependency dikirim dari luar
        fun factory(
            mahasiswaId: Long,
            repository: LowonganMahasiswaRepository,
            kataKunciAwal: String = ""
        ) = viewModelFactory {
            initializer { LowonganViewModel(mahasiswaId, repository, kataKunciAwal) }
        }
    }
}