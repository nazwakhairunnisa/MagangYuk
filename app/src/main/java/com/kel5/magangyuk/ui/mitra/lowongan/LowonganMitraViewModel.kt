package com.kel5.magangyuk.ui.mitra.lowongan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.kel5.magangyuk.MagangYukApp
import com.kel5.magangyuk.data.local.entity.LowonganEntity
import com.kel5.magangyuk.data.repository.HasilHapus
import com.kel5.magangyuk.data.repository.LowonganRepository
import com.kel5.magangyuk.data.repository.StatusLowongan
import com.kel5.magangyuk.data.session.SessionManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class LowonganItem(
    val lowongan: LowonganEntity,
    val terisi: Int,
    val jumlahPelamar: Int,
    val status: StatusLowongan
)

data class LowonganMitraUiState(
    val items: List<LowonganItem> = emptyList(),
    val query: String = "",
    val loading: Boolean = true
)

@OptIn(ExperimentalCoroutinesApi::class)
class LowonganMitraViewModel(
    private val repository: LowonganRepository,
    sessionManager: SessionManager
) : ViewModel() {

    private val query = MutableStateFlow("")

    val uiState: StateFlow<LowonganMitraUiState> = sessionManager.session
        .flatMapLatest { session ->
            if (session == null) flowOf(emptyList())
            else repository.observeByMitra(session.userId)
        }
        .combine(query) { daftar, kataKunci ->
            val items = daftar
                .map {
                    LowonganItem(
                        lowongan = it.lowongan,
                        terisi = it.terisi,
                        jumlahPelamar = it.jumlahPelamar,
                        status = repository.hitungStatus(it.lowongan, it.terisi)
                    )
                }
                .filter {
                    kataKunci.isBlank() ||
                            it.lowongan.posisi.contains(kataKunci, ignoreCase = true) ||
                            it.lowongan.divisi.contains(kataKunci, ignoreCase = true)
                }
            LowonganMitraUiState(items = items, query = kataKunci, loading = false)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), LowonganMitraUiState())

    fun onQueryChange(teks: String) {
        query.value = teks
    }

    fun hapusAtauTutup(lowongan: LowonganEntity, onSelesai: (HasilHapus) -> Unit) {
        viewModelScope.launch {
            onSelesai(repository.hapusAtauTutup(lowongan))
        }
    }
}

class LowonganMitraViewModelFactory(
    private val app: MagangYukApp
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        LowonganMitraViewModel(app.lowonganRepository, app.sessionManager) as T
}