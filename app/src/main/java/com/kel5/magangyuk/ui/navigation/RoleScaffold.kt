package com.kel5.magangyuk.ui.navigation

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.kel5.magangyuk.MagangYukApp
import com.kel5.magangyuk.data.local.entity.Role
import com.kel5.magangyuk.ui.common.AppHeader
import com.kel5.magangyuk.ui.common.BottomNavBar
import com.kel5.magangyuk.ui.common.BottomNavItem
import com.kel5.magangyuk.ui.mitra.lowongan.LowonganRoutes
import com.kel5.magangyuk.ui.notifikasi.NotifikasiPermissionRequester
import com.kel5.magangyuk.ui.notifikasi.NotifikasiRoute
import com.kel5.magangyuk.ui.notifikasi.NotifikasiViewModel
import com.kel5.magangyuk.ui.notifikasi.NotifikasiViewModelFactory
import com.kel5.magangyuk.ui.profil.ProfilRoute
import com.kel5.magangyuk.ui.theme.CreamLight
import com.kel5.magangyuk.util.NotifikasiTap
import kotlinx.coroutines.flow.first

// OptIn: WindowInsets.isImeVisible masih ditandai experimental oleh Compose
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RoleScaffold(
    role: Role,
    notifTap: NotifikasiTap?,
    onNotifTapConsumed: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    // NavController milik NavHost dalam (khusus area di antara header dan navbar)
    val navController = rememberNavController()

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val showAppHeader = !hidesAppHeader(currentRoute)

    // Satu NotifikasiViewModel untuk header (badge) DAN layar Notifikasi.
    // Dibuat di sini (di luar NavHost dalam), jadi keduanya melihat data yang sama.
    val app = LocalContext.current.applicationContext as MagangYukApp
    val notifViewModel: NotifikasiViewModel = viewModel(
        factory = NotifikasiViewModelFactory(app.notifikasiRepository, app.sessionManager)
    )
    val notifState by notifViewModel.uiState.collectAsState()

    // Ketuk notifikasi dari status bar (spesifikasi K.3):
    // penerima = akun yang sedang login -> langsung ke layar tujuan, selain itu tetap di Beranda.
    // LaunchedEffect(notifTap): jalan sekali setiap ada ketukan baru.
    LaunchedEffect(notifTap) {
        val tap = notifTap ?: return@LaunchedEffect
        val session = app.sessionManager.session.first()
        if (session != null && session.userId == tap.userId) {
            navController.navigateToTab(notifikasiTujuan(role, tap.tipe))
        }
        onNotifTapConsumed() // kosongkan, supaya tidak diproses ulang
    }

    val tabs = remember(role) { bottomTabsFor(role) }

    // startsWith: rute detail (mis. "mhs/lowongan/detail/3") tetap menyalakan menu induknya.
    val selectedRoute = tabs.firstOrNull { currentRoute?.startsWith(it.route) == true }
        ?.route.orEmpty()

    // Dialog izin notifikasi: muncul sekali setelah login
    NotifikasiPermissionRequester()

    Scaffold(
        modifier = modifier,
        containerColor = CreamLight,
        topBar = {
            // Layar dengan header sendiri: AppHeader tidak dirender,
            // Scaffold otomatis tidak menyisakan ruang di atas.
            if (showAppHeader) {
                AppHeader(
                    roleLabel = roleLabel(role),
                    unreadCount = notifState.unreadCount,
                    onNotificationClick = { navController.openSingleTop(Routes.NOTIFIKASI) },
                    onProfileClick = { navController.openSingleTop(Routes.PROFIL) }
                )
            }
        },
        bottomBar = {
            // Navbar disembunyikan saat keyboard terbuka, supaya tidak ada
            // jarak kosong setinggi navbar di atas keyboard.
            val imeVisible = WindowInsets.isImeVisible
            if (!imeVisible) {
                BottomNavBar(
                    items = tabs,
                    selectedRoute = selectedRoute,
                    onItemClick = { item -> navController.navigateToTab(item.route) }
                )
            }
        }
    ) { innerPadding ->
        // Kalau AppHeader disembunyikan, layar tujuan mengurus sendiri jarak status bar-nya,
        // jadi padding atas dari Scaffold tidak dipakai (supaya tidak dobel).
        val contentPadding = if (showAppHeader) {
            innerPadding
        } else {
            PaddingValues(
                start = 0.dp,
                top = 0.dp,
                end = 0.dp,
                bottom = innerPadding.calculateBottomPadding()
            )
        }

        NavHost(
            navController = navController,
            startDestination = tabs.first().route,
            modifier = Modifier.padding(contentPadding)
        ) {
            composable(Routes.NOTIFIKASI) {
                // ViewModel + penandaan dibaca sudah diurus NotifikasiRoute (buatan B).
                // Kita cukup menentukan tujuan saat kartu diketuk.
                NotifikasiRoute(
                    onNavigateTo = { item ->
                        navController.navigateToTab(notifikasiTujuan(role, item.tipe))
                    }
                )
            }
            composable(Routes.PROFIL) {
                ProfilRoute(
                    onBack = { navController.popBackStack() },
                    onLogout = onLogout
                )
            }

            // Layar khusus role
            when (role) {
                Role.MAHASISWA -> mahasiswaGraph(navController)
                Role.MITRA -> mitraGraph(navController)
                Role.DOSEN -> dosenGraph(navController)
            }
        }
    }
}

private fun NavController.openSingleTop(route: String) =
    navigate(route) { launchSingleTop = true }

// Rute yang punya header sendiri (back arrow + judul), jadi AppHeader global disembunyikan.
// Pakai startsWith karena rute form bisa punya argumen, mis. "mitra/lowongan/form?id={id}".
private fun hidesAppHeader(route: String?): Boolean {
    if (route == null) return false
    val routesWithOwnHeader = listOf(
        LowonganRoutes.FORM,
        Routes.PROFIL
    )
    return routesWithOwnHeader.any { route.startsWith(it) }
}

// Pindah ke satu menu navbar. Dipakai tap navbar dan ketuk notifikasi.
private fun NavController.navigateToTab(route: String) {
    // Notifikasi dan Profil adalah layar sementara dari header, bukan bagian menu mana pun.
    // Dibuang dulu TANPA saveState, supaya tidak ikut tersimpan lalu muncul lagi saat menu dibuka.
    // Kalau layarnya memang tidak ada di tumpukan, hasilnya false dan tidak terjadi apa-apa.
    popBackStack(Routes.NOTIFIKASI, inclusive = true)
    popBackStack(Routes.PROFIL, inclusive = true)

    navigate(route) {
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

private fun roleLabel(role: Role) = when (role) {
    Role.MAHASISWA -> "Mahasiswa"
    Role.MITRA -> "Mitra"
    Role.DOSEN -> "Dosen"
}

private fun bottomTabsFor(role: Role): List<BottomNavItem> = when (role) {
    Role.MAHASISWA -> listOf(
        BottomNavItem(Routes.Mahasiswa.BERANDA, "Beranda", Icons.Default.Home),
        BottomNavItem(Routes.Mahasiswa.LOWONGAN, "Lowongan", Icons.Default.Search),
        BottomNavItem(Routes.Mahasiswa.LAMARAN, "Lamaran", Icons.Default.Email),
        BottomNavItem(Routes.Mahasiswa.MAGANG, "Magang", Icons.Default.DateRange)
    )
    Role.MITRA -> listOf(
        BottomNavItem(Routes.Mitra.BERANDA, "Beranda", Icons.Default.Home),
        BottomNavItem(Routes.Mitra.LOWONGAN, "Lowongan", Icons.Default.Work),
        BottomNavItem(Routes.Mitra.PELAMAR, "Pelamar", Icons.Default.People),
        BottomNavItem(Routes.Mitra.MAGANG, "Magang", Icons.Default.DateRange)
    )
    Role.DOSEN -> listOf(
        BottomNavItem(Routes.Dosen.BERANDA, "Beranda", Icons.Default.Home),
        BottomNavItem(Routes.Dosen.BIMBINGAN, "Bimbingan", Icons.Default.People),
        BottomNavItem(Routes.Dosen.PENILAIAN, "Penilaian", Icons.Default.Star)
    )
}