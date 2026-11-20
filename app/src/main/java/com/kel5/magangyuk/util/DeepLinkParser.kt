package com.kel5.magangyuk.util

import android.content.Intent
import com.kel5.magangyuk.data.local.entity.Role

object DeepLinkParser {
    // magangyuk://login/mitra -> pathSegments = ["mitra"]
    // Mengembalikan null jika bukan link kita atau role tidak dikenal,
    // jadi app tetap jalan normal (default Mahasiswa).
    fun roleFromIntent(intent: Intent?): Role? {
        val uri = intent?.data ?: return null
        if (uri.scheme != "magangyuk" || uri.host != "login") return null
        return when (uri.pathSegments.firstOrNull()?.lowercase()) {
            "mahasiswa" -> Role.MAHASISWA
            "mitra" -> Role.MITRA
            "dosen" -> Role.DOSEN
            else -> null
        }
    }
}