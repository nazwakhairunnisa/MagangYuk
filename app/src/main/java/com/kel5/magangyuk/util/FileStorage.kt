package com.kel5.magangyuk.util

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException

// File yang baru dipilih user, belum disalin ke mana-mana.
// Uri = "alamat" file di HP, hanya dipakai untuk membaca isinya.
data class FileTerpilih(
    val uri: Uri,
    val nama: String,
    val ukuranByte: Long,   // -1 kalau ukuran tidak diketahui
    val mimeType: String?
)

class FileStorage(private val context: Context) {

    // Ambil nama, ukuran, dan tipe file dari Uri hasil file picker.
    fun baca(uri: Uri): FileTerpilih {
        val resolver = context.contentResolver
        var nama = "file"
        var ukuran = -1L
        // query + OpenableColumns = cara standar Android menanyakan nama & ukuran file
        resolver.query(uri, null, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val iNama = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                val iUkuran = cursor.getColumnIndex(OpenableColumns.SIZE)
                if (iNama >= 0) nama = cursor.getString(iNama) ?: nama
                if (iUkuran >= 0 && !cursor.isNull(iUkuran)) ukuran = cursor.getLong(iUkuran)
            }
        }
        return FileTerpilih(uri, nama, ukuran, resolver.getType(uri))
    }

    // Salin ke filesDir/<folder>/ (penyimpanan internal, tidak hilang seperti cache).
    // Mengembalikan path absolut yang disimpan di kolom cvPath / ktmPath.
    suspend fun salin(file: FileTerpilih, folder: String, prefix: String): String =
        withContext(Dispatchers.IO) {   // kerja file itu berat, jangan di main thread
            val dir = File(context.filesDir, folder).apply { mkdirs() }
            val ekstensi = MimeTypeMap.getSingleton()
                .getExtensionFromMimeType(file.mimeType)
                ?: file.nama.substringAfterLast('.', "bin")
            // currentTimeMillis di nama file mencegah bentrok kalau user kirim ulang
            val tujuan = File(dir, "${prefix}_${System.currentTimeMillis()}.$ekstensi")

            val input = context.contentResolver.openInputStream(file.uri)
                ?: throw IOException("Tidak bisa membuka file")
            input.use { masuk -> tujuan.outputStream().use { keluar -> masuk.copyTo(keluar) } }
            tujuan.absolutePath
        }

    // Dipakai untuk membersihkan salinan kalau pengiriman gagal di tengah jalan
    fun hapus(path: String) {
        runCatching { File(path).delete() }
    }
}