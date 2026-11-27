package com.kel5.magangyuk.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.kel5.magangyuk.data.local.entity.LamaranEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LamaranDao {

    // Sudah pernah melamar lowongan ini? (status apa pun, sesuai aturan "tidak boleh dobel")
    @Query(
        """
        SELECT EXISTS(
            SELECT 1 FROM lamaran
            WHERE mahasiswaId = :mahasiswaId AND lowonganId = :lowonganId
        )
        """
    )
    suspend fun sudahMelamar(mahasiswaId: Long, lowonganId: Long): Boolean

    // Lamaran aktif = MENUNGGU atau DITERIMA (tawaran belum dikonfirmasi).
    // DIKONFIRMASI tidak dihitung karena sudah jadi magang.
    @Query(
        """
        SELECT COUNT(*) FROM lamaran
        WHERE mahasiswaId = :mahasiswaId AND status IN ('MENUNGGU', 'DITERIMA')
        """
    )
    suspend fun countAktif(mahasiswaId: Long): Int

    @Query(
        """
        SELECT EXISTS(
            SELECT 1 FROM lamaran
            WHERE mahasiswaId = :mahasiswaId AND lowonganId = :lowonganId
        )
        """
    )
    fun observeSudahMelamar(mahasiswaId: Long, lowonganId: Long): Flow<Boolean>

    @Query(
        """
        SELECT COUNT(*) FROM lamaran
        WHERE mahasiswaId = :mahasiswaId AND status IN ('MENUNGGU', 'DITERIMA')
        """
    )
    fun observeCountAktif(mahasiswaId: Long): Flow<Int>

    // Mengembalikan id lamaran baru. Kalau (mahasiswaId, lowonganId) sudah ada,
    // Room melempar SQLiteConstraintException karena index unik di entity.
    @Insert
    suspend fun insert(lamaran: LamaranEntity): Long
}
