package com.kel5.magangyuk.data.local.dao

import androidx.room.Dao
import androidx.room.Embedded
import androidx.room.Insert
import androidx.room.Query
import com.kel5.magangyuk.data.local.entity.LowonganEntity
import kotlinx.coroutines.flow.Flow
import androidx.room.Delete
import androidx.room.Update

@Dao
interface LowonganDao {

    // Dipakai seed untuk mengisi contoh lowongan. Mengembalikan id yang dibuat Room.
    @Insert
    suspend fun insert(lowongan: LowonganEntity): Long

    // Dipakai untuk mengecek jumlah lowongan di database
    @Query("SELECT COUNT(*) FROM lowongan")
    suspend fun count(): Int

    // Daftar lowongan milik satu mitra untuk menu "Lowongan Saya".
    // Return Flow = "aliran data": kalau isi tabel berubah (tambah/sunting/tutup),
    // Room otomatis mengirim daftar terbaru, jadi layar ikut ter-update sendiri.
    // Dua subquery menghitung angka dari tabel lamaran:
    // - terisi: lamaran berstatus DITERIMA atau DIKONFIRMASI (sesuai aturan kuota)
    // - jumlahPelamar: semua lamaran untuk lowongan itu
    @Query(
        """
        SELECT l.*,
          (SELECT COUNT(*) FROM lamaran
             WHERE lowonganId = l.id
               AND status IN ('DITERIMA', 'DIKONFIRMASI')) AS terisi,
          (SELECT COUNT(*) FROM lamaran
             WHERE lowonganId = l.id) AS jumlahPelamar
        FROM lowongan l
        WHERE l.mitraId = :mitraId
        ORDER BY l.id DESC
        """
    )
    fun observeByMitra(mitraId: Long): Flow<List<LowonganDenganStatistik>>

    // Ambil satu lowongan, dipakai form saat mode sunting
    @Query("SELECT * FROM lowongan WHERE id = :id")
    suspend fun getById(id: Long): LowonganEntity?

    // Simpan perubahan lowongan. Room mencocokkan baris lewat primary key (id).
    @Update
    suspend fun update(lowongan: LowonganEntity)

    // Hapus permanen. Hanya boleh dipanggil kalau belum ada lamaran (dijaga di Repository).
    @Delete
    suspend fun delete(lowongan: LowonganEntity)

    // Tutup lowongan secara manual, barisnya tetap ada
    @Query("UPDATE lowongan SET ditutupManual = 1 WHERE id = :id")
    suspend fun tutupManual(id: Long)

    // Jumlah semua lamaran untuk satu lowongan (penentu: Hapus atau Tutup)
    @Query("SELECT COUNT(*) FROM lamaran WHERE lowonganId = :lowonganId")
    suspend fun countLamaran(lowonganId: Long): Int

    // Jumlah slot terpakai, dipakai validasi "kuota tidak boleh lebih kecil dari slot terpakai"
    @Query(
        """
        SELECT COUNT(*) FROM lamaran
        WHERE lowonganId = :lowonganId AND status IN ('DITERIMA', 'DIKONFIRMASI')
        """
    )
    suspend fun countTerisi(lowonganId: Long): Int
}

// Hasil query di atas: satu lowongan + dua angka hitungan.
// @Embedded artinya semua kolom lowongan dimasukkan ke properti `lowongan`,
// sedangkan kolom terisi dan jumlahPelamar dicocokkan lewat nama.
data class LowonganDenganStatistik(
    @Embedded val lowongan: LowonganEntity,
    val terisi: Int,
    val jumlahPelamar: Int
)