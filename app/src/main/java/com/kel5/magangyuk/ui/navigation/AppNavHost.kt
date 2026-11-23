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
import com.kel5.magangyuk.util.NotifikasiTap
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.launch

@Composable
fun AppNavHost(
    linkRole: Role?,
    notifTap: NotifikasiTap?,
    onNotifTapConsumed: () -> Unit,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val app = LocalContext.current.applicationContext as MagangYukApp
    val scope = rememberCoroutineScope()

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
            // Belum login = tidak ada penerima yang cocok. Buang ketukan notifikasi supaya
            // tidak "nyangkut" lalu tiba-tiba melompat setelah login nanti.
            LaunchedEffect(notifTap) {
                if (notifTap != null) onNotifTapConsumed()
            }

            LoginScreen(
                role = entry.role(),
                onLoginSuccess = { role ->
                    navController.navigate(Routes.main(role)) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.MAIN, arguments = roleArg) { entry ->
            val role = entry.role()
            RoleScaffold(
                role = role,
                notifTap = notifTap,
                onNotifTapConsumed = onNotifTapConsumed,
                onLogout = {
                    scope.launch {
                        app.authRepository.logout()
                        navController.navigate(Routes.login(role)) {
                            popUpTo(Routes.MAIN) { inclusive = true }
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