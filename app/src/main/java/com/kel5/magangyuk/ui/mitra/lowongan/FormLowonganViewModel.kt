package com.kel5.magangyuk.ui.mitra.lowongan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.kel5.magangyuk.MagangYukApp
import com.kel5.magangyuk.data.local.entity.JenisLowongan
import com.kel5.magangyuk.data.local.entity.LowonganEntity
import com.kel5.magangyuk.data.local.entity.ModeKerja
import com.kel5.magangyuk.data.repository.HasilSimpan
import com.kel5.magangyuk.data.repository.LowonganRepository
import com.kel5.magangyuk.data.session.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FormLowonganState(
    val posisi: String = "",
    val divisi: String = "",
    val deskripsi: String = "",
    val persyaratan: String = "",
    val jurusan: String = "",
    val kuota: String = "",
    val lokasi: String = "",
    val mode: ModeKerja = ModeKerja.HYBRID,
    val jenis: JenisLowongan = JenisLowongan.PAID,
    val tglMulai: String = "",
    val tglSelesai: String = "",
    val batasDaftar: String = "",
    val pembimbingLapangan: String = "",
    val wajibKtm: Boolean = false,
    val wajibLaporanMitra: Boolean = false,
    val errors: Map<String, String> = emptyMap(),
    val saving: Boolean = false,
    val selesai: Boolean = false
)

class FormLowonganViewModel(
    private val lowonganId: Long,
    private val repository: LowonganRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _state = MutableStateFlow(FormLowonganState())
    val state: StateFlow<FormLowonganState> = _state.asStateFlow()

    private var existing: LowonganEntity? = null

    val sedangSunting: Boolean = lowonganId != 0L

    init {
        if (sedangSunting) {
            viewModelScope.launch {
                val data = repository.getById(lowonganId) ?: return@launch
                existing = data
                _state.update {
                    it.copy(
                        posisi = data.posisi,
                        divisi = data.divisi,
                        deskripsi = data.deskripsi,
                        persyaratan = data.persyaratan,
                        jurusan = data.jurusan.orEmpty(),
                        kuota = data.kuota.toString(),
                        lokasi = data.lokasi,
                        mode = data.mode,
                        jenis = data.jenis,
                        tglMulai = data.tglMulai,
                        tglSelesai = data.tglSelesai,
                        batasDaftar = data.batasDaftar,
                        pembimbingLapangan = data.pembimbingLapangan,
                        wajibKtm = data.wajibKtm,
                        wajibLaporanMitra = data.wajibLaporanMitra
                    )
                }
            }
        }
    }

    fun ubah(transform: (FormLowonganState) -> FormLowonganState) {
        _state.update(transform)
    }

    fun simpan() {
        val s = _state.value
        if (s.saving) return
        _state.update { it.copy(saving = true) }
        viewModelScope.launch {
            val session = sessionManager.session.first()
            if (session == null) {
                _state.update { it.copy(saving = false) }
                return@launch
            }
            val entity = LowonganEntity(
                id = lowonganId,
                mitraId = existing?.mitraId ?: session.userId,
                posisi = s.posisi.trim(),
                divisi = s.divisi.trim(),
                deskripsi = s.deskripsi.trim(),
                persyaratan = s.persyaratan.trim(),
                jurusan = s.jurusan.trim().ifBlank { null },
                kuota = s.kuota.toIntOrNull() ?: 0,
                lokasi = s.lokasi.trim(),
                mode = s.mode,
                jenis = s.jenis,
                tglMulai = s.tglMulai,
                tglSelesai = s.tglSelesai,
                batasDaftar = s.batasDaftar,
                pembimbingLapangan = s.pembimbingLapangan.trim(),
                wajibKtm = s.wajibKtm,
                wajibLaporanMitra = s.wajibLaporanMitra,
                ditutupManual = existing?.ditutupManual ?: false
            )
            when (val hasil = repository.simpan(entity)) {
                is HasilSimpan.Sukses -> _state.update { it.copy(saving = false, selesai = true, errors = emptyMap()) }
                is HasilSimpan.Gagal -> _state.update { it.copy(saving = false, errors = hasil.errors) }
            }
        }
    }
}

class FormLowonganViewModelFactory(
    private val app: MagangYukApp,
    private val lowonganId: Long
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        FormLowonganViewModel(lowonganId, app.lowonganRepository, app.sessionManager) as T
}