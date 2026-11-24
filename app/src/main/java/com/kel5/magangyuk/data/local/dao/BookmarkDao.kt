package com.kel5.magangyuk.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kel5.magangyuk.data.local.entity.BookmarkEntity

@Dao
interface BookmarkDao {

    // IGNORE: kalau pasangan yang sama sudah ada, diam saja (tidak crash)
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(bookmark: BookmarkEntity)

    @Query("DELETE FROM bookmark WHERE mahasiswaId = :mahasiswaId AND lowonganId = :lowonganId")
    suspend fun delete(mahasiswaId: Long, lowonganId: Long)
}