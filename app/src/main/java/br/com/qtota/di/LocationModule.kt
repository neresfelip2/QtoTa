package br.com.qtota.di

import android.content.Context
import br.com.qtota.data.repository.LocationRepository
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@dagger.Module
@InstallIn(SingletonComponent::class)
object LocationModule {

    @dagger.Provides
    @Singleton
    fun provideFusedLocationClient(@ApplicationContext ctx: Context): FusedLocationProviderClient {
        return LocationServices.getFusedLocationProviderClient(ctx)
    }

    @dagger.Provides
    @Singleton
    fun provideLocationRepository(fusedLocationProviderClient: FusedLocationProviderClient, @ApplicationContext context: Context): LocationRepository {
        return LocationRepository(fusedLocationProviderClient, context)
    }

}
