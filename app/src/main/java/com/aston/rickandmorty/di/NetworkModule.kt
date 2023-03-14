package com.aston.rickandmorty.di

import com.aston.rickandmorty.data.apiCalls.ApiCall
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

@Module
interface NetworkModule {

    companion object {
        @Provides
        @ApplicationScope
        fun provideApiCall(retrofit: Retrofit): ApiCall {
            return retrofit.create()
        }

        @ApplicationScope
        @Provides
        fun provideRetrofit(
            client: OkHttpClient,
            converterFactory: GsonConverterFactory,
            callAdapterFactory: RxJava2CallAdapterFactory,
        ): Retrofit {
            return Retrofit.Builder()
                .client(client)
                .addConverterFactory(converterFactory)
                .addCallAdapterFactory(callAdapterFactory)
                .baseUrl(BASE_URL)
                .build()
        }

        @ApplicationScope
        @Provides
        fun provideConverterFactory(): GsonConverterFactory {
            return GsonConverterFactory.create()
        }

        @ApplicationScope
        @Provides
        fun provideCallAdapterFactory(): RxJava2CallAdapterFactory {
            return RxJava2CallAdapterFactory.create()
        }

        @ApplicationScope
        @Provides
        fun provideOkHttpClient(): OkHttpClient {
            val interceptor =
                HttpLoggingInterceptor().apply { setLevel(HttpLoggingInterceptor.Level.BODY) }
            return OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build()
        }

        private const val BASE_URL = "https://rickandmortyapi.com/api/"
    }
}