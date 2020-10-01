package com.lucidsoftworksllc.sabotcommunity.db.di

import com.lucidsoftworksllc.sabotcommunity.db.notifications.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

@InstallIn(ApplicationComponent::class)
@Module
object RepositoryModule {

    @Singleton
    @Provides
    fun provideNotiRepository(
            notiDao: NotificationDao,
            retrofit: NotificationRetrofit,
            cacheMapper: NotificationCacheMapper,
            networkMapper: NotificationNetworkMapper
    ): NotificationRepository{
        return NotificationRepository(notiDao,retrofit,cacheMapper,networkMapper)
    }
}