package com.kel5.magangyuk.notification

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.kel5.magangyuk.MainActivity
import com.kel5.magangyuk.data.local.entity.TipeNotifikasi

// Satu-satunya tempat yang membuat notifikasi status bar.
// Repository tidak boleh membuatnya sendiri, hanya lewat NotifikasiRepository.kirim(...)
object NotificationHelper {

    const val CHANNEL_ID = "magangyuk_default"

    // Kunci data titipan di PendingIntent, dibaca nanti untuk menentukan layar tujuan
    const val EXTRA_USER_ID = "notif_user_id"
    const val EXTRA_TIPE = "notif_tipe"
    const val EXTRA_REF_ID = "notif_ref_id"

    // Dipanggil sekali di Application.onCreate. Aman dipanggil berulang.
    fun createChannel(context: Context) {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Notifikasi MagangYuk!",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Kabar terbaru soal lamaran, laporan, dan penilaian"
        }
        context.getSystemService(NotificationManager::class.java)
            .createNotificationChannel(channel)
    }

    // Tampilkan satu notifikasi di status bar
    @SuppressLint("MissingPermission") // izin sudah dicek manual di bawah
    fun show(
        context: Context,
        notifikasiId: Long,
        userId: Long,
        tipe: TipeNotifikasi,
        refId: Long,
        judul: String,
        pesan: String
    ) {
        // Android 13+ butuh izin POST_NOTIFICATIONS. Kalau belum diberikan, lewati.
        if (Build.VERSION.SDK_INT >= 33 &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) return

        // Ketuk notifikasi = buka app, membawa data penerima dan tujuan
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(EXTRA_USER_ID, userId)
            putExtra(EXTRA_TIPE, tipe.name)
            putExtra(EXTRA_REF_ID, refId)
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            notifikasiId.toInt(), // requestCode unik, supaya data tiap notifikasi tidak saling menimpa
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            // Ikon sementara bawaan Android, bisa diganti ikon sendiri nanti
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(judul)
            .setContentText(pesan)
            .setStyle(NotificationCompat.BigTextStyle().bigText(pesan))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true) // hilang dari status bar setelah diketuk
            .build()

        NotificationManagerCompat.from(context).notify(notifikasiId.toInt(), notification)
    }
}