package com.kel5.magangyuk.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kel5.magangyuk.MagangYukApp
import com.kel5.magangyuk.data.local.entity.Role
import com.kel5.magangyuk.ui.common.AppButton
import com.kel5.magangyuk.ui.common.AppTextField
import com.kel5.magangyuk.ui.common.MagangLogo
import com.kel5.magangyuk.ui.common.RolePill
import com.kel5.magangyuk.ui.theme.CreamLight
import com.kel5.magangyuk.ui.theme.Navy
import com.kel5.magangyuk.ui.theme.StatusGrayText
import com.kel5.magangyuk.ui.theme.White

// Teks yang berbeda per role, dikumpulkan di satu tempat
private data class LoginTexts(
    val subtitle: String,
    val label: String,
    val placeholder: String,
    val keyboardType: KeyboardType
)

private fun Role.loginTexts() = when (this) {
    Role.MAHASISWA -> LoginTexts(
        subtitle = "Gunakan akun yang dibuat oleh pihak kampus.",
        label = "NIM",
        placeholder = "231402045",
        keyboardType = KeyboardType.Number
    )
    Role.MITRA -> LoginTexts(
        subtitle = "Gunakan akun perusahaan yang didaftarkan oleh pihak kampus.",
        label = "Email Perusahaan",
        placeholder = "hrd@abadisolusi.co.id",
        keyboardType = KeyboardType.Email
    )
    Role.DOSEN -> LoginTexts(
        subtitle = "Gunakan akun kampus dosen pembimbing.",
        label = "NIDN",
        placeholder = "0012068801",
        keyboardType = KeyboardType.Number
    )
}

// Tinggi blok navy di bagian atas (sesuai Figma: 668px / 2)
private val NavyBlockHeight = 334.dp

@Composable
fun LoginScreen(
    role: Role,
    onLoginSuccess: (Role) -> Unit,
    modifier: Modifier = Modifier
) {
    val app = LocalContext.current.applicationContext as MagangYukApp
    // key = role: kalau role berubah (deep link lain), ViewModel dibuat baru
    val viewModel: LoginViewModel = viewModel(
        key = "login_${role.name}",
        factory = LoginViewModel.factory(role, app.authRepository)
    )
    val state by viewModel.uiState.collectAsState()
    val texts = role.loginTexts()

    // Selalu pakai callback terbaru tanpa memulai ulang LaunchedEffect
    val currentOnSuccess by rememberUpdatedState(onLoginSuccess)
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is LoginEvent.Success -> currentOnSuccess(event.role)
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CreamLight)
            .imePadding()                    // area scroll menyusut saat keyboard muncul
            .verticalScroll(rememberScrollState())
            .fillMaxWidth()
            // Navy digambar DI DALAM konten yang di-scroll, jadi ikut bergeser bareng card
            .drawBehind {
                drawRect(
                    color = Navy,
                    size = Size(size.width, NavyBlockHeight.toPx())
                )
            }
    ) {
        // Area logo: tinggi 270dp, card mulai tepat di bawahnya (sesuai Figma: 540px / 2)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(270.dp)
                .padding(top = 150.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            MagangLogo()
            Spacer(Modifier.height(4.dp))
            RolePill(role)
        }

        // Card putih: menimpa batas navy dan krem
        Column(
            modifier = Modifier
                .padding(horizontal = 25.dp)
                .fillMaxWidth()
                .shadow(2.dp, RoundedCornerShape(24.dp)) // shadow sangat tipis
                .background(White, RoundedCornerShape(24.dp))
                .padding(24.dp)
        ) {
            Text(
                text = "Masuk",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Navy
            )
            Spacer(Modifier.height(4.dp))
            Text(text = texts.subtitle, fontSize = 15.sp, color = StatusGrayText)

            Spacer(Modifier.height(20.dp))
            AppTextField(
                value = state.loginId,
                onValueChange = viewModel::onLoginIdChange,
                label = texts.label,
                placeholder = texts.placeholder,
                error = state.loginIdError,
                keyboardType = texts.keyboardType,
                imeAction = ImeAction.Next,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))
            AppTextField(
                value = state.password,
                onValueChange = viewModel::onPasswordChange,
                label = "Password",
                error = state.passwordError,
                isPassword = true,
                imeAction = ImeAction.Done,
                // Tombol "Done" di keyboard = tekan Masuk
                keyboardActions = KeyboardActions(onDone = { viewModel.login() }),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(24.dp))
            AppButton(
                text = "Masuk",
                onClick = viewModel::login,
                trailingIcon = Icons.AutoMirrored.Filled.ArrowForward,
                loading = state.isLoading,
                modifier = Modifier.fillMaxWidth().height(44.dp)
            )
        }

        Spacer(Modifier.height(24.dp))
        Text(
            text = "Akun dibuat oleh pihak kampus. Hubungi kampus jika ada kendala.",
            fontSize = 13.sp,
            color = StatusGrayText,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 40.dp)
        )
        Spacer(Modifier.height(24.dp))
    }
}