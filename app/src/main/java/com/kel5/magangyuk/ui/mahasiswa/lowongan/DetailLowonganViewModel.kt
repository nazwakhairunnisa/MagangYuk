package com.kel5.magangyuk.ui.mahasiswa.lowongan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.kel5.magangyuk.data.repository.DetailLowongan
import com.kel5.magangyuk.data.repository.LowonganMahasiswaRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// detail == null setelah isLoading false berarti lowongan tidak ditemukan
data class DetailUiState(
    val isLoading: Boolean = true,
    val detail: DetailLowongan? = null
)

class DetailLowonganViewModel(
    private val mahasiswaId: Long,
    private val lowonganId: Long,
    private val repository: LowonganMahasiswaRepository
) : ViewModel() {

    val uiState: StateFlow<DetailUiState> =
        repository.observeDetail(mahasiswaId, lowonganId)
            .map { DetailUiState(isLoading = false, detail = it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = DetailUiState()
            )

    // Cukup tulis ke database. Ikon ikut berubah sendiri lewat Flow.
    fun toggleBookmark() {
        val sekarang = uiState.value.detail?.item?.dibookmark ?: return
        viewModelScope.launch {
            repository.setBookmark(mahasiswaId, lowonganId, !sekarang)
        }
    }

    companion object {
        fun factory(
            mahasiswaId: Long,
            lowonganId: Long,
            repository: LowonganMahasiswaRepository
        ) = viewModelFactory {
            initializer { DetailLowonganViewModel(mahasiswaId, lowonganId, repository) }
        }
    }
}