package com.kel5.magangyuk.ui.mahasiswa.lowongan

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Work
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.kel5.magangyuk.data.local.dao.LowonganUntukMahasiswa
import com.kel5.magangyuk.data.local.entity.JenisLowongan
import com.kel5.magangyuk.data.local.entity.ModeKerja
import com.kel5.magangyuk.ui.common.AppButton
import com.kel5.magangyuk.ui.common.InfoTag
import com.kel5.magangyuk.ui.common.TagTone
import com.kel5.magangyuk.ui.theme.CreamLight
import com.kel5.magangyuk.ui.theme.Navy
import com.kel5.magangyuk.ui.theme.StatusGrayText
import com.kel5.magangyuk.ui.theme.White
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@Composable
fun LowonganCard(
    item: LowonganUntukMahasiswa,
    onDetailClick: () -> Unit,
    onBookmarkClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val l = item.lowongan
    val shape = RoundedCornerShape(24.dp)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(1.dp, shape)
            .background(White, shape)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Baris atas: ikon posisi, nama posisi + PT, bookmark
        Row(verticalAlignment = Alignment.Top) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(CreamLight, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.Work, contentDescription = null, tint = Navy)
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    text = l.posisi,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Navy,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = item.namaPerusahaan,
                    style = MaterialTheme.typography.bodyMedium,
                    color = StatusGrayText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            IconButton(onClick = onBookmarkClick) {
                Icon(
                    imageVector = if (item.dibookmark) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                    contentDescription = if (item.dibookmark) "Hapus bookmark" else "Simpan lowongan",
                    tint = Navy
                )
            }
        }

        // Tag: Paid/Unpaid (ungu), durasi dan mode (abu)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            InfoTag(labelJenis(l.jenis), tone = TagTone.PURPLE)
            InfoTag("${durasiBulan(l.tglMulai, l.tglSelesai)} bulan")
            InfoTag(labelMode(l.mode))
        }

        // Baris bawah: lokasi + tombol Details
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Outlined.LocationOn,
                contentDescription = null,
                tint = StatusGrayText,
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(4.dp))
            Text(
                text = l.lokasi,
                style = MaterialTheme.typography.bodyMedium,
                color = StatusGrayText,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(8.dp))
            AppButton(text = "Details", onClick = onDetailClick)
        }
    }
}

// ---- Fungsi bantu tampilan (dipakai juga oleh Detail Lowongan nanti) ----

fun labelJenis(jenis: JenisLowongan) = when (jenis) {
    JenisLowongan.PAID -> "Paid"
    JenisLowongan.UNPAID -> "Unpaid"
}

fun labelMode(mode: ModeKerja) = when (mode) {
    ModeKerja.ONSITE -> "Onsite"
    ModeKerja.HYBRID -> "Hybrid"
    ModeKerja.ONLINE -> "Online"
}

// Durasi dalam bulan, dihitung dari tanggal. plusDays(1) supaya tanggal selesai ikut terhitung:
// 1 Agt - 30 Sep = 2 bulan (sesuai contoh spesifikasi). Minimal 1 bulan.
fun durasiBulan(mulai: String, selesai: String): Int = runCatching {
    val bulan = ChronoUnit.MONTHS.between(LocalDate.parse(mulai), LocalDate.parse(selesai).plusDays(1))
    maxOf(bulan.toInt(), 1)
}.getOrDefault(1)