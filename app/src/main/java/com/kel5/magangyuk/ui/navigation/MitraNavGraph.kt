package com.kel5.magangyuk.ui.navigation

import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.kel5.magangyuk.MagangYukApp
import com.kel5.magangyuk.ui.mitra.lowongan.FormLowonganScreen
import com.kel5.magangyuk.ui.mitra.lowongan.FormLowonganViewModel
import com.kel5.magangyuk.ui.mitra.lowongan.FormLowonganViewModelFactory
import com.kel5.magangyuk.ui.mitra.lowongan.LowonganMitraScreen
import com.kel5.magangyuk.ui.mitra.lowongan.LowonganMitraViewModel
import com.kel5.magangyuk.ui.mitra.lowongan.LowonganMitraViewModelFactory
import com.kel5.magangyuk.ui.mitra.lowongan.LowonganRoutes

fun NavGraphBuilder.mitraGraph(navController: NavController) {
    composable(Routes.Mitra.BERANDA) { PlaceholderScreen("Beranda Mitra") }

    composable(Routes.Mitra.LOWONGAN) {
        val app = LocalContext.current.applicationContext as MagangYukApp
        val viewModel: LowonganMitraViewModel = viewModel(factory = LowonganMitraViewModelFactory(app))
        LowonganMitraScreen(
            viewModel = viewModel,
            onTambahClick = { navController.navigate(LowonganRoutes.form(0L)) },
            onSuntingClick = { id -> navController.navigate(LowonganRoutes.form(id)) }
        )
    }

    composable(
        route = LowonganRoutes.FORM,
        arguments = listOf(navArgument("id") { type = NavType.LongType })
    ) { entry ->
        val app = LocalContext.current.applicationContext as MagangYukApp
        val id = entry.arguments?.getLong("id") ?: 0L
        val viewModel: FormLowonganViewModel = viewModel(factory = FormLowonganViewModelFactory(app, id))
        FormLowonganScreen(
            viewModel = viewModel,
            onBack = { navController.popBackStack() },
            onSelesai = { navController.popBackStack() }
        )
    }

    composable(Routes.Mitra.PELAMAR) { PlaceholderScreen("Pelamar") }
    composable(Routes.Mitra.MAGANG) { PlaceholderScreen("Magang Mitra") }
}