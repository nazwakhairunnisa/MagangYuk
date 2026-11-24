package com.kel5.magangyuk.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.kel5.magangyuk.ui.mahasiswa.lowongan.LowonganRoute

fun NavGraphBuilder.mahasiswaGraph(navController: NavController) {
    composable(Routes.Mahasiswa.BERANDA) { PlaceholderScreen("Beranda Mahasiswa") }

    composable(Routes.Mahasiswa.LOWONGAN) {
        LowonganRoute(
            onDetailClick = { id ->
                navController.navigate(Routes.Mahasiswa.detailLowongan(id))
            }
        )
    }
    // Sementara placeholder, diganti layar asli di langkah Detail Lowongan.
    // navArgument = mendeklarasikan bahwa {id} di rute bertipe Long.
    composable(
        route = Routes.Mahasiswa.DETAIL_LOWONGAN,
        arguments = listOf(navArgument(Routes.Mahasiswa.ARG_LOWONGAN_ID) { type = NavType.LongType })
    ) { PlaceholderScreen("Detail Lowongan") }

    composable(Routes.Mahasiswa.LAMARAN) { PlaceholderScreen("Lamaran") }
    composable(Routes.Mahasiswa.MAGANG) { PlaceholderScreen("Magang Saya") }
}