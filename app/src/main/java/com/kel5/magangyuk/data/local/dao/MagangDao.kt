package com.kel5.magangyuk.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MagangDao {

    // Dipakai untuk kondisi tombol "Kamu sudah memiliki magang"
    @Query("SELECT EXISTS(SELECT 1 FROM magang WHERE mahasiswaId = :mahasiswaId)")
    suspend fun punyaMagang(mahasiswaId: Long): Boolean

    @Query("SELECT EXISTS(SELECT 1 FROM magang WHERE mahasiswaId = :mahasiswaId)")
    fun observePunyaMagang(mahasiswaId: Long): Flow<Boolean>
}