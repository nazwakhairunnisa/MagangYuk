package com.kel5.magangyuk.data.repository

import com.kel5.magangyuk.data.local.entity.Role

// Kunci field yang boleh diedit. Satu enum dipakai semua role (mahasiswa/dosen hanya memakai sebagian).
enum class ProfilFieldKey { ALAMAT, DESKRIPSI, TELEPON, EMAIL, WEBSITE }

// Field terkunci (ikon gembok di Figma)
data class ReadOnlyField(val label: String, val value: String)

// Field yang bisa diedit setelah tombol Edit Profil ditekan
data class EditableField(
    val key: ProfilFieldKey,
    val label: String,
    val value: String,
    val multiline: Boolean = false,
    val required: Boolean = true
)

// Bentuk data profil yang siap ditampilkan, sama untuk ketiga role.
// Layar tidak perlu tahu entity mana yang dipakai di belakangnya.
data class ProfilData(
    val role: Role,
    val nama: String,
    // Teks di bawah nama ("NIM ...", "NIDN ..."), null untuk mitra
    val subtitle: String?,
    val statusLabel: String,
    val readOnlyFields: List<ReadOnlyField>,
    val editableFields: List<EditableField>
)

// Hasil simpan, sealed class supaya ViewModel wajib menangani semua kemungkinan
sealed class SaveResult {
    data object Success : SaveResult()
    data object NotFound : SaveResult()
    data class Invalid(val errors: Map<ProfilFieldKey, String>) : SaveResult()
}