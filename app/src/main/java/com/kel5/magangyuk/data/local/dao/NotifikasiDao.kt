package com.kel5.magangyuk.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.kel5.magangyuk.data.local.entity.NotifikasiEntity
import com.kel5.magangyuk.data.local.entity.TipeNotifikasi
import kotlinx.coroutines.flow.Flow

@Dao
interface NotifikasiDao {

    // Simpan notifikasi baru, mengembalikan id yang dibuat Room
    @Insert
    suspend fun insert(notifikasi: NotifikasiEntity): Long

    // Daftar notifikasi satu user, terbaru di atas.
    // Flow = otomatis mengirim data baru setiap isi tabel berubah, jadi layar ikut ter-update.
    @Query("SELECT * FROM notifikasi WHERE userId = :userId ORDER BY tglBuat DESC")
    fun getAll(userId: Long): Flow<List<NotifikasiEntity>>

    // Jumlah belum dibaca, untuk badge lonceng (memakai index userId + dibaca)
    @Query("SELECT COUNT(*) FROM notifikasi WHERE userId = :userId AND dibaca = 0")
    fun countUnread(userId: Long): Flow<Int>

    // Tandai satu notifikasi sudah dibaca (notifikasi tidak dihapus, hanya ditandai)
    @Query("UPDATE notifikasi SET dibaca = 1 WHERE id = :id")
    suspend fun markAsRead(id: Long)

    // Tombol "Tandai semua dibaca"
    @Query("UPDATE notifikasi SET dibaca = 1 WHERE userId = :userId")
    suspend fun markAllAsRead(userId: Long)

    // Cek apakah kombinasi (userId, tipe, refId) sudah ada.
    // Dipakai agar TAWARAN_KEDALUWARSA dan MAGANG_SELESAI tidak dobel.
    @Query("SELECT EXISTS(SELECT 1 FROM notifikasi WHERE userId = :userId AND tipe = :tipe AND refId = :refId)")
    suspend fun exists(userId: Long, tipe: TipeNotifikasi, refId: Long): Boolean
}