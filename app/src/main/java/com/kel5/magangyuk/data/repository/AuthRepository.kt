package com.kel5.magangyuk.data.repository

import com.kel5.magangyuk.data.local.dao.UserDao
import com.kel5.magangyuk.data.local.entity.Role
import com.kel5.magangyuk.data.session.Session
import com.kel5.magangyuk.data.session.SessionManager
import com.kel5.magangyuk.util.PasswordHasher

// Hasil login dibuat sealed class supaya ViewModel gampang menanganinya
sealed class LoginResult {
    data class Success(val userId: Long, val role: Role) : LoginResult()
    data object WrongCredentials : LoginResult()
}

class AuthRepository(
    private val userDao: UserDao,
    private val sessionManager: SessionManager
) {

    suspend fun login(role: Role, loginId: String, password: String): LoginResult {
        // Role sudah ikut dicek di query, jadi akun mahasiswa tidak bisa masuk lewat login mitra
        val user = userDao.findByLoginIdAndRole(loginId.trim(), role)

        // Pesan sengaja sama untuk "akun tidak ada" dan "password salah"
        // (spesifikasi: "Akun atau password salah"), supaya tidak membocorkan info akun
        if (user == null || !PasswordHasher.verify(password, user.passwordHash)) {
            return LoginResult.WrongCredentials
        }

        sessionManager.saveSession(user.id, user.role)
        return LoginResult.Success(user.id, user.role)
    }

    // Dipakai Splash: pastikan akun di sesi tersimpan masih ada di database.
    // Contoh kasus: database di-reset tapi DataStore masih menyimpan sesi lama.
    suspend fun isSessionValid(session: Session): Boolean =
        userDao.findById(session.userId) != null

    suspend fun logout() = sessionManager.clearSession()
}