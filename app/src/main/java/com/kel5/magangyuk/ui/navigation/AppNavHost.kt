package com.kel5.magangyuk.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.kel5.magangyuk.MagangYukApp
import com.kel5.magangyuk.data.local.entity.Role
import com.kel5.magangyuk.ui.auth.LoginScreen
import com.kel5.magangyuk.ui.auth.SplashScreen
import kotlinx.coroutines.launch

@Composable
fun AppNavHost(
    linkRole: Role?,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val app = LocalContext.current.applicationContext as MagangYukApp
    val scope = rememberCoroutineScope() // untuk memanggil fungsi suspend (logout) dari tombol

    val roleArg = listOf(navArgument(Routes.ARG_ROLE) { type = NavType.StringType })

    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH,
        modifier = modifier
    ) {
        composable(Routes.SPLASH) {
            SplashScreen(
                linkRole = linkRole,
                onGoToHome = { role ->
                    navController.navigate(Routes.main(role)) {
                        // Splash dibuang dari tumpukan supaya tombol Back tidak kembali ke logo
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                },
                onGoToLogin = { role ->
                    navController.navigate(Routes.login(role)) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.LOGIN, arguments = roleArg) { entry ->
            LoginScreen(
                role = entry.role(),
                onLoginSuccess = { role ->
                    navController.navigate(Routes.main(role)) {
                        popUpTo(Routes.LOGIN) { inclusive = true } // Back dari Beranda tidak ke Login
                    }
                }
            )
        }

        composable(Routes.MAIN, arguments = roleArg) { entry ->
            val role = entry.role()
            RoleScaffold(
                role = role,
                onLogout = {
                    scope.launch {
                        app.authRepository.logout() // hapus sesi dulu, baru pindah layar
                        navController.navigate(Routes.login(role)) {
                            popUpTo(Routes.MAIN) { inclusive = true } // buang seluruh area Main
                        }
                    }
                }
            )
        }
    }
}

// Baca argumen {role} dari rute. Kalau isinya aneh, jatuh ke Mahasiswa (sama seperti default Splash).
private fun NavBackStackEntry.role(): Role {
    val name = arguments?.getString(Routes.ARG_ROLE)
    return Role.entries.firstOrNull { it.name == name } ?: Role.MAHASISWA
}