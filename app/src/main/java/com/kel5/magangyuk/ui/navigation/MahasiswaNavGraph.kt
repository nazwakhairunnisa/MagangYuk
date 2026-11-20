package com.kel5.magangyuk.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

// Extension function pada NavGraphBuilder: dipanggil di dalam NavHost { ... }.
// Nanti tiap fitur mengganti PlaceholderScreen dengan layar aslinya.
fun NavGraphBuilder.mahasiswaGraph(navController: NavController) {
    composable(Routes.Mahasiswa.BERANDA) { PlaceholderScreen("Beranda Mahasiswa") }
    composable(Routes.Mahasiswa.LOWONGAN) { PlaceholderScreen("Lowongan") }
    composable(Routes.Mahasiswa.LAMARAN) { PlaceholderScreen("Lamaran") }
    composable(Routes.Mahasiswa.MAGANG) { PlaceholderScreen("Magang Saya") }
}