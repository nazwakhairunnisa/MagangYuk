package com.kel5.magangyuk.ui.profil

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kel5.magangyuk.MagangYukApp
import com.kel5.magangyuk.data.local.entity.Role
import com.kel5.magangyuk.data.repository.ProfilData
import com.kel5.magangyuk.data.repository.ProfilFieldKey
import com.kel5.magangyuk.data.repository.ReadOnlyField
import com.kel5.magangyuk.ui.common.AppButton
import com.kel5.magangyuk.ui.common.AppButtonStyle
import com.kel5.magangyuk.ui.common.AppTextField
import com.kel5.magangyuk.ui.common.ChipTone
import com.kel5.magangyuk.ui.common.StatusChip
import com.kel5.magangyuk.ui.theme.CreamLight
import com.kel5.magangyuk.ui.theme.CreamYellow
import com.kel5.magangyuk.ui.theme.GrayCream
import com.kel5.magangyuk.ui.theme.Navy
import com.kel5.magangyuk.ui.theme.StatusGrayText
import com.kel5.magangyuk.ui.theme.White

// Pintu masuk layar: membuat ViewModel, lalu meneruskan state ke ProfilScreen.
// onBack = panah kembali, onLogout = tombol Keluar Akun (diurus RoleScaffold).
@Composable
fun ProfilRoute(
    onBack: () -> Unit,
    onLogout: () -> Unit
) {
    val app = LocalContext.current.applicationContext as MagangYukApp
    val viewModel: ProfilViewModel = viewModel(
        factory = ProfilViewModel.factory(app.profilRepository, app.sessionManager)
    )
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Tiap ada pesan baru: tampilkan snackbar, lalu kosongkan pesannya
    LaunchedEffect(state.message) {
        val msg = state.message ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(msg)
        viewModel.messageShown()
    }

    // Saat mode edit, tombol back sistem membatalkan edit dulu (bukan langsung keluar)
    BackHandler(enabled = state.isEditing) { viewModel.cancelEdit() }

    ProfilScreen(
        state = state,
        snackbarHostState = snackbarHostState,
        onBack = { if (state.isEditing) viewModel.cancelEdit() else onBack() },
        onEdit = viewModel::startEdit,
        onFieldChange = viewModel::onFieldChange,
        onSave = viewModel::save,
        onCancel = viewModel::cancelEdit,
        onLogout = onLogout
    )
}

@Composable
private fun ProfilScreen(
    state: ProfilUiState,
    snackbarHostState: SnackbarHostState,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onFieldChange: (ProfilFieldKey, String) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit,
    onLogout: () -> Unit
) {
    Box(Modifier.fillMaxSize().background(CreamLight)) {
        Column(Modifier.fillMaxSize()) {
            ProfilTopBar(title = topBarTitle(state.data?.role), onBack = onBack)

            val data = state.data
            when {
                state.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Navy)
                }
                data == null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Data profil tidak ditemukan", color = StatusGrayText)
                }
                else -> ProfilContent(
                    data = data,
                    state = state,
                    onEdit = onEdit,
                    onFieldChange = onFieldChange,
                    onSave = onSave,
                    onCancel = onCancel,
                    onLogout = onLogout,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp)
        )
    }
}

// Top bar sendiri (AppHeader global disembunyikan di rute Profil)
@Composable
private fun ProfilTopBar(title: String, onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp)
            .background(Navy)
            .statusBarsPadding() // background dulu, supaya navy ikut menutupi area status bar
            .padding(horizontal = 8.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali", tint = CreamYellow)
        }
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = CreamYellow
        )
    }
}

@Composable
private fun ProfilContent(
    data: ProfilData,
    state: ProfilUiState,
    onEdit: () -> Unit,
    onFieldChange: (ProfilFieldKey, String) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ProfilHeader(data)

        // Kartu 1: data terkunci (read-only)
        ProfilCard(
            title = akademikTitle(data.role),
            caption = akademikCaption(data.role)
        ) {
            data.readOnlyFields.forEach { LockedField(it) }
        }

        // Kartu 2: data yang boleh diubah. Mode lihat = kotak nilai, mode edit = field ketik.
        ProfilCard(title = kontakTitle(data.role)) {
            val lastIndex = data.editableFields.lastIndex
            data.editableFields.forEachIndexed { index, field ->
                if (state.isEditing) {
                    AppTextField(
                        value = state.draft[field.key].orEmpty(),
                        onValueChange = { onFieldChange(field.key, it) },
                        label = if (field.required) "${field.label} *" else field.label,
                        error = state.errors[field.key],
                        keyboardType = keyboardFor(field.key),
                        imeAction = if (index == lastIndex) ImeAction.Done else ImeAction.Next,
                        singleLine = !field.multiline,
                        minLines = if (field.multiline) 3 else 1,
                        leadingIcon = iconFor(field.key),
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    ValueBox(
                        label = field.label,
                        value = field.value,
                        icon = iconFor(field.key)
                    )
                }
            }
        }

        // Tombol berubah sesuai mode
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            if (state.isEditing) {
                AppButton(
                    text = "Simpan",
                    onClick = onSave,
                    loading = state.isSaving,
                    modifier = Modifier.fillMaxWidth()
                )
                AppButton(
                    text = "Batal",
                    onClick = onCancel,
                    style = AppButtonStyle.NEUTRAL_OUTLINE,
                    enabled = !state.isSaving,
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                AppButton(
                    text = "Edit Profil",
                    onClick = onEdit,
                    trailingIcon = Icons.Default.Edit,
                    modifier = Modifier.fillMaxWidth()
                )
                AppButton(
                    text = "Keluar Akun",
                    onClick = onLogout,
                    style = AppButtonStyle.DESTRUCTIVE_OUTLINE,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

// Avatar bulat + nama + subtitle + chip status
@Composable
private fun ProfilHeader(data: ProfilData) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.size(96.dp).background(CreamYellow, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (data.role == Role.MITRA) Icons.Default.Business else Icons.Default.Person,
                contentDescription = null,
                tint = Navy,
                modifier = Modifier.size(48.dp)
            )
        }
        Spacer(Modifier.height(16.dp))
        Text(
            text = data.nama,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = Navy,
            textAlign = TextAlign.Center
        )
        if (data.subtitle != null) {
            Text(
                text = data.subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = StatusGrayText
            )
        }
        Spacer(Modifier.height(8.dp))
        StatusChip(text = data.statusLabel, tone = ChipTone.BLUE)
    }
}

// Kartu putih pembungkus satu kelompok field
@Composable
private fun ProfilCard(
    title: String,
    modifier: Modifier = Modifier,
    caption: String? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(White, RoundedCornerShape(20.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Column {
            Text(title, style = MaterialTheme.typography.titleMedium, color = Navy)
            if (caption != null) {
                Text(caption, style = MaterialTheme.typography.bodySmall, color = StatusGrayText)
            }
        }
        content()
    }
}

// Field terkunci: latar abu krem + ikon gembok
@Composable
private fun LockedField(field: ReadOnlyField) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(GrayCream, RoundedCornerShape(16.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(field.label, style = MaterialTheme.typography.bodySmall, color = StatusGrayText)
            Text(
                field.value,
                style = MaterialTheme.typography.titleSmall,
                color = Navy
            )
        }
        Icon(Icons.Default.Lock, contentDescription = "Terkunci", tint = StatusGrayText, modifier = Modifier.size(20.dp))
    }
}

// Tampilan nilai field saat belum mode edit (label + kotak putih tipis)
@Composable
private fun ValueBox(label: String, value: String, icon: ImageVector?) {
    val shape = RoundedCornerShape(16.dp)
    Column {
        Text(label, style = MaterialTheme.typography.titleSmall, color = Navy)
        Row(
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxWidth()
                .shadow(1.dp, shape)
                .background(White, shape)
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                Icon(icon, contentDescription = null, tint = StatusGrayText, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(12.dp))
            }
            Text(
                // website opsional bisa kosong, tampilkan strip
                text = value.ifBlank { "-" },
                style = MaterialTheme.typography.bodyLarge,
                color = Navy
            )
        }
    }
}

// ---- Teks dan ikon per role/field ----

private fun topBarTitle(role: Role?) = when (role) {
    Role.MAHASISWA -> "Profil Mahasiswa"
    Role.DOSEN -> "Profil Dosen"
    else -> "Profil"
}

private fun akademikTitle(role: Role) = when (role) {
    Role.MAHASISWA -> "Data Akademik"
    Role.DOSEN -> "Data Akademik"
    Role.MITRA -> "Data Perusahaan"
}

private fun akademikCaption(role: Role): String? = when (role) {
    Role.MAHASISWA -> "Data terverifikasi kampus"
    Role.MITRA -> "Untuk memperbarui data tervalidasi di atas, ajukan verifikasi ulang via PIC Mitra Vokasi kampus."
    Role.DOSEN -> null
}

private fun kontakTitle(role: Role) =
    if (role == Role.MITRA) "Data Kontak & Alamat" else "Data Kontak"

private fun iconFor(key: ProfilFieldKey): ImageVector? = when (key) {
    ProfilFieldKey.TELEPON -> Icons.Default.Phone
    ProfilFieldKey.EMAIL -> Icons.Default.Email
    ProfilFieldKey.WEBSITE -> Icons.Default.Language
    else -> null // alamat dan deskripsi tanpa ikon
}

private fun keyboardFor(key: ProfilFieldKey) = when (key) {
    ProfilFieldKey.TELEPON -> KeyboardType.Phone
    ProfilFieldKey.EMAIL -> KeyboardType.Email
    ProfilFieldKey.WEBSITE -> KeyboardType.Uri
    else -> KeyboardType.Text
}