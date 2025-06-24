package br.com.qtota

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.navigation.compose.rememberNavController
import br.com.qtota.ui.theme.QtoTaTheme
import dagger.hilt.android.AndroidEntryPoint
import androidx.compose.runtime.getValue
import br.com.qtota.ui.navigation.AppNavHost
import br.com.qtota.ui.navigation.AppRoutes

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            enableEdgeToEdge()
            QtoTaTheme {
                val isFirstAccess by viewModel.isFirstAccess.collectAsState()
                isFirstAccess?.let {
                    AppNavHost(rememberNavController(), if (it) AppRoutes.Login.route else AppRoutes.Home.route)
                }
            }
        }
    }
}