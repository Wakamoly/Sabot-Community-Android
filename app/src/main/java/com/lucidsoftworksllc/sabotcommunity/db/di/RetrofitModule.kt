package com.lucidsoftworksllc.sabotcommunity.db.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.lucidsoftworksllc.sabotcommunity.db.notifications.NotificationRetrofit
import com.lucidsoftworksllc.sabotcommunity.others.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object RetrofitModule {

    @Singleton
    @Provides
    fun provideGsonBuilder(): Gson {
        return GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()
    }


    @Singleton
    @Provides
    fun provideRetrofit(gson: Gson): Retrofit.Builder{
        return Retrofit.Builder().baseUrl(Constants.ROOT_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
    }

    @Singleton
    @Provides
    fun provideNotificationService(retrofit: Retrofit.Builder): NotificationRetrofit{
        return retrofit.build().create(NotificationRetrofit::class.java)
    }
}