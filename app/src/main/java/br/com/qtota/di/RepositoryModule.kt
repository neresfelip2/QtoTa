package br.com.qtota.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import br.com.qtota.data.local.dao.ProductDAO
import br.com.qtota.data.remote.APIService
import br.com.qtota.data.repository.ProductRepository
import br.com.qtota.data.repository.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideUserRepository(
        apiService: APIService,
        dataStore: DataStore<Preferences>
    ): UserRepository {
        return UserRepository(apiService, dataStore)
    }

    @Provides
    fun provideProductRepository(apiService: APIService, productDAO: ProductDAO): ProductRepository {
        return ProductRepository(apiService, productDAO)
    }

}