package com.kel5.magangyuk.ui.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.kel5.magangyuk.data.local.entity.Role
import com.kel5.magangyuk.ui.common.AppButton
import com.kel5.magangyuk.ui.common.AppHeader
import com.kel5.magangyuk.ui.common.BottomNavBar
import com.kel5.magangyuk.ui.common.BottomNavItem
import com.kel5.magangyuk.ui.notifikasi.NotifikasiPermissionRequester
import com.kel5.magangyuk.ui.theme.CreamLight

@Composable
fun RoleScaffold(
    role: Role,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    // NavController milik NavHost dalam (khusus area di antara header dan navbar)
    val navController = rememberNavController()

    // Rute layar yang sedang tampil. by + State: berubah -> UI tergambar ulang.
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    val tabs = remember(role) { bottomTabsFor(role) }

    // startsWith: rute detail (mis. "mhs/lowongan/detail/3") tetap menyalakan menu induknya.
    // Di layar Notifikasi/Profil tidak ada menu yang cocok -> "" -> tidak ada yang menyala.
    val selectedRoute = tabs.firstOrNull { currentRoute?.startsWith(it.route) == true }
        ?.route.orEmpty()

    // Dialog izin notifikasi: muncul sekali setelah login (di-skip sendiri kalau sudah pernah diminta)
    NotifikasiPermissionRequester()

    Scaffold(
        modifier = modifier,
        containerColor = CreamLight,
        topBar = {
            AppHeader(
                roleLabel = roleLabel(role),
                unreadCount = 0, // TODO(notif): langkah 2, ambil dari database
                onNotificationClick = { navController.openSingleTop(Routes.NOTIFIKASI) },
                onProfileClick = { navController.openSingleTop(Routes.PROFIL) }
            )
        },
        bottomBar = {
            BottomNavBar(
                items = tabs,
                selectedRoute = selectedRoute,
                onItemClick = { item ->
                    navController.navigate(item.route) {
                        // Kembali ke layar awal (Beranda) sambil menyimpan state menu yang ditinggalkan
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true   // tap menu yang sama tidak menumpuk layar
                        restoreState = true      // state menu tujuan dipulihkan
                    }
                }
            )
        }
    ) { innerPadding ->
        // innerPadding = ruang yang sudah dipakai header dan navbar, supaya isi tidak tertutup
        NavHost(
            navController = navController,
            startDestination = tabs.first().route,   // menu pertama = Beranda
            modifier = Modifier.padding(innerPadding)
        ) {
            // Layar bersama semua role
            composable(Routes.NOTIFIKASI) {
                PlaceholderScreen("Notifikasi") // TODO(notif): langkah 2
            }
            composable(Routes.PROFIL) {
                // SEMENTARA sampai feature/profil jadi. Tombol ini cuma untuk menguji logout.
                Column(Modifier.fillMaxSize()) {
                    PlaceholderScreen("Profil", Modifier.weight(1f))
                    AppButton(
                        text = "Keluar Akun",
                        onClick = onLogout,
                        modifier = Modifier.fillMaxWidth().padding(24.dp)
                    )
                }
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

private fun roleLabel(role: Role) = when (role) {
    Role.MAHASISWA -> "Mahasiswa"
    Role.MITRA -> "Mitra"
    Role.DOSEN -> "Dosen"
}

// Isi navbar per role (urutan = urutan di spesifikasi). Ikon silakan disamakan dengan Figma.
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