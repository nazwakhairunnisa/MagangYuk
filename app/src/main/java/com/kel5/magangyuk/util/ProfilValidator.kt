package com.kel5.magangyuk.util

import android.util.Patterns
import com.kel5.magangyuk.data.local.entity.Role
import com.kel5.magangyuk.data.repository.ProfilFieldKey

object ProfilValidator {

    // 08 atau +62, lalu 8-11 digit lagi => total 10-13 digit (spesifikasi D.1)
    private val PHONE_MAHASISWA_DOSEN = Regex("^(08|\\+62)\\d{8,11}$")

    // TODO: menunggu konfirmasi keputusan #1. Mitra lebih longgar karena
    // di Figma nomornya telepon kantor (061 ...).
    private val PHONE_MITRA = Regex("^\\+?\\d{8,15}$")

    // Field mana saja yang bisa diedit tiap role (spesifikasi D.1, E.1, F.1)
    fun editableKeys(role: Role): List<ProfilFieldKey> = when (role) {
        Role.MITRA -> listOf(
            ProfilFieldKey.ALAMAT, ProfilFieldKey.DESKRIPSI,
            ProfilFieldKey.TELEPON, ProfilFieldKey.EMAIL, ProfilFieldKey.WEBSITE
        )
        else -> listOf(ProfilFieldKey.TELEPON, ProfilFieldKey.EMAIL)
    }

    // Buang spasi dan strip, karena di Figma nomor ditulis "0813 1122 3344"
    fun normalizePhone(raw: String): String =
        raw.trim().replace(" ", "").replace("-", "")

    // Mengembalikan peta key -> pesan error. Kosong berarti semua valid.
    fun validate(role: Role, values: Map<ProfilFieldKey, String>): Map<ProfilFieldKey, String> {
        val errors = mutableMapOf<ProfilFieldKey, String>()

        for (key in editableKeys(role)) {
            val value = values[key].orEmpty().trim()
            when (key) {
                ProfilFieldKey.WEBSITE -> Unit // opsional, tidak divalidasi

                ProfilFieldKey.TELEPON -> {
                    val phone = normalizePhone(value)
                    val regex = if (role == Role.MITRA) PHONE_MITRA else PHONE_MAHASISWA_DOSEN
                    if (phone.isEmpty()) {
                        errors[key] = "Wajib diisi"
                    } else if (!regex.matches(phone)) {
                        errors[key] = if (role == Role.MITRA) {
                            "Nomor telepon tidak valid"
                        } else {
                            "Nomor tidak valid. Pakai 08xx atau +62, 10-13 digit"
                        }
                    }
                }

                ProfilFieldKey.EMAIL -> {
                    if (value.isEmpty()) {
                        errors[key] = "Wajib diisi"
                    } else if (!Patterns.EMAIL_ADDRESS.matcher(value).matches()) {
                        errors[key] = "Format email tidak valid"
                    }
                }

                else -> if (value.isEmpty()) errors[key] = "Wajib diisi" // alamat, deskripsi
            }
        }
        return errors
    }
}