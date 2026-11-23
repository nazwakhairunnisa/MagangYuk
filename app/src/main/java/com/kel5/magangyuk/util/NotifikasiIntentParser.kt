package com.kel5.magangyuk.util

import android.content.Intent
import com.kel5.magangyuk.data.local.entity.TipeNotifikasi
import com.kel5.magangyuk.notification.NotificationHelper

// Isi titipan dari notifikasi status bar
data class NotifikasiTap(
    val userId: Long,    // penerima notifikasi
    val tipe: TipeNotifikasi,
    val refId: Long      // belum dipakai, disiapkan untuk layar detail nanti
)

object NotifikasiIntentParser {
    // Mengembalikan null kalau intent ini bukan dari notifikasi (mis. dibuka lewat ikon / deep link login)
    fun fromIntent(intent: Intent?): NotifikasiTap? {
        if (intent == null || !intent.hasExtra(NotificationHelper.EXTRA_USER_ID)) return null

        val userId = intent.getLongExtra(NotificationHelper.EXTRA_USER_ID, -1L)
        val refId = intent.getLongExtra(NotificationHelper.EXTRA_REF_ID, -1L)
        val tipe = intent.getStringExtra(NotificationHelper.EXTRA_TIPE)
            ?.let { nama -> TipeNotifikasi.entries.firstOrNull { it.name == nama } }

        if (userId < 0 || tipe == null) return null
        return NotifikasiTap(userId, tipe, refId)
    }
}