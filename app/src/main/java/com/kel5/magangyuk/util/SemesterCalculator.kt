package com.kel5.magangyuk.util

import java.time.LocalDate

object SemesterCalculator {

    // Hitung semester dari NIM. Dua digit pertama NIM = tahun masuk (23 -> 2023).
    // Mengembalikan null kalau NIM tidak sesuai format, supaya layar tetap aman.
    // `today` dijadikan parameter supaya gampang dites dengan tanggal buatan.
    fun fromNim(nim: String, today: LocalDate = LocalDate.now()): Int? {
        val angkatan = nim.take(2).toIntOrNull() ?: return null
        val tahunMasuk = 2000 + angkatan

        // Tentukan kita ada di semester ganjil/genap tahun akademik yang mana:
        //  - Agu-Des   -> ganjil, tahun akademik mulai tahun ini
        //  - Januari   -> ganjil, tahun akademik mulai tahun lalu
        //  - Feb-Jul   -> genap,  tahun akademik mulai tahun lalu
        val bulan = today.monthValue
        val tahunAkademik = if (bulan >= 8) today.year else today.year - 1
        val urutanDalamTahun = if (bulan >= 8 || bulan == 1) 1 else 2

        // Tiap tahun akademik = 2 semester
        val semester = (tahunAkademik - tahunMasuk) * 2 + urutanDalamTahun
        return semester.coerceAtLeast(1)
    }
}