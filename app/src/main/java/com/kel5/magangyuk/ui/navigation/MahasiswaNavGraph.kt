package com.kel5.magangyuk.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.kel5.magangyuk.ui.mahasiswa.lowongan.DetailLowonganRoute
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
    composable(
        route = Routes.Mahasiswa.DETAIL_LOWONGAN,
        arguments = listOf(navArgument(Routes.Mahasiswa.ARG_LOWONGAN_ID) { type = NavType.LongType })
    ) { entry ->
        DetailLowonganRoute(
            // Ambil {id} dari alamat rute
            lowonganId = entry.arguments?.getLong(Routes.Mahasiswa.ARG_LOWONGAN_ID) ?: 0L,
            onBack = { navController.popBackStack() },
            // TODO Fase 3 (feature/mhs-lamaran): navigate ke Form Lamaran
            onAjukanLamaran = { }
        )
    }

    composable(Routes.Mahasiswa.LAMARAN) { PlaceholderScreen("Lamaran") }
    composable(Routes.Mahasiswa.MAGANG) { PlaceholderScreen("Magang Saya") }
}