package com.kel5.magangyuk.data.repository

import android.database.sqlite.SQLiteConstraintException
import androidx.room.withTransaction
import com.kel5.magangyuk.data.local.AppDatabase
import com.kel5.magangyuk.data.local.entity.LamaranEntity
import com.kel5.magangyuk.data.local.entity.StatusLamaran
import com.kel5.magangyuk.data.local.entity.TipeNotifikasi
import com.kel5.magangyuk.util.FileStorage
import com.kel5.magangyuk.util.FileTerpilih
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.first

// Data untuk bagian atas form (read-only) + bahan validasi
data class InfoFormLamaran(
    val nama: String,
    val nim: String,
    val prodi: String,
    val email: String,
    val telepon: String,      // nilai awal dari profil, boleh diubah di form
    val posisi: String,
    val namaPerusahaan: String,
    val wajibKtm: Boolean
)

// Isian yang diketik/dipilih user
data class InputLamaran(
    val telepon: String,
    val cv: FileTerpilih?,
    val ktm: FileTerpilih?
)

sealed class HasilKirimLamaran {
    data class Sukses(val lamaranId: Long) : HasilKirimLamaran()
    // Salah isi field -> pesan ditampilkan di bawah field yang bersangkutan
    data class GagalField(val errors: Map<String, String>) : HasilKirimLamaran()
    // Melanggar aturan bisnis (dobel, 3 aktif, ditutup, dst) -> satu pesan umum
    data class Gagal(val pesan: String) : HasilKirimLamaran()
}

class LamaranRepository(
    private val db: AppDatabase,
    private val lowonganRepository: LowonganRepository,       // punya B: sumber hitungStatus
    private val notifikasiRepository: NotifikasiRepository,
    private val fileStorage: FileStorage
) {
    private val lamaranDao = db.lamaranDao()
    private val mahasiswaDao = db.mahasiswaDao()
    private val lowonganDao = db.lowonganDao()
    private val magangDao = db.magangDao()

    // Bahan untuk membuka form. Null = lowongan atau mahasiswa tidak ditemukan.
    suspend fun muatForm(mahasiswaId: Long, lowonganId: Long): InfoFormLamaran? {
        val mhs = mahasiswaDao.findByUserId(mahasiswaId) ?: return null
        // first() = ambil satu nilai pertama dari Flow lalu berhenti (kita cukup sekali baca)
        val detail = lowonganDao.observeDetailUntukMahasiswa(lowonganId, mahasiswaId).first()
            ?: return null
        return InfoFormLamaran(
            nama = mhs.nama,
            nim = mhs.nim,
            prodi = mhs.prodi,
            email = mhs.email,
            telepon = mhs.telepon,
            posisi = detail.lowongan.posisi,
            namaPerusahaan = detail.namaPerusahaan,
            wajibKtm = detail.lowongan.wajibKtm
        )
    }

    suspend fun kirim(
        mahasiswaId: Long,
        lowonganId: Long,
        input: InputLamaran
    ): HasilKirimLamaran {
        val lowonganAwal = lowonganDao.getById(lowonganId)
            ?: return HasilKirimLamaran.Gagal("Lowongan tidak ditemukan")

        // Validasi field dulu (murah, tanpa transaksi)
        val errors = validasiFormLamaran(
            input.telepon, input.cv, input.ktm, lowonganAwal.wajibKtm
        )
        if (errors.isNotEmpty()) return HasilKirimLamaran.GagalField(errors)

        // Path file yang sudah disalin, dicatat supaya bisa dihapus kalau transaksi gagal
        val salinan = mutableListOf<String>()

        return try {
            db.withTransaction {
                // ---- Validasi ulang aturan bisnis, dibaca ulang dari database ----
                val mhs = mahasiswaDao.findByUserId(mahasiswaId)
                    ?: return@withTransaction HasilKirimLamaran.Gagal("Data mahasiswa tidak ditemukan")
                val lowongan = lowonganDao.getById(lowonganId)
                    ?: return@withTransaction HasilKirimLamaran.Gagal("Lowongan tidak ditemukan")
                val terisi = lowonganDao.countTerisi(lowonganId)
                val status = lowonganRepository.hitungStatus(lowongan, terisi)

                // Urutan sama dengan prioritas tombol di halaman detail
                val pelanggaran = when {
                    lamaranDao.sudahMelamar(mahasiswaId, lowonganId) ->
                        "Kamu sudah melamar lowongan ini"
                    magangDao.punyaMagang(mahasiswaId) ->
                        "Kamu sudah memiliki magang"
                    status == StatusLowongan.TUTUP ->
                        "Lowongan sudah ditutup"
                    lamaranDao.countAktif(mahasiswaId) >= MAKS_LAMARAN_AKTIF ->
                        "Batas $MAKS_LAMARAN_AKTIF lamaran aktif tercapai"
                    else -> null
                }
                if (pelanggaran != null) return@withTransaction HasilKirimLamaran.Gagal(pelanggaran)

                // ---- Semua aman: salin file, simpan lamaran, kirim notifikasi ----
                val prefix = "${mahasiswaId}_$lowonganId"
                val cvPath = fileStorage.salin(input.cv!!, "lamaran", "cv_$prefix")
                    .also { salinan += it }
                val ktmPath = input.ktm?.let {
                    fileStorage.salin(it, "lamaran", "ktm_$prefix").also { p -> salinan += p }
                }

                val lamaranId = lamaranDao.insert(
                    LamaranEntity(
                        mahasiswaId = mahasiswaId,
                        lowonganId = lowonganId,
                        tglKirim = System.currentTimeMillis(),
                        status = StatusLamaran.MENUNGGU,
                        telepon = input.telepon.trim(),
                        cvPath = cvPath,
                        ktmPath = ktmPath
                    )
                )

                // Notifikasi ke MITRA pemilik lowongan. refId = lamaranId (data model).
                // lowongan.mitraId = mitra.userId = id akun mitra, jadi langsung jadi userId penerima.
                notifikasiRepository.kirim(
                    userId = lowongan.mitraId,
                    tipe = TipeNotifikasi.LAMARAN_BARU,
                    judul = "Pelamar baru",
                    pesan = "${mhs.nama} melamar posisi ${lowongan.posisi}.",
                    refId = lamaranId
                )

                HasilKirimLamaran.Sukses(lamaranId)
            }.also { hasil ->
                // Aturan ditolak (bukan error): tidak ada yang disimpan, tapi file belum
                // sempat disalin juga, jadi tidak ada yang perlu dibersihkan di sini.
                if (hasil !is HasilKirimLamaran.Sukses) salinan.forEach(fileStorage::hapus)
            }
        } catch (e: CancellationException) {
            salinan.forEach(fileStorage::hapus)
            throw e   // pembatalan coroutine harus diteruskan, jangan ditelan
        } catch (e: SQLiteConstraintException) {
            // Jaring pengaman: index unik menolak lamaran dobel
            salinan.forEach(fileStorage::hapus)
            HasilKirimLamaran.Gagal("Kamu sudah melamar lowongan ini")
        } catch (e: Exception) {
            salinan.forEach(fileStorage::hapus)
            HasilKirimLamaran.Gagal("Gagal mengirim lamaran. Coba lagi.")
        }
    }
}