package com.kel5.magangyuk.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kel5.magangyuk.data.local.entity.Role
import com.kel5.magangyuk.ui.theme.CreamYellow
import com.kel5.magangyuk.ui.theme.Navy

// Pill krem kecil berisi nama role (di bawah logo)
@Composable
fun RolePill(role: Role, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(CreamYellow, RoundedCornerShape(50))
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
        Text(
            text = role.displayName(),
            color = Navy,
            style = MaterialTheme.typography.labelLarge
        )
    }
}

fun Role.displayName(): String = when (this) {
    Role.MAHASISWA -> "Mahasiswa"
    Role.MITRA -> "Mitra"
    Role.DOSEN -> "Dosen"
}