package br.com.qtota.data.repository

import android.Manifest
import android.location.Location
import androidx.annotation.RequiresPermission
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationRepository @Inject constructor(
    private val fusedLocationProviderClient: FusedLocationProviderClient
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

}