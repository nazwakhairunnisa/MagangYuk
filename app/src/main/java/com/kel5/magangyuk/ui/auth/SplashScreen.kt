package com.kel5.magangyuk.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.kel5.magangyuk.MagangYukApp
import com.kel5.magangyuk.data.local.entity.Role
import com.kel5.magangyuk.ui.common.MagangLogo
import com.kel5.magangyuk.ui.common.RolePill
import com.kel5.magangyuk.ui.theme.MagangYukTheme
import com.kel5.magangyuk.ui.theme.Navy

// Layar "pintar": mengambil ViewModel lalu memanggil callback saat tujuan sudah diputuskan.
// Rute sebenarnya dihubungkan nanti di feature/navigation, jadi di sini cukup callback.
@Composable
fun SplashScreen(
    linkRole: Role?,
    onGoToHome: (Role) -> Unit,
    onGoToLogin: (Role) -> Unit
) {
    val app = LocalContext.current.applicationContext as MagangYukApp

    val viewModel: SplashViewModel = viewModel(
        factory = viewModelFactory {
            initializer {
                SplashViewModel(app.sessionManager, app.authRepository, app.seedJob, linkRole)
            }
        }
    )

    // collectAsState: mengubah StateFlow jadi State Compose, layar tergambar ulang kalau nilainya berubah
    val destination by viewModel.destination.collectAsState()

    // LaunchedEffect berjalan sekali setiap destination berubah (di sini: null -> tujuan)
    LaunchedEffect(destination) {
        when (val d = destination) {
            is SplashDestination.Home -> onGoToHome(d.role)
            is SplashDestination.Login -> onGoToLogin(d.role)
            null -> Unit   // masih menunggu
        }
    }

    SplashContent(linkRole)
}

// Layar hanya tampilan, tanpa logika. buat dipreview.
@Composable
private fun SplashContent(linkRole: Role?) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Navy),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        MagangLogo()
        // Pill hanya tampil kalau app dibuka lewat deep link (keputusan kita)
        if (linkRole != null) {
            RolePill(role = linkRole, modifier = Modifier.padding(top = 8.dp))
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun SplashPreviewTanpaLink() = MagangYukTheme { SplashContent(null) }

@Preview(showSystemUi = true)
@Composable
private fun SplashPreviewDenganLink() = MagangYukTheme { SplashContent(Role.MAHASISWA) }