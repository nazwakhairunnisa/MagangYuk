package com.kel5.magangyuk.data.repository

import com.kel5.magangyuk.data.local.dao.LowonganDao
import com.kel5.magangyuk.data.local.dao.LowonganDenganStatistik
import com.kel5.magangyuk.data.local.entity.LowonganEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

enum class StatusLowongan { AKTIF, TUTUP }

sealed class HasilSimpan {
    object Sukses : HasilSimpan()
    data class Gagal(val errors: Map<String, String>) : HasilSimpan()
}

enum class HasilHapus { DIHAPUS, DITUTUP }

class LowonganRepository(private val dao: LowonganDao) {

    fun observeByMitra(mitraId: Long): Flow<List<LowonganDenganStatistik>> =
        dao.observeByMitra(mitraId)

    suspend fun getById(id: Long): LowonganEntity? = dao.getById(id)

    fun hitungStatus(
        lowongan: LowonganEntity,
        terisi: Int,
        hariIni: LocalDate = LocalDate.now()
    ): StatusLowongan {
        val batas = parseTanggal(lowongan.batasDaftar)
        val lewatBatas = batas != null && hariIni.isAfter(batas)
        val penuh = terisi >= lowongan.kuota
        return if (lowongan.ditutupManual || lewatBatas || penuh) StatusLowongan.TUTUP
        else StatusLowongan.AKTIF
    }

    suspend fun simpan(lowongan: LowonganEntity): HasilSimpan {
        val errors = mutableMapOf<String, String>()
        val sedangSunting = lowongan.id != 0L

        if (lowongan.posisi.isBlank()) errors["posisi"] = "Wajib diisi"
        if (lowongan.divisi.isBlank()) errors["divisi"] = "Wajib diisi"
        if (lowongan.deskripsi.isBlank()) errors["deskripsi"] = "Wajib diisi"
        if (lowongan.persyaratan.isBlank()) errors["persyaratan"] = "Wajib diisi"
        if (lowongan.lokasi.isBlank()) errors["lokasi"] = "Wajib diisi"
        if (lowongan.pembimbingLapangan.isBlank()) errors["pembimbingLapangan"] = "Wajib diisi"

        if (lowongan.kuota < 1) {
            errors["kuota"] = "Kuota minimal 1"
        } else if (sedangSunting) {
            val terisi = dao.countTerisi(lowongan.id)
            if (lowongan.kuota < terisi) {
                errors["kuota"] = "Kuota tidak boleh kurang dari slot terpakai ($terisi)"
            }
        }

        val mulai = parseTanggal(lowongan.tglMulai)
        val selesai = parseTanggal(lowongan.tglSelesai)
        val batas = parseTanggal(lowongan.batasDaftar)

        if (mulai == null) errors["tglMulai"] = "Wajib diisi"
        if (selesai == null) errors["tglSelesai"] = "Wajib diisi"
        if (mulai != null && selesai != null && !selesai.isAfter(mulai)) {
            errors["tglSelesai"] = "Tanggal selesai harus setelah tanggal mulai"
        }

        if (batas == null) {
            errors["batasDaftar"] = "Wajib diisi"
        } else {
            if (mulai != null && !batas.isBefore(mulai)) {
                errors["batasDaftar"] = "Batas daftar harus sebelum tanggal mulai"
            } else {
                val batasLama = if (sedangSunting) dao.getById(lowongan.id)?.batasDaftar else null
                val berubah = batasLama != lowongan.batasDaftar
                if (berubah && batas.isBefore(LocalDate.now())) {
                    errors["batasDaftar"] = "Batas daftar tidak boleh di masa lalu"
                }
            }
        }

        if (errors.isNotEmpty()) return HasilSimpan.Gagal(errors)

        if (sedangSunting) dao.update(lowongan) else dao.insert(lowongan)
        return HasilSimpan.Sukses
    }

    suspend fun hapusAtauTutup(lowongan: LowonganEntity): HasilHapus {
        return if (dao.countLamaran(lowongan.id) == 0) {
            dao.delete(lowongan)
            HasilHapus.DIHAPUS
        } else {
            dao.tutupManual(lowongan.id)
            HasilHapus.DITUTUP
        }
    }

    private fun parseTanggal(teks: String): LocalDate? =
        runCatching { LocalDate.parse(teks) }.getOrNull()
}