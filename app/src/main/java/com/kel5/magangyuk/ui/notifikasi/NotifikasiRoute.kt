package com.kel5.magangyuk.ui.notifikasi

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kel5.magangyuk.MagangYukApp
import com.kel5.magangyuk.data.local.entity.NotifikasiEntity

@Composable
fun NotifikasiRoute(
    onNavigateTo: (NotifikasiEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val app = LocalContext.current.applicationContext as MagangYukApp
    val viewModel: NotifikasiViewModel = viewModel(
        factory = NotifikasiViewModelFactory(app.notifikasiRepository, app.sessionManager)
    )
    val state by viewModel.uiState.collectAsState()

    NotifikasiScreen(
        state = state,
        onItemClick = { item ->
            viewModel.markAsRead(item.id)
            onNavigateTo(item)
        },
        onMarkAllRead = { viewModel.markAllAsRead() },
        modifier = modifier
    )
}