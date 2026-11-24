package com.kel5.magangyuk.data.local.dao

import androidx.room.Dao
import androidx.room.Query

@Dao
interface MagangDao {

    // Dipakai untuk kondisi tombol "Kamu sudah memiliki magang"
    @Query("SELECT EXISTS(SELECT 1 FROM magang WHERE mahasiswaId = :mahasiswaId)")
    suspend fun punyaMagang(mahasiswaId: Long): Boolean
}