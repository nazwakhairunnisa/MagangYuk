package com.kel5.magangyuk

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.kel5.magangyuk.data.local.entity.Role
import com.kel5.magangyuk.ui.auth.LoginScreen
import com.kel5.magangyuk.ui.auth.SplashScreen
import com.kel5.magangyuk.ui.common.AppHeader
import com.kel5.magangyuk.ui.navigation.AppNavHost
import com.kel5.magangyuk.ui.theme.MagangYukTheme
import com.kel5.magangyuk.util.DeepLinkParser

// Tujuan sementara, nanti digantikan NavHost di feature/navigation
private sealed interface TempScreen {
    data object Splash : TempScreen
    data class Login(val role: Role) : TempScreen
    data class Home(val role: Role) : TempScreen
}

class MainActivity : ComponentActivity() {

    // Berubah kalau ada deep link baru masuk saat app sudah terbuka
    private var linkRole by mutableStateOf<Role?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        linkRole = DeepLinkParser.roleFromIntent(intent)

        setContent {
            MagangYukTheme {
                // key(linkRole): deep link baru -> NavController dibuat ulang,
                // alur mulai lagi dari Splash (perilaku yang sudah kamu tes sebelumnya)
                key(linkRole) {
                    AppNavHost(linkRole = linkRole)
                }
            }
        }
    }

    // Dipanggil kalau link baru masuk saat Activity sudah hidup (efek singleTop)
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        linkRole = DeepLinkParser.roleFromIntent(intent)
        Log.d("DeepLink", "onNewIntent: data=${intent.data}, linkRole=$linkRole")
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