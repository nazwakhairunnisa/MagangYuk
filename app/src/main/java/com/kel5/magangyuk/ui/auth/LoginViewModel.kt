package com.kel5.magangyuk.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.kel5.magangyuk.data.local.entity.Role
import com.kel5.magangyuk.data.repository.AuthRepository
import com.kel5.magangyuk.data.repository.LoginResult
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Semua yang ditampilkan layar Login ada di sini (ibarat papan tulis)
data class LoginUiState(
    val loginId: String = "",
    val password: String = "",
    val loginIdError: String? = null,
    val passwordError: String? = null,
    val isLoading: Boolean = false
)

// Kejadian sekali jalan (bukan state), supaya tidak terpicu dua kali saat rotasi
sealed interface LoginEvent {
    data class Success(val role: Role) : LoginEvent
}

class LoginViewModel(
    private val role: Role,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    // Channel = "kotak surat": dikirim sekali, diterima sekali
    private val _events = Channel<LoginEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    fun onLoginIdChange(value: String) {
        // Saat mengetik, error dihapus supaya tidak "menempel"
        _uiState.update { it.copy(loginId = value, loginIdError = null, passwordError = null) }
    }

    fun onPasswordChange(value: String) {
        _uiState.update { it.copy(password = value, passwordError = null) }
    }

    fun login() {
        val state = _uiState.value
        if (state.isLoading) return // cegah tombol ditekan dua kali

        val loginId = state.loginId.trim()
        val password = state.password

        // 1) Validasi field kosong (belum perlu menyentuh database)
        val idError = if (loginId.isEmpty()) "Wajib diisi" else null
        val pwError = if (password.isEmpty()) "Wajib diisi" else null
        if (idError != null || pwError != null) {
            _uiState.update { it.copy(loginIdError = idError, passwordError = pwError) }
            return
        }

        // 2) Cek ke database. login() adalah suspend function, jadi harus di coroutine.
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            when (val result = authRepository.login(role, loginId, password)) {
                is LoginResult.Success -> {
                    // Sesi sudah disimpan di dalam AuthRepository.login()
                    _uiState.update { it.copy(isLoading = false) }
                    _events.send(LoginEvent.Success(result.role))
                }
                LoginResult.WrongCredentials -> {
                    // Pesan seragam, tidak membocorkan apakah akunnya ada
                    _uiState.update {
                        it.copy(isLoading = false, passwordError = "Akun atau password salah")
                    }
                }
            }
        }
    }

    companion object {
        // "Resep" membuat ViewModel yang punya parameter
        fun factory(role: Role, authRepository: AuthRepository) = viewModelFactory {
            initializer { LoginViewModel(role, authRepository) }
        }
    }
}