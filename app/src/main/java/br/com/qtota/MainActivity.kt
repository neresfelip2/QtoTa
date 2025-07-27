package br.com.qtota

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.rememberNavController
import br.com.qtota.ui.navigation.AppRoute
import br.com.qtota.ui.navigation.AppNavHost
import br.com.qtota.ui.screen.main_navigation.MainNavigationViewModel
import br.com.qtota.ui.theme.QtoTaTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainNavigationViewModel by viewModels()

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            enableEdgeToEdge()
            QtoTaTheme {

                val isFirstAccess by viewModel.isFirstAccess.collectAsState()

                val navController = rememberNavController()

                isFirstAccess?.let { isFirstAccess ->
                    Scaffold {
                        AppNavHost(
                            navController,
                            if (isFirstAccess) AppRoute.Login.route else AppRoute.Main.route
                        )
                    }
                }
            }
        }
    }

}