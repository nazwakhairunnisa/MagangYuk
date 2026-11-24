package com.kel5.magangyuk.data.repository

import com.kel5.magangyuk.data.local.dao.BookmarkDao
import com.kel5.magangyuk.data.local.dao.LowonganDao
import com.kel5.magangyuk.data.local.dao.LowonganUntukMahasiswa
import com.kel5.magangyuk.data.local.entity.BookmarkEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LowonganMahasiswaRepository(
    private val lowonganDao: LowonganDao,
    private val bookmarkDao: BookmarkDao,
    private val lowonganRepository: LowonganRepository // punya B: sumber aturan status
) {

    // Hanya lowongan berstatus AKTIF (aturan spesifikasi D.3).
    // Flow.map = "setiap kali Room kirim daftar baru, saring dulu sebelum diteruskan".
    fun observeAktif(mahasiswaId: Long): Flow<List<LowonganUntukMahasiswa>> =
        lowonganDao.observeUntukMahasiswa(mahasiswaId).map { daftar ->
            daftar.filter {
                lowonganRepository.hitungStatus(it.lowongan, it.terisi) == StatusLowongan.AKTIF
            }
        }

    // simpan = true -> tambah bookmark, false -> hapus.
    // Karena daftar berupa Flow, ikon bookmark otomatis ikut berubah setelah ini.
    suspend fun setBookmark(mahasiswaId: Long, lowonganId: Long, simpan: Boolean) {
        if (simpan) bookmarkDao.insert(BookmarkEntity(mahasiswaId, lowonganId))
        else bookmarkDao.delete(mahasiswaId, lowonganId)
    }
}