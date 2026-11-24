package com.kel5.magangyuk

import android.app.Application
import com.kel5.magangyuk.data.local.AppDatabase
import com.kel5.magangyuk.data.local.DatabaseSeeder
import com.kel5.magangyuk.data.repository.AuthRepository
import com.kel5.magangyuk.data.repository.NotifikasiRepository
import com.kel5.magangyuk.data.session.SessionManager
import com.kel5.magangyuk.notification.NotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import com.kel5.magangyuk.data.repository.LowonganRepository
import com.kel5.magangyuk.data.repository.LowonganMahasiswaRepository
import com.kel5.magangyuk.data.repository.ProfilRepository

// Dijalankan Android satu kali setiap app dimulai, sebelum layar pertama muncul
class MagangYukApp : Application() {

    val database: AppDatabase by lazy { AppDatabase.getInstance(this) }
    val sessionManager: SessionManager by lazy { SessionManager(this) }

    // Satu AuthRepository untuk seluruh app (dipakai Splash dan Login)
    val authRepository: AuthRepository by lazy {
        AuthRepository(database.userDao(), sessionManager)
    }

    // Pintu tunggal untuk membuat notifikasi, dibuat sekali untuk seluruh app
    val notifikasiRepository: NotifikasiRepository by lazy {
        NotifikasiRepository(this, database.notifikasiDao())
    }

    // Dipakai layar Profil (satu repository untuk ketiga role)
    val profilRepository: ProfilRepository by lazy {
        ProfilRepository(
            database.mahasiswaDao(),
            database.mitraDao(),
            database.dosenDao()
        )
    }

    val lowonganRepository: LowonganRepository by lazy {
        LowonganRepository(database.lowonganDao())
    }

    val lowonganMahasiswaRepository: LowonganMahasiswaRepository by lazy {
        LowonganMahasiswaRepository(
            database.lowonganDao(),
            database.bookmarkDao(),
            database.lamaranDao(),
            database.magangDao(),
            lowonganRepository
        )
    }

    // Splash menunggu tiket ini selesai (join) sebelum mengecek sesi,
    // supaya login pertama tidak gagal karena data belum terisi.
    lateinit var seedJob: Job
        private set

    override fun onCreate() {
        super.onCreate()

        // Buat channel notifikasi (satu channel: magangyuk_default)
        NotificationHelper.createChannel(this)

        // Isi data awal di thread IO. seedIfEmpty melewati proses kalau akun sudah ada.
        seedJob = CoroutineScope(Dispatchers.IO).launch {
            DatabaseSeeder.seedIfEmpty(database)
        }
    }
}