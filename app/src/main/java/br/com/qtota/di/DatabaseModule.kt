package br.com.qtota.di

import android.content.Context
import androidx.room.Room
import br.com.qtota.data.local.AppDatabase
import br.com.qtota.data.local.dao.ProductDAO
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@dagger.Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @dagger.Provides
    @Singleton
    fun provideDatabase(@ApplicationContext ctx: Context): AppDatabase {
        return Room.databaseBuilder(ctx, AppDatabase::class.java, "qtota.db")
            .fallbackToDestructiveMigration(false)
            .build()
    }

    @dagger.Provides
    fun provideProductDAO(db: AppDatabase): ProductDAO {
        return db.productDAO()
    }

}