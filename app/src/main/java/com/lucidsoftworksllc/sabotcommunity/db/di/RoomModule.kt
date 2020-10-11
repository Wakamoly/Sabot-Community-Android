package com.lucidsoftworksllc.sabotcommunity.db.di

import android.content.Context
import androidx.room.Room
import com.lucidsoftworksllc.sabotcommunity.db.SabotDatabase
import com.lucidsoftworksllc.sabotcommunity.db.messages.general.MessagesDao
import com.lucidsoftworksllc.sabotcommunity.db.notifications.NotificationDao
import com.lucidsoftworksllc.sabotcommunity.db.publics.PublicsDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@InstallIn(ApplicationComponent::class)
@Module
object RoomModule {

    @Singleton
    @Provides
    fun provideSabotDb(@ApplicationContext context: Context): SabotDatabase{
        return Room.databaseBuilder(context, SabotDatabase::class.java, SabotDatabase.DATABASE_NAME).fallbackToDestructiveMigration().build()
    }

    @Singleton
    @Provides
    fun provideNotiDAO(sabotDatabase: SabotDatabase): NotificationDao{
        return sabotDatabase.getNotificationsDao()
    }

    @Singleton
    @Provides
    fun provideMessagesDAO(sabotDatabase: SabotDatabase): MessagesDao{
        return sabotDatabase.getMessagesDao()
    }

    @Singleton
    @Provides
    fun providePublicsDAO(sabotDatabase: SabotDatabase): PublicsDao{
        return sabotDatabase.getPublicsDao()
    }
}