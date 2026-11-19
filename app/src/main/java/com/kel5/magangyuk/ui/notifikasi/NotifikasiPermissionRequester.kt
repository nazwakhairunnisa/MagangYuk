package com.kel5.magangyuk.ui.notifikasi

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.kel5.magangyuk.MagangYukApp
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@Composable
fun NotifikasiPermissionRequester() {
    if (Build.VERSION.SDK_INT < 33) return

    val context = LocalContext.current
    val app = context.applicationContext as MagangYukApp
    val scope = rememberCoroutineScope()

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        scope.launch { app.sessionManager.markNotifPermissionAsked() }
    }

    LaunchedEffect(Unit) {
        val sudahDiminta = app.sessionManager.notifPermissionAsked.first()
        val sudahDiizinkan = ContextCompat.checkSelfPermission(
            context, Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED

        if (!sudahDiminta && !sudahDiizinkan) {
            launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}