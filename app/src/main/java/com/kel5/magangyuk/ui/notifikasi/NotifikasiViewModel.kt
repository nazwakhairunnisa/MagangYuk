package com.kel5.magangyuk.ui.notifikasi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.kel5.magangyuk.data.local.entity.NotifikasiEntity
import com.kel5.magangyuk.data.repository.NotifikasiRepository
import com.kel5.magangyuk.data.session.SessionManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class NotifikasiUiState(
    val items: List<NotifikasiEntity> = emptyList(),
    val unreadCount: Int = 0
)

@OptIn(ExperimentalCoroutinesApi::class)
class NotifikasiViewModel(
    private val repository: NotifikasiRepository,
    private val sessionManager: SessionManager
) : ViewModel() {


    val uiState: StateFlow<NotifikasiUiState> = sessionManager.session
        .flatMapLatest { session ->
            if (session == null) {
                flowOf(NotifikasiUiState())
            } else {
                combine(
                    repository.getAll(session.userId),
                    repository.countUnread(session.userId)
                ) { items, unread -> NotifikasiUiState(items, unread) }
            }
        }

        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), NotifikasiUiState())


    fun markAsRead(id: Long) {
        viewModelScope.launch { repository.markAsRead(id) }
    }

    fun markAllAsRead() {
        viewModelScope.launch {
            val userId = sessionManager.session.first()?.userId ?: return@launch
            repository.markAllAsRead(userId)
        }
    }
}

class NotifikasiViewModelFactory(
    private val repository: NotifikasiRepository,
    private val sessionManager: SessionManager
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return NotifikasiViewModel(repository, sessionManager) as T
    }
}