package br.com.qtota.ui.screen.menu

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.qtota.data.repository.UserRepository
import com.auth0.jwt.JWT
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MenuViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    internal val user: StateFlow<User?> = userRepository.authTokenFlow.map{ token ->
        token?.let {
            val claims = JWT().decodeJwt(it).claims

            Log.i("teste", claims.toString())

            User(
                id = claims["sub"]!!.asString().toLong(),
                name = claims["name"]!!.asString(),
                email = claims["email"]!!.asString()
            )
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        null
    )

    fun logout() {
        viewModelScope.launch {
            userRepository.deleteAuthToken()
        }
    }

    internal fun openPlayStore(context: Context) {
        val appPackageName = context.packageName
        try {
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    "market://details?id=$appPackageName".toUri()
                )
            )
        } catch (_: ActivityNotFoundException) {
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    "https://play.google.com/store/apps/details?id=$appPackageName".toUri()
                )
            )
        }
    }

}