package br.com.qtota.di

import br.com.qtota.BuildConfig
import br.com.qtota.data.remote.APIService
import br.com.qtota.data.remote.adapters.LocalDateAdapter
import br.com.qtota.data.remote.adapters.MeasureTypeAdapter
import br.com.qtota.data.remote.product.MeasureType
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDate
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@dagger.Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    /*@Provides
    @Singleton
    fun provideAuthInterceptor(): Interceptor {
        return Interceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer ${getTokenFromPrefs(context)}")
                .build()
            chain.proceed(request)
        }
    }*/

    @dagger.Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @dagger.Provides
    @Singleton
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        //authInterceptor: Interceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)   // tempo máximo para abrir a conexão
            .readTimeout(30, TimeUnit.SECONDS)      // tempo máximo para ler resposta
            .writeTimeout(30, TimeUnit.SECONDS)     // tempo máximo para enviar corpo da requisição
            //.addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @dagger.Provides
    @Singleton
    fun provideGsonTypeAdapter(): Gson {
        return GsonBuilder()
            .registerTypeAdapter(LocalDate::class.java, LocalDateAdapter())
            .registerTypeAdapter(MeasureType::class.java, MeasureTypeAdapter())
            .create()
    }

    @dagger.Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, typeAdapter: Gson): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.API_URL)
            .addConverterFactory(GsonConverterFactory.create(typeAdapter))
            .client(okHttpClient)
            .build()
    }

    @dagger.Provides
    @Singleton
    fun provideAPIService(retrofit: Retrofit): APIService {
        return retrofit.create(APIService::class.java)
    }

}
