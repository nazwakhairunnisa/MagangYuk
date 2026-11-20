package com.kel5.magangyuk.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

fun NavGraphBuilder.mitraGraph(navController: NavController) {
    composable(Routes.Mitra.BERANDA) { PlaceholderScreen("Beranda Mitra") }
    composable(Routes.Mitra.LOWONGAN) { PlaceholderScreen("Lowongan Mitra") }
    composable(Routes.Mitra.PELAMAR) { PlaceholderScreen("Pelamar") }
    composable(Routes.Mitra.MAGANG) { PlaceholderScreen("Magang Mitra") }
}