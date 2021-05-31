package com.snad.core.network

import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import java.io.File
import javax.inject.Singleton

@Module
class NetworkModule {
    @Singleton
    @Provides
    fun provideRetrofit(cacheDir: File): Retrofit = RetrofitClient(cacheDir).get()
}