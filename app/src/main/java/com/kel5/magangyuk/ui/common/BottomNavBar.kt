package com.kel5.magangyuk.ui.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import com.kel5.magangyuk.ui.theme.*

// Satu menu di navbar. "route" nanti dipakai Navigation Compose
// (diisi saat mengerjakan feature/navigation).
data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
)

@Composable
fun BottomNavBar(
    items: List<BottomNavItem>,
    selectedRoute: String,
    onItemClick: (BottomNavItem) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        containerColor = Navy,
        modifier = modifier
    ) {
        items.forEach { item ->
            val selected = item.route == selectedRoute
            NavigationBarItem(
                selected = selected,
                onClick = { onItemClick(item) },
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label, style = MaterialTheme.typography.labelSmall) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = CreamYellow,
                    selectedTextColor = CreamYellow,
                    unselectedIconColor = White.copy(alpha = 0.6f),   // putih redup
                    unselectedTextColor = White.copy(alpha = 0.6f),
                    indicatorColor = Color.Transparent                // tanpa "pil" latar bawaan M3
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun BottomNavBarPreview() {
    MagangYukTheme {
        BottomNavBar(
            items = listOf(
                BottomNavItem("beranda", "Beranda", Icons.Default.Home),
                BottomNavItem("lowongan", "Lowongan", Icons.Default.Search),
                BottomNavItem("lamaran", "Lamaran", Icons.Default.Email),
                BottomNavItem("magang", "Magang", Icons.Default.DateRange)
            ),
            selectedRoute = "lowongan",
            onItemClick = {}
        )
    }
}