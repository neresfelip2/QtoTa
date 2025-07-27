package br.com.qtota.data.repository

import android.Manifest
import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresPermission
import br.com.qtota.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.IOException
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

@Singleton
class LocationRepository @Inject constructor(
    private val fusedLocationProviderClient: FusedLocationProviderClient,
    private val context: Context
) {

    var location: Location? = null

    private val _loadStatus = MutableStateFlow(true)
    val loadStatus = _loadStatus.asStateFlow()

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    internal fun startLocationUpdates() {
        _loadStatus.value = true
        fusedLocationProviderClient.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            CancellationTokenSource().token
        ).addOnCompleteListener { result ->
            if(result.isSuccessful) {
                this.location = result.result
            }
            _loadStatus.value = false
        }
    }

    internal suspend fun getNeighborhood(): String {
        val geocoder = Geocoder(context, Locale.getDefault())
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // Modern API for Android 13+
                val addresses = suspendCancellableCoroutine { continuation ->
                    geocoder.getFromLocation(location!!.latitude, location!!.longitude, 1) { result ->
                        continuation.resume(result) { cause, _, _ ->
                            continuation.resume(emptyList())
                        }
                    }
                }
                if (addresses.isNotEmpty()) {
                    val address = addresses[0]
                    "${address.subLocality ?: address.locality}, ${address.subAdminArea}"
                } else {
                    context.getString(R.string.unavailable)
                }
            } else {
                @Suppress("DEPRECATION")
                val addresses = geocoder.getFromLocation(location!!.latitude, location!!.longitude, 1)
                if (!addresses.isNullOrEmpty()) {
                    val address = addresses[0]
                    "${address.subLocality ?: address.locality}, ${address.subAdminArea}"
                } else {
                    context.getString(R.string.unavailable)
                }
            }
        } catch (e: IOException) {
            Log.d(LocationRepository::class.simpleName, "Erro ao obter informação da localizaçãp: ${e.message}")
            context.getString(R.string.unavailable)
        }
    }

}