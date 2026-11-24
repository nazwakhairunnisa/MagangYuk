package com.kel5.magangyuk.data.local.dao

import androidx.room.Dao
import androidx.room.Query

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
}