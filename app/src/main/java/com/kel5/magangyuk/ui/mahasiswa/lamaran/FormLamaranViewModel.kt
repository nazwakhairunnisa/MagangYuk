package com.kel5.magangyuk.ui.mahasiswa.lamaran

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.kel5.magangyuk.data.repository.HasilKirimLamaran
import com.kel5.magangyuk.data.repository.InfoFormLamaran
import com.kel5.magangyuk.data.repository.InputLamaran
import com.kel5.magangyuk.data.repository.LamaranRepository
import com.kel5.magangyuk.data.repository.validasiCv
import com.kel5.magangyuk.data.repository.validasiFormLamaran
import com.kel5.magangyuk.data.repository.validasiKtm
import com.kel5.magangyuk.util.FileStorage
import com.kel5.magangyuk.util.FileTerpilih
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FormLamaranUiState(
    val memuat: Boolean = true,
    val info: InfoFormLamaran? = null,       // null setelah memuat = lowongan tidak ditemukan
    val telepon: String = "",
    val cv: FileTerpilih? = null,
    val ktm: FileTerpilih? = null,
    val errors: Map<String, String> = emptyMap(),   // kunci: "telepon", "cv", "ktm"
    val tampilDialog: Boolean = false,       // dialog "Yakin kirim lamaran?"
    val mengirim: Boolean = false,
    val pesanGagal: String? = null,          // pelanggaran aturan bisnis dari Repository
    val berhasil: Boolean = false            // true -> layar pindah ke menu Lamaran
)

class FormLamaranViewModel(
    private val repository: LamaranRepository,
    private val fileStorage: FileStorage,
    private val mahasiswaId: Long,
    private val lowonganId: Long
) : ViewModel() {

    private val _uiState = MutableStateFlow(FormLamaranUiState())
    val uiState: StateFlow<FormLamaranUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val info = repository.muatForm(mahasiswaId, lowonganId)
            _uiState.update {
                it.copy(memuat = false, info = info, telepon = info?.telepon.orEmpty())
            }
        }
    }

    fun onTeleponChange(nilai: String) {
        // Saat user mengetik lagi, error field itu dihapus
        _uiState.update { it.copy(telepon = nilai, errors = it.errors - "telepon") }
    }

    // uri == null artinya user menutup picker tanpa memilih -> abaikan
    fun onCvDipilih(uri: Uri?) {
        if (uri == null) return
        val file = fileStorage.baca(uri)
        val error = validasiCv(file)
        _uiState.update {
            if (error == null) it.copy(cv = file, errors = it.errors - "cv")
            else it.copy(cv = null, errors = it.errors + ("cv" to error))   // file ditolak
        }
    }

    fun onKtmDipilih(uri: Uri?) {
        if (uri == null) return
        val file = fileStorage.baca(uri)
        val error = validasiKtm(file, wajib = false)   // di sini hanya cek format
        _uiState.update {
            if (error == null) it.copy(ktm = file, errors = it.errors - "ktm")
            else it.copy(ktm = null, errors = it.errors + ("ktm" to error))
        }
    }

    fun onHapusKtm() {
        _uiState.update { it.copy(ktm = null, errors = it.errors - "ktm") }
    }

    // Tombol "Kirim Lamaran": cek field dulu, kalau lolos baru tampilkan dialog konfirmasi
    fun onKirimDitekan() {
        val s = _uiState.value
        val info = s.info ?: return
        if (s.mengirim) return
        val errors = validasiFormLamaran(s.telepon, s.cv, s.ktm, info.wajibKtm)
        _uiState.update {
            it.copy(errors = errors, pesanGagal = null, tampilDialog = errors.isEmpty())
        }
    }

    fun onDialogBatal() {
        _uiState.update { it.copy(tampilDialog = false) }
    }

    // Tombol "Ya" di dialog: kirim sungguhan (Repository mengulang semua validasi)
    fun onKonfirmasiKirim() {
        val s = _uiState.value
        if (s.mengirim) return
        _uiState.update { it.copy(tampilDialog = false, mengirim = true, pesanGagal = null) }
        viewModelScope.launch {
            val hasil = repository.kirim(
                mahasiswaId, lowonganId,
                InputLamaran(s.telepon, s.cv, s.ktm)
            )
            _uiState.update {
                when (hasil) {
                    is HasilKirimLamaran.Sukses -> it.copy(mengirim = false, berhasil = true)
                    is HasilKirimLamaran.GagalField -> it.copy(mengirim = false, errors = hasil.errors)
                    is HasilKirimLamaran.Gagal -> it.copy(mengirim = false, pesanGagal = hasil.pesan)
                }
            }
        }
    }
    companion object {
        fun factory(
            repository: LamaranRepository,
            fileStorage: FileStorage,
            mahasiswaId: Long,
            lowonganId: Long
        ) = viewModelFactory {
            initializer { FormLamaranViewModel(repository, fileStorage, mahasiswaId, lowonganId) }
        }
    }
}

class FormLamaranViewModelFactory(
    private val repository: LamaranRepository,
    private val fileStorage: FileStorage,
    private val mahasiswaId: Long,
    private val lowonganId: Long,

) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        FormLamaranViewModel(repository, fileStorage, mahasiswaId, lowonganId) as T
}