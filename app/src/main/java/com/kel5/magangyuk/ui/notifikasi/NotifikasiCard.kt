package com.kel5.magangyuk.ui.notifikasi

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.kel5.magangyuk.data.local.entity.NotifikasiEntity
import com.kel5.magangyuk.data.local.entity.TipeNotifikasi
import com.kel5.magangyuk.ui.theme.CreamLight
import com.kel5.magangyuk.ui.theme.GrayCream
import com.kel5.magangyuk.ui.theme.Navy
import com.kel5.magangyuk.ui.theme.StatusGrayText
import com.kel5.magangyuk.ui.theme.White
import com.kel5.magangyuk.util.RelativeTime

@Composable
fun NotifikasiCard(
    item: NotifikasiEntity,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        color = if (item.dibaca) CreamLight else White,
        shape = RoundedCornerShape(16.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            Box(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .size(8.dp)
                    .background(if (item.dibaca) Color.Transparent else Navy, CircleShape)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(GrayCream, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = iconUntuk(item.tipe),
                    contentDescription = null,
                    tint = Navy
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.judul,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Navy
                )
                Text(
                    text = item.pesan,
                    style = MaterialTheme.typography.bodySmall,
                    color = StatusGrayText,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 2.dp)
                )
                Text(
                    text = RelativeTime.format(item.tglBuat),
                    style = MaterialTheme.typography.labelSmall,
                    color = StatusGrayText,
                    modifier = Modifier.padding(top = 6.dp)
                )
            }
        }
    }
}

private fun iconUntuk(tipe: TipeNotifikasi): ImageVector = when (tipe) {
    TipeNotifikasi.LAMARAN_BARU -> Icons.Default.Person
    TipeNotifikasi.LAMARAN_DITERIMA,
    TipeNotifikasi.TAWARAN_DIKONFIRMASI,
    TipeNotifikasi.LAPORAN_DISETUJUI -> Icons.Default.Check
    TipeNotifikasi.LAMARAN_DITOLAK,
    TipeNotifikasi.TAWARAN_DITOLAK -> Icons.Default.Close
    TipeNotifikasi.TAWARAN_KEDALUWARSA,
    TipeNotifikasi.LAPORAN_REVISI -> Icons.Default.Warning
    TipeNotifikasi.LAPORAN_DIUNGGAH -> Icons.Default.Info
    TipeNotifikasi.NILAI_MITRA_TERKIRIM,
    TipeNotifikasi.NILAI_DOSEN_TERKIRIM -> Icons.Default.Star
    TipeNotifikasi.MAGANG_SELESAI -> Icons.Default.Done
}