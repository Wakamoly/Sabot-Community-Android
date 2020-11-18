package com.lucidsoftworksllc.sabotcommunity.db

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.TypeConverter
import java.time.Instant
import java.util.*

class DbConverters {
    companion object {
        @RequiresApi(Build.VERSION_CODES.O)
        @TypeConverter
        @JvmStatic
        fun fromInstant(value: Instant): Long {
            return value.toEpochMilli()
        }

        @RequiresApi(Build.VERSION_CODES.O)
        @TypeConverter
        @JvmStatic
        fun toInstant(value: Long): Instant {
            return Instant.ofEpochMilli(value)
        }

        /*@TypeConverter
        @JvmStatic
        fun fromDate(value: Date): String {
            return value.toString()
        }

        @TypeConverter
        @JvmStatic
        fun toDate(value: String): Date {
            return Date.format(value)
        }*/


    }
}