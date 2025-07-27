package br.com.qtota.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import br.com.qtota.data.local.dao.ProductDAO
import br.com.qtota.data.remote.APIService
import br.com.qtota.data.repository.ProductRepository
import br.com.qtota.data.repository.StoreRespository
import br.com.qtota.data.repository.UserRepository
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@dagger.Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @dagger.Provides
    @Singleton
    fun provideUserRepository(
        apiService: APIService,
        dataStore: DataStore<Preferences>
    ): UserRepository {
        return UserRepository(apiService, dataStore)
    }

    @dagger.Provides
    @Singleton
    fun provideProductRepository(
        @ApplicationContext context: Context,
        apiService: APIService,
        productDAO: ProductDAO,
    ): ProductRepository {
        return ProductRepository(context, apiService, productDAO)
    }

    @dagger.Provides
    @Singleton
    fun provideStoreRepository(apiService: APIService): StoreRespository {
        return StoreRespository(apiService)
    }

}