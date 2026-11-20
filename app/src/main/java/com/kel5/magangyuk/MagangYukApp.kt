package com.kel5.magangyuk

import com.kel5.magangyuk.data.session.SessionManager
import android.app.Application
import com.kel5.magangyuk.data.local.AppDatabase
import com.kel5.magangyuk.data.local.DatabaseSeeder
import com.kel5.magangyuk.data.repository.AuthRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

// Dijalankan Android satu kali setiap app dimulai, sebelum layar pertama muncul
class MagangYukApp : Application() {

    val database: AppDatabase by lazy { AppDatabase.getInstance(this) }
    val sessionManager: SessionManager by lazy { SessionManager(this) }

    // satu AuthRepository untuk seluruh app (dipakai Splash dan Login)
    val authRepository: AuthRepository by lazy {
        AuthRepository(database.userDao(), sessionManager)
    }

    // Splash menunggu tiket ini selesai (join)
    // sebelum mengecek sesi, supaya login pertama tidak gagal karena data belum terisi.
    lateinit var seedJob: Job
        private set

    override fun onCreate() {
        super.onCreate()
        seedJob = CoroutineScope(Dispatchers.IO).launch {
            DatabaseSeeder.seedIfEmpty(database)
        }
    }
}