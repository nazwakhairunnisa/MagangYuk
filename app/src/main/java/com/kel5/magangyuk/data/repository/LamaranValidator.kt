package com.kel5.magangyuk.data.repository

import com.kel5.magangyuk.util.FileTerpilih

// Batas ukuran CV (spesifikasi D.4)
const val MAKS_UKURAN_CV_BYTE = 5L * 1024 * 1024

// Nomor 08xx atau +62, total 10-13 digit (spesifikasi D.1).
// 08 + 8..11 digit = 10..13 digit | +62 + 8..11 digit = 10..13 digit (62 ikut dihitung)
private val POLA_TELEPON = Regex("""^(\+62|08)\d{8,11}$""")

fun validasiTelepon(telepon: String): String? = when {
    telepon.isBlank() -> "Wajib diisi"
    !POLA_TELEPON.matches(telepon.trim()) -> "Nomor tidak valid (08xx atau +62, 10-13 digit)"
    else -> null
}

private fun FileTerpilih.isPdf() =
    mimeType == "application/pdf" || nama.endsWith(".pdf", ignoreCase = true)

private fun FileTerpilih.isGambar() = mimeType?.startsWith("image/") == true

fun validasiCv(cv: FileTerpilih?): String? = when {
    cv == null -> "Wajib diisi"
    !cv.isPdf() -> "CV harus berformat PDF"
    cv.ukuranByte > MAKS_UKURAN_CV_BYTE -> "Ukuran CV maksimal 5 MB"
    else -> null
}

// KTM: wajib hanya jika perusahaan mengaktifkan "Wajib KTM", selain itu boleh kosong.
// Kalau diisi, formatnya harus foto atau PDF.
fun validasiKtm(ktm: FileTerpilih?, wajib: Boolean): String? = when {
    ktm == null -> if (wajib) "Wajib diisi" else null
    !(ktm.isPdf() || ktm.isGambar()) -> "KTM harus berupa foto atau PDF"
    else -> null
}

// Kunci map = nama field: "telepon", "cv", "ktm". Map kosong = semua valid.
fun validasiFormLamaran(
    telepon: String,
    cv: FileTerpilih?,
    ktm: FileTerpilih?,
    wajibKtm: Boolean
): Map<String, String> {
    val errors = mutableMapOf<String, String>()
    validasiTelepon(telepon)?.let { errors["telepon"] = it }
    validasiCv(cv)?.let { errors["cv"] = it }
    validasiKtm(ktm, wajibKtm)?.let { errors["ktm"] = it }
    return errors
}