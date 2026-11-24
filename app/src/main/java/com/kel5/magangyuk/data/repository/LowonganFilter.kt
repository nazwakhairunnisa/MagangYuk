package com.kel5.magangyuk.data.repository

import com.kel5.magangyuk.data.local.dao.LowonganUntukMahasiswa
import com.kel5.magangyuk.data.local.entity.JenisLowongan
import com.kel5.magangyuk.data.local.entity.ModeKerja

// Semua pilihan filter dalam satu objek. null / kosong = tidak difilter.
data class FilterLowongan(
    val kataKunci: String = "",
    val jurusan: String? = null,
    val lokasi: String? = null,      // nama kota, mis. "Medan"
    val jenis: JenisLowongan? = null,
    val mode: ModeKerja? = null
) {
    // Dipakai untuk menampilkan chip filter aktif
    val jumlahAktif: Int
        get() = listOfNotNull(jurusan, lokasi, jenis, mode).size
}

// Lokasi disimpan sebagai teks bebas ("Medan, Sumatera Utara"), jadi kota = bagian sebelum koma
fun kotaDari(lokasi: String): String = lokasi.substringBefore(",").trim()

// Fungsi murni (input -> output, tanpa database), jadi gampang dites dan dijelaskan
fun List<LowonganUntukMahasiswa>.terapkan(filter: FilterLowongan): List<LowonganUntukMahasiswa> =
    filter { item ->
        val l = item.lowongan
        val cocokKata = filter.kataKunci.isBlank() ||
                l.posisi.contains(filter.kataKunci.trim(), ignoreCase = true) ||
                item.namaPerusahaan.contains(filter.kataKunci.trim(), ignoreCase = true)
        val cocokJurusan = filter.jurusan == null || l.jurusan.equals(filter.jurusan, ignoreCase = true)
        val cocokLokasi = filter.lokasi == null || kotaDari(l.lokasi).equals(filter.lokasi, ignoreCase = true)
        val cocokJenis = filter.jenis == null || l.jenis == filter.jenis
        val cocokMode = filter.mode == null || l.mode == filter.mode
        cocokKata && cocokJurusan && cocokLokasi && cocokJenis && cocokMode
    }