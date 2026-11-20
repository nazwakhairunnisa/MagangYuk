package com.kel5.magangyuk.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kel5.magangyuk.data.local.entity.Role
import com.kel5.magangyuk.data.repository.AuthRepository
import com.kel5.magangyuk.data.session.SessionManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

// Tujuan setelah Splash selesai
sealed interface SplashDestination {
    data class Home(val role: Role) : SplashDestination
    data class Login(val role: Role) : SplashDestination
}

class SplashViewModel(
    private val sessionManager: SessionManager,
    private val authRepository: AuthRepository,
    private val seedJob: Job,
    private val linkRole: Role?        // role dari deep link, null kalau dibuka lewat ikon
) : ViewModel() {

    // null = masih memutuskan (layar tetap menampilkan logo)
    private val _destination = MutableStateFlow<SplashDestination?>(null)
    val destination: StateFlow<SplashDestination?> = _destination.asStateFlow()

    init {
        viewModelScope.launch { _destination.value = decide() }
    }

    private suspend fun decide(): SplashDestination = coroutineScope {
        // Timer minimal supaya logo tidak berkedip terlalu cepat. Berjalan paralel dengan
        // pengecekan di bawah, jadi total waktu = yang lebih lama di antara keduanya.
        val minShow = async { delay(MIN_SPLASH_MS) }

        seedJob.join()   // tunggu data awal selesai diisi
        val session = sessionManager.session.first()   // ambil nilai sesi saat ini sekali saja

        val sessionValid = session != null && authRepository.isSessionValid(session)
        // Sesi ada tapi akunnya sudah tidak ada di database (mis. database di-reset): bersihkan
        if (session != null && !sessionValid) authRepository.logout()

        // Aturan: pakai sesi kalau valid DAN (tidak ada link ATAU role link sama dengan role sesi).
        // Kalau link-nya role lain, arahkan ke Login role itu (untuk demo satu HP).
        val result = if (session != null && sessionValid &&
            (linkRole == null || linkRole == session.role)
        ) {
            SplashDestination.Home(session.role)
        } else {
            SplashDestination.Login(linkRole ?: Role.MAHASISWA)   // default Mahasiswa
        }

        minShow.await()
        result
    }

    private companion object {
        const val MIN_SPLASH_MS = 1200L
    }
}