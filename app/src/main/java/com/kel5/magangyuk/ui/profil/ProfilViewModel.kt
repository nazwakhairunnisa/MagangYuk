package com.kel5.magangyuk.ui.profil

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.kel5.magangyuk.data.repository.ProfilData
import com.kel5.magangyuk.data.repository.ProfilFieldKey
import com.kel5.magangyuk.data.repository.ProfilRepository
import com.kel5.magangyuk.data.repository.SaveResult
import com.kel5.magangyuk.data.session.Session
import com.kel5.magangyuk.data.session.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Satu objek yang menggambarkan seluruh isi layar. Layar hanya "menggambar" objek ini.
data class ProfilUiState(
    val isLoading: Boolean = true,
    val data: ProfilData? = null,
    val isEditing: Boolean = false,
    // Isi field yang sedang diketik (hanya terpakai saat isEditing)
    val draft: Map<ProfilFieldKey, String> = emptyMap(),
    val errors: Map<ProfilFieldKey, String> = emptyMap(),
    val isSaving: Boolean = false,
    // Pesan sekali tampil (snackbar). Layar memanggil messageShown() setelah menampilkannya.
    val message: String? = null
)

class ProfilViewModel(
    private val profilRepository: ProfilRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfilUiState())
    val uiState: StateFlow<ProfilUiState> = _uiState.asStateFlow()

    private var session: Session? = null

    init {
        loadProfil()
    }

    private fun loadProfil() {
        viewModelScope.launch {
            // first() = ambil nilai sesi saat ini sekali saja, lalu berhenti mengamati
            val s = sessionManager.session.first()
            session = s
            val data = s?.let { profilRepository.load(it.userId, it.role) }
            _uiState.update { it.copy(isLoading = false, data = data) }
        }
    }

    // Tombol "Edit Profil": salin nilai saat ini ke draft, lalu field jadi bisa diketik
    fun startEdit() {
        val data = _uiState.value.data ?: return
        _uiState.update {
            it.copy(
                isEditing = true,
                draft = data.editableFields.associate { f -> f.key to f.value },
                errors = emptyMap()
            )
        }
    }

    fun onFieldChange(key: ProfilFieldKey, value: String) {
        _uiState.update {
            it.copy(
                draft = it.draft + (key to value),
                errors = it.errors - key // error field itu hilang begitu user mengetik lagi
            )
        }
    }

    // Keluar dari mode edit tanpa menyimpan (dipakai kalau nanti ada tombol batal/back)
    fun cancelEdit() {
        _uiState.update { it.copy(isEditing = false, draft = emptyMap(), errors = emptyMap()) }
    }

    fun save() {
        val state = _uiState.value
        val s = session ?: return
        val data = state.data ?: return
        if (state.isSaving) return // cegah tekan dua kali

        // Tidak ada perubahan: cukup keluar dari mode edit, tidak perlu tulis database
        val original = data.editableFields.associate { it.key to it.value }
        if (state.draft == original) {
            cancelEdit()
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            when (val result = profilRepository.save(s.userId, s.role, state.draft)) {
                SaveResult.Success -> {
                    val fresh = profilRepository.load(s.userId, s.role)
                    _uiState.update {
                        it.copy(
                            isSaving = false, isEditing = false, data = fresh,
                            draft = emptyMap(), errors = emptyMap(),
                            message = "Profil berhasil disimpan"
                        )
                    }
                }
                is SaveResult.Invalid ->
                    _uiState.update { it.copy(isSaving = false, errors = result.errors) }
                SaveResult.NotFound ->
                    _uiState.update { it.copy(isSaving = false, message = "Data profil tidak ditemukan") }
            }
        }
    }

    fun messageShown() {
        _uiState.update { it.copy(message = null) }
    }

    companion object {
        fun factory(profilRepository: ProfilRepository, sessionManager: SessionManager) =
            viewModelFactory {
                initializer { ProfilViewModel(profilRepository, sessionManager) }
            }
    }
}