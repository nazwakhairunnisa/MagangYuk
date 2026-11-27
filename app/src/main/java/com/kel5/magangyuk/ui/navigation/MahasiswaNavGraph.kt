package com.kel5.magangyuk.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.kel5.magangyuk.ui.mahasiswa.lamaran.FormLamaranRoute
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
        val lowonganId = entry.arguments?.getLong(Routes.Mahasiswa.ARG_LOWONGAN_ID) ?: 0L
        DetailLowonganRoute(
            lowonganId = lowonganId,
            onBack = { navController.popBackStack() },
            onAjukanLamaran = { id ->
                navController.navigate(Routes.Mahasiswa.formLamaran(id))
            }
        )
    }

    composable(
        route = Routes.Mahasiswa.FORM_LAMARAN,
        arguments = listOf(navArgument(Routes.Mahasiswa.ARG_LOWONGAN_ID) { type = NavType.LongType })
    ) { entry ->
        FormLamaranRoute(
            lowonganId = entry.arguments?.getLong(Routes.Mahasiswa.ARG_LOWONGAN_ID) ?: 0L,
            onBack = { navController.popBackStack() },
            onSelesai = {
                // Sukses -> ke menu Lamaran. popUpTo(BERANDA) membuang Lowongan, Detail, dan Form
                // dari tumpukan (tanpa saveState), jadi tab Lowongan dibuka bersih lagi nanti
                // dan form yang sudah terkirim tidak muncul lagi.
                navController.navigate(Routes.Mahasiswa.LAMARAN) {
                    popUpTo(Routes.Mahasiswa.BERANDA)
                    launchSingleTop = true
                }
            }
        )
    }

    composable(Routes.Mahasiswa.LAMARAN) { PlaceholderScreen("Lamaran") }
    composable(Routes.Mahasiswa.MAGANG) { PlaceholderScreen("Magang Saya") }
}