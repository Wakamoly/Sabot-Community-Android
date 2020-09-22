package com.lucidsoftworksllc.sabotcommunity.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
        entities = [PublicsEntity::class],
        version = 1)
abstract class SabotDatabase: RoomDatabase() {

    abstract fun getPublicsDao() : PublicsDao
    //abstract fun getNotificationsDao() : NotificationsDao
    //abstract fun getMessagesDao() : MessagesDao
    //abstract fun getUserMessagesDao() : UserMessagesDao
    //abstract fun getTypedMessageDao() : TypedMessageDao

    companion object {
        @Volatile private var instance : SabotDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance?: synchronized(LOCK){
            instance ?: buildDatabase(context).also {
                instance = it
            }
        }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(
                context.applicationContext,
                SabotDatabase::class.java,
                "sabotdatabase"
        ).build()

    }
}