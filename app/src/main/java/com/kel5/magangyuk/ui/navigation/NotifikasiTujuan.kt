package com.kel5.magangyuk.ui.navigation

import com.kel5.magangyuk.data.local.entity.Role
import com.kel5.magangyuk.data.local.entity.TipeNotifikasi

// notifikasi membuka layar detail (Detail Pelamar, Detail Magang, dst) belum ada rutenya, jadi untuk sekarang
// diarahkan ke menu induknya. Setiap kali detailnya selesai dibuat, ganti baris yang bersangkutan.
// Role ikut dicek karena LAPORAN_DIUNGGAH dikirim ke dosen DAN mitra, tujuannya beda.
fun notifikasiTujuan(role: Role, tipe: TipeNotifikasi): String = when (tipe) {
    // Mitra
    TipeNotifikasi.LAMARAN_BARU,                // TODO: Detail Pelamar
    TipeNotifikasi.TAWARAN_DIKONFIRMASI,
    TipeNotifikasi.TAWARAN_DITOLAK -> Routes.Mitra.PELAMAR

    // Mahasiswa: menu Lamaran
    TipeNotifikasi.LAMARAN_DITERIMA,
    TipeNotifikasi.LAMARAN_DITOLAK,
    TipeNotifikasi.TAWARAN_KEDALUWARSA -> Routes.Mahasiswa.LAMARAN

    // Dosen atau Mitra: laporan baru diunggah
    TipeNotifikasi.LAPORAN_DIUNGGAH ->          // TODO: Detail Magang / Detail Penilaian, tab Laporan
        if (role == Role.DOSEN) Routes.Dosen.PENILAIAN else Routes.Mitra.MAGANG

    // Mahasiswa: menu Magang (TODO: buka tab Laporan / Penilaian)
    TipeNotifikasi.LAPORAN_DISETUJUI,
    TipeNotifikasi.LAPORAN_REVISI,
    TipeNotifikasi.NILAI_MITRA_TERKIRIM,
    TipeNotifikasi.NILAI_DOSEN_TERKIRIM -> Routes.Mahasiswa.MAGANG

    // Dosen
    TipeNotifikasi.MAGANG_SELESAI -> Routes.Dosen.PENILAIAN   // TODO: Detail Penilaian
}