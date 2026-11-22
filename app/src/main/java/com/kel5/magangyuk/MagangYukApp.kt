package com.kel5.magangyuk

import com.kel5.magangyuk.data.repository.NotifikasiRepository
import com.kel5.magangyuk.notification.NotificationHelper
import com.kel5.magangyuk.data.session.SessionManager
import android.app.Application
import com.kel5.magangyuk.data.local.AppDatabase
import com.kel5.magangyuk.data.local.DatabaseSeeder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.kel5.magangyuk.data.repository.LowonganRepository

// Dijalankan Android satu kali setiap app dimulai, sebelum layar pertama muncul
class MagangYukApp : Application() {

    // lazy = database baru dibuat saat pertama kali dipakai
    val database: AppDatabase by lazy { AppDatabase.getInstance(this) }

    // Pencatat sesi login (siapa yang sedang login), dibuat sekali untuk seluruh app
    val sessionManager: SessionManager by lazy { SessionManager(this) }

    // Pintu tunggal untuk membuat notifikasi, dibuat sekali untuk seluruh app
    val notifikasiRepository: NotifikasiRepository by lazy {
        NotifikasiRepository(this, database.notifikasiDao())
    }

    val lowonganRepository: LowonganRepository by lazy {
        LowonganRepository(database.lowonganDao())
    }

    override fun onCreate() {
        super.onCreate()

        // Buat channel notifikasi (satu channel: magangyuk_default)
        NotificationHelper.createChannel(this)

        // Isi data awal di thread IO. seedIfEmpty melewati proses kalau akun sudah ada.
        CoroutineScope(Dispatchers.IO).launch {
            DatabaseSeeder.seedIfEmpty(database)
        }
    }
}