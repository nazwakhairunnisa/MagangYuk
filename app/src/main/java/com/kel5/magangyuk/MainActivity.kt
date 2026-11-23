package com.kel5.magangyuk

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.kel5.magangyuk.data.local.entity.Role
import com.kel5.magangyuk.ui.navigation.AppNavHost
import com.kel5.magangyuk.ui.theme.MagangYukTheme
import com.kel5.magangyuk.util.DeepLinkParser
import com.kel5.magangyuk.util.NotifikasiIntentParser
import com.kel5.magangyuk.util.NotifikasiTap

class MainActivity : ComponentActivity() {

    // Berubah kalau ada deep link login baru masuk saat app sudah terbuka
    private var linkRole by mutableStateOf<Role?>(null)

    // Notifikasi status bar yang baru diketuk dan belum diproses. null = tidak ada.
    private var notifTap by mutableStateOf<NotifikasiTap?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        linkRole = DeepLinkParser.roleFromIntent(intent)

        // Hanya baca saat peluncuran baru. Saat rotasi layar savedInstanceState != null,
        // dan intent lama masih sama, jadi tanpa pengecekan ini navigasinya terulang.
        if (savedInstanceState == null) {
            notifTap = NotifikasiIntentParser.fromIntent(intent)
        }

        setContent {
            MagangYukTheme {
                key(linkRole) {
                    AppNavHost(
                        linkRole = linkRole,
                        notifTap = notifTap,
                        onNotifTapConsumed = { notifTap = null }
                    )
                }
            }
        }
    }

    // Dipanggil kalau intent baru masuk saat Activity sudah hidup (efek singleTop)
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)

        val tap = NotifikasiIntentParser.fromIntent(intent)
        if (tap != null) {
            // Dari notifikasi: JANGAN sentuh linkRole, supaya navigasi tidak ter-reset
            notifTap = tap
        } else {
            linkRole = DeepLinkParser.roleFromIntent(intent)
        }
        Log.d("DeepLink", "onNewIntent: data=${intent.data}, linkRole=$linkRole, notifTap=$notifTap")
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MagangYukTheme {
        Greeting("Android")
    }
}