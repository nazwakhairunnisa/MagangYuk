package com.kel5.magangyuk.data.repository

import com.kel5.magangyuk.data.local.dao.DosenDao
import com.kel5.magangyuk.data.local.dao.MahasiswaDao
import com.kel5.magangyuk.data.local.dao.MitraDao
import com.kel5.magangyuk.data.local.entity.DosenEntity
import com.kel5.magangyuk.data.local.entity.MahasiswaEntity
import com.kel5.magangyuk.data.local.entity.MitraEntity
import com.kel5.magangyuk.data.local.entity.Role
import com.kel5.magangyuk.util.ProfilValidator
import com.kel5.magangyuk.util.SemesterCalculator

class ProfilRepository(
    private val mahasiswaDao: MahasiswaDao,
    private val mitraDao: MitraDao,
    private val dosenDao: DosenDao
) {

    // Ambil profil sesuai role. Null kalau datanya tidak ada di database.
    suspend fun load(userId: Long, role: Role): ProfilData? = when (role) {
        Role.MAHASISWA -> mahasiswaDao.findByUserId(userId)?.toProfilData()
        Role.MITRA -> mitraDao.findByUserId(userId)?.toProfilData()
        Role.DOSEN -> dosenDao.findByUserId(userId)?.toProfilData(
            jumlahBimbingan = mahasiswaDao.countByDosenId(userId)
        )
    }

    suspend fun save(
        userId: Long,
        role: Role,
        values: Map<ProfilFieldKey, String>
    ): SaveResult {
        // Validasi di sini (bukan di layar) supaya aturannya satu tempat
        val errors = ProfilValidator.validate(role, values)
        if (errors.isNotEmpty()) return SaveResult.Invalid(errors)

        val telepon = ProfilValidator.normalizePhone(values[ProfilFieldKey.TELEPON].orEmpty())
        val email = values[ProfilFieldKey.EMAIL].orEmpty().trim()

        // copy() hanya mengganti field yang disebut, field lain (nama, NIM, dst) tetap
        when (role) {
            Role.MAHASISWA -> {
                val old = mahasiswaDao.findByUserId(userId) ?: return SaveResult.NotFound
                mahasiswaDao.update(old.copy(telepon = telepon, email = email))
            }
            Role.DOSEN -> {
                val old = dosenDao.findByUserId(userId) ?: return SaveResult.NotFound
                dosenDao.update(old.copy(telepon = telepon, email = email))
            }
            Role.MITRA -> {
                val old = mitraDao.findByUserId(userId) ?: return SaveResult.NotFound
                mitraDao.update(
                    old.copy(
                        alamat = values[ProfilFieldKey.ALAMAT].orEmpty().trim(),
                        deskripsi = values[ProfilFieldKey.DESKRIPSI].orEmpty().trim(),
                        telepon = telepon,
                        email = email,
                        // website opsional: kosong disimpan sebagai null
                        website = values[ProfilFieldKey.WEBSITE].orEmpty().trim().ifBlank { null }
                    )
                )
            }
        }
        return SaveResult.Success
    }

    // ---- Mapper entity -> ProfilData. Label mengikuti Figma. ----

    private fun MahasiswaEntity.toProfilData() = ProfilData(
        role = Role.MAHASISWA,
        nama = nama,
        subtitle = "NIM $nim",
        statusLabel = SemesterCalculator.fromNim(nim)
            ?.let { "Mahasiswa Aktif · Semester $it" }
            ?: "Mahasiswa Aktif",
        readOnlyFields = listOf(
            ReadOnlyField("Nama Lengkap", nama),
            ReadOnlyField("NIM", nim),
            ReadOnlyField("Program Studi", prodi),
            ReadOnlyField("Fakultas", fakultas)
        ),
        editableFields = listOf(
            EditableField(ProfilFieldKey.TELEPON, "Nomor Telepon", telepon),
            EditableField(ProfilFieldKey.EMAIL, "Email", email)
        )
    )

    private fun DosenEntity.toProfilData(jumlahBimbingan: Int) = ProfilData(
        role = Role.DOSEN,
        nama = nama,
        subtitle = "NIDN $nidn",
        statusLabel = "Dosen Pembimbing Aktif",
        readOnlyFields = listOf(
            ReadOnlyField("Nama Lengkap", nama),
            ReadOnlyField("NIDN", nidn),
            ReadOnlyField("Jurusan / Program Studi", jurusan),
            ReadOnlyField("Jumlah Mahasiswa Bimbingan", "$jumlahBimbingan Mahasiswa")
        ),
        editableFields = listOf(
            EditableField(ProfilFieldKey.TELEPON, "Nomor Telepon", telepon),
            EditableField(ProfilFieldKey.EMAIL, "Email", email)
        )
    )

    private fun MitraEntity.toProfilData() = ProfilData(
        role = Role.MITRA,
        nama = namaPerusahaan,
        subtitle = null, // "Terdaftar sejak / ID Mitra" tidak dibuat
        statusLabel = "Mitra Industri Aktif",
        readOnlyFields = listOf(
            ReadOnlyField("Nama Perusahaan", namaPerusahaan),
            ReadOnlyField("Bidang Usaha", bidangUsaha)
        ),
        editableFields = listOf(
            EditableField(ProfilFieldKey.ALAMAT, "Alamat Kantor", alamat, multiline = true),
            EditableField(ProfilFieldKey.DESKRIPSI, "Deskripsi Singkat", deskripsi, multiline = true),
            EditableField(ProfilFieldKey.TELEPON, "Nomor Telepon", telepon),
            EditableField(ProfilFieldKey.EMAIL, "Email PIC / HRD", email),
            EditableField(ProfilFieldKey.WEBSITE, "Website Resmi", website.orEmpty(), required = false)
        )
    )
}