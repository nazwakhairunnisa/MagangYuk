package com.kel5.magangyuk.ui.navigation

import com.kel5.magangyuk.data.local.entity.Role

// Semua "alamat" layar dikumpulkan di sini
object Routes {
    const val ARG_ROLE = "role"

    // ---- NavHost luar ----
    const val SPLASH = "splash"
    const val LOGIN = "login/{$ARG_ROLE}"   // pola rute (ada placeholder {role})
    const val MAIN = "main/{$ARG_ROLE}"

    // Fungsi pembuat alamat sungguhan, contoh: login(Role.MITRA) -> "login/MITRA"
    fun login(role: Role) = "login/${role.name}"
    fun main(role: Role) = "main/${role.name}"

    // ---- NavHost dalam: layar yang dipakai semua role ----
    const val NOTIFIKASI = "notifikasi"
    const val PROFIL = "profil"

    // ---- NavHost dalam: menu navbar per role ----
    // ATURAN: rute detail milik sebuah menu harus diawali rute menunya,
    // contoh "mhs/lowongan/detail/{id}". Dengan begitu menu "Lowongan" tetap
    // menyala di navbar saat kita ada di halaman detailnya.
    object Mahasiswa {
        const val BERANDA = "mhs/beranda"
        const val LOWONGAN = "mhs/lowongan"
        const val LAMARAN = "mhs/lamaran"
        const val MAGANG = "mhs/magang"

        const val ARG_LOWONGAN_ID = "id"
        const val DETAIL_LOWONGAN = "mhs/lowongan/detail/{$ARG_LOWONGAN_ID}"
        fun detailLowongan(id: Long) = "mhs/lowongan/detail/$id"
    }

    object Mitra {
        const val BERANDA = "mitra/beranda"
        const val LOWONGAN = "mitra/lowongan"
        const val PELAMAR = "mitra/pelamar"
        const val MAGANG = "mitra/magang"
    }

    object Dosen {
        const val BERANDA = "dosen/beranda"
        const val BIMBINGAN = "dosen/bimbingan"
        const val PENILAIAN = "dosen/penilaian"
    }
}