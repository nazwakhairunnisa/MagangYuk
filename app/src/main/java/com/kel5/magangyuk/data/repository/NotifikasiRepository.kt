package com.kel5.magangyuk.data.repository

import android.content.Context
import com.kel5.magangyuk.data.local.dao.NotifikasiDao
import com.kel5.magangyuk.data.local.entity.NotifikasiEntity
import com.kel5.magangyuk.data.local.entity.TipeNotifikasi
import com.kel5.magangyuk.notification.NotificationHelper
import kotlinx.coroutines.flow.Flow

class NotifikasiRepository(
    private val context: Context,
    private val dao: NotifikasiDao
) {

    suspend fun kirim(
        userId: Long,
        tipe: TipeNotifikasi,
        judul: String,
        pesan: String,
        refId: Long
    ) {
        val id = dao.insert(
            NotifikasiEntity(
                userId = userId,
                tipe = tipe,
                judul = judul,
                pesan = pesan,
                refId = refId,
                tglBuat = System.currentTimeMillis()
            )
        )
        NotificationHelper.show(context, id, userId, tipe, refId, judul, pesan)
    }

    suspend fun kirimSekali(
        userId: Long,
        tipe: TipeNotifikasi,
        judul: String,
        pesan: String,
        refId: Long
    ): Boolean {
        if (dao.exists(userId, tipe, refId)) return false
        kirim(userId, tipe, judul, pesan, refId)
        return true
    }

    fun getAll(userId: Long): Flow<List<NotifikasiEntity>> = dao.getAll(userId)

    fun countUnread(userId: Long): Flow<Int> = dao.countUnread(userId)

    suspend fun markAsRead(id: Long) = dao.markAsRead(id)

    suspend fun markAllAsRead(userId: Long) = dao.markAllAsRead(userId)
}