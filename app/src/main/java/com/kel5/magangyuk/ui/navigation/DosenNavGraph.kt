package com.kel5.magangyuk.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

fun NavGraphBuilder.dosenGraph(navController: NavController) {
    composable(Routes.Dosen.BERANDA) { PlaceholderScreen("Beranda Dosen") }
    composable(Routes.Dosen.BIMBINGAN) { PlaceholderScreen("Bimbingan") }
    composable(Routes.Dosen.PENILAIAN) { PlaceholderScreen("Penilaian") }
}