package br.com.qtota.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import br.com.qtota.data.remote.APIService
import br.com.qtota.data.repository.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import br.com.qtota.BuildConfig

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides @Singleton
    fun provideAPIService(retrofit: Retrofit): APIService {
        return retrofit.create(APIService::class.java)
    }

    @Provides @Singleton
    fun provideUserRepository(apiService: APIService, dataStore: DataStore<Preferences>) : UserRepository {
        return UserRepository(apiService, dataStore)
    }

}