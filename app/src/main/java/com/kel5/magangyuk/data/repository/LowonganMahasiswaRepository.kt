package com.kel5.magangyuk.data.repository

import com.kel5.magangyuk.data.local.dao.BookmarkDao
import com.kel5.magangyuk.data.local.dao.LamaranDao
import com.kel5.magangyuk.data.local.dao.LowonganDao
import com.kel5.magangyuk.data.local.dao.LowonganUntukMahasiswa
import com.kel5.magangyuk.data.local.dao.MagangDao
import com.kel5.magangyuk.data.local.entity.BookmarkEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

// Batas lamaran aktif per mahasiswa (aturan spesifikasi G.Mahasiswa no. 2)
const val MAKS_LAMARAN_AKTIF = 3

// 5 kemungkinan tampilan tombol "Ajukan Lamaran"
enum class KondisiTombol { BISA_MELAMAR, SUDAH_MELAMAR, BATAS_LAMARAN, DITUTUP, SUDAH_PUNYA_MAGANG }

// Semua yang dibutuhkan halaman detail dalam satu objek
data class DetailLowongan(
    val item: LowonganUntukMahasiswa,
    val status: StatusLowongan,
    val sisaKuota: Int,
    val kondisiTombol: KondisiTombol
)

class LowonganMahasiswaRepository(
    private val lowonganDao: LowonganDao,
    private val bookmarkDao: BookmarkDao,
    private val lamaranDao: LamaranDao,
    private val magangDao: MagangDao,
    private val lowonganRepository: LowonganRepository // punya B: sumber aturan status
) {

    // Hanya lowongan berstatus AKTIF (aturan spesifikasi D.3).
    fun observeAktif(mahasiswaId: Long): Flow<List<LowonganUntukMahasiswa>> =
        lowonganDao.observeUntukMahasiswa(mahasiswaId).map { daftar ->
            daftar.filter {
                lowonganRepository.hitungStatus(it.lowongan, it.terisi) == StatusLowongan.AKTIF
            }
        }

    // Detail satu lowongan + kondisi tombol. combine = tiap salah satu dari 4 sumber berubah,
    // hitung ulang. Hasil null kalau lowongan tidak ditemukan.
    fun observeDetail(mahasiswaId: Long, lowonganId: Long): Flow<DetailLowongan?> =
        combine(
            lowonganDao.observeDetailUntukMahasiswa(lowonganId, mahasiswaId),
            lamaranDao.observeSudahMelamar(mahasiswaId, lowonganId),
            lamaranDao.observeCountAktif(mahasiswaId),
            magangDao.observePunyaMagang(mahasiswaId)
        ) { item, sudahMelamar, jumlahAktif, punyaMagang ->
            if (item == null) {
                null
            } else {
                // Status dihitung lewat fungsi B, sama persis dengan sisi mitra
                val status = lowonganRepository.hitungStatus(item.lowongan, item.terisi)
                // Urutan when = urutan prioritas, yang cocok duluan menang
                val kondisi = when {
                    sudahMelamar -> KondisiTombol.SUDAH_MELAMAR
                    punyaMagang -> KondisiTombol.SUDAH_PUNYA_MAGANG
                    status == StatusLowongan.TUTUP -> KondisiTombol.DITUTUP
                    jumlahAktif >= MAKS_LAMARAN_AKTIF -> KondisiTombol.BATAS_LAMARAN
                    else -> KondisiTombol.BISA_MELAMAR
                }
                DetailLowongan(
                    item = item,
                    status = status,
                    sisaKuota = (item.lowongan.kuota - item.terisi).coerceAtLeast(0),
                    kondisiTombol = kondisi
                )
            }
        }

    suspend fun setBookmark(mahasiswaId: Long, lowonganId: Long, simpan: Boolean) {
        if (simpan) bookmarkDao.insert(BookmarkEntity(mahasiswaId, lowonganId))
        else bookmarkDao.delete(mahasiswaId, lowonganId)
    }
}