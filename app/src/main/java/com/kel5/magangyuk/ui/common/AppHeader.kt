package com.kel5.magangyuk.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kel5.magangyuk.ui.theme.*

@Composable
fun AppHeader(
    roleLabel: String,              // "Mahasiswa" / "Mitra" / "Dosen"
    unreadCount: Int,               // jumlah notifikasi belum dibaca
    onNotificationClick: () -> Unit,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Surface = "alas" berwarna yang bisa punya shadow.
    // shadowElevation kecil = shadow tipis sesuai spesifikasi.
    Surface(
        color = Navy,
        shadowElevation = 2.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                // statusBarsPadding: dorong isi ke bawah status bar, tapi warna navy
                // tetap menutupi area status bar (karena app memakai edge-to-edge)
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // ----- Kiri: logo + pill role -----
            Column {
                Text(
                    text = "MagangYuk!",
                    style = MaterialTheme.typography.titleLarge,
                    color = CreamYellow
                )
                Text(
                    text = roleLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = Navy,
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .background(CreamYellow, RoundedCornerShape(50))
                        .padding(horizontal = 10.dp, vertical = 2.dp)
                )
            }

            // ----- Kanan: lonceng + profil -----
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                NotificationBell(unreadCount, onNotificationClick)
                ProfileButton(onProfileClick)
            }
        }
    }
}

@Composable
private fun NotificationBell(unreadCount: Int, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        // BadgedBox = ikon dengan "lencana" kecil di pojok kanan atasnya
        BadgedBox(
            badge = {
                if (unreadCount > 0) {   // 0 -> badge disembunyikan
                    Badge(containerColor = ActionRed, contentColor = White) {
                        Text(if (unreadCount > 99) "99+" else unreadCount.toString())
                    }
                }
            }
        ) {
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = "Notifikasi",
                tint = CreamYellow
            )
        }
    }
}

@Composable
private fun ProfileButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(CreamYellow)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = "Profil",
            tint = Navy
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AppHeaderPreview() {
    MagangYukTheme {
        Column {
            AppHeader("Mahasiswa", unreadCount = 3, onNotificationClick = {}, onProfileClick = {})
            AppHeader("Mitra", unreadCount = 120, onNotificationClick = {}, onProfileClick = {})
            AppHeader("Dosen", unreadCount = 0, onNotificationClick = {}, onProfileClick = {})
        }
    }
}
