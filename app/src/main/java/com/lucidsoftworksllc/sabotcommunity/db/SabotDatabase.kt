package com.lucidsoftworksllc.sabotcommunity.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.lucidsoftworksllc.sabotcommunity.db.messages.general.MessagesCacheEntity
import com.lucidsoftworksllc.sabotcommunity.db.messages.general.MessagesDao
import com.lucidsoftworksllc.sabotcommunity.db.messages.typed.TypedMessageDao
import com.lucidsoftworksllc.sabotcommunity.db.messages.typed.TypedMessageEntity
import com.lucidsoftworksllc.sabotcommunity.db.messages.user_info.MessageUserInfoDao
import com.lucidsoftworksllc.sabotcommunity.db.messages.user_info.MessageUserInfoEntity
import com.lucidsoftworksllc.sabotcommunity.db.messages.user_messages.UserMessagesDao
import com.lucidsoftworksllc.sabotcommunity.db.messages.user_messages.UserMessagesEntity
import com.lucidsoftworksllc.sabotcommunity.db.notifications.NotificationCacheEntity
import com.lucidsoftworksllc.sabotcommunity.db.notifications.NotificationDao
import com.lucidsoftworksllc.sabotcommunity.db.publics.PublicsDao
import com.lucidsoftworksllc.sabotcommunity.db.publics.PublicsEntity

@Database(
        entities = [PublicsEntity::class,
            NotificationCacheEntity::class,
            MessagesCacheEntity::class,
            MessageUserInfoEntity::class,
            UserMessagesEntity::class,
            TypedMessageEntity::class],
        version = 18)
abstract class SabotDatabase: RoomDatabase() {

    abstract fun getPublicsDao() : PublicsDao
    abstract fun getNotificationsDao() : NotificationDao
    abstract fun getMessagesDao() : MessagesDao
    abstract fun getUserMessagesDao() : UserMessagesDao
    //abstract fun getGroupMessagesDao() : GroupMessagesDao
    abstract fun getTypedMessageDao() : TypedMessageDao
    abstract fun getMessageUserInfo() : MessageUserInfoDao
    //abstract fun getMessageGroupInfo() : MessageGroupInfoDao



    companion object {
        val DATABASE_NAME: String = "sabotdatabase"
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
        ).fallbackToDestructiveMigration().build()

    }
}