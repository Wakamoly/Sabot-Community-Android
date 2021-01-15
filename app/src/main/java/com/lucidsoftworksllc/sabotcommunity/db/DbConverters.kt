package com.lucidsoftworksllc.sabotcommunity.db

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.time.Instant


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

        @TypeConverter
        fun restoreList(listOfString: String?): List<String?>? {
            return Gson().fromJson(listOfString, object : TypeToken<List<String?>?>() {}.type)
        }

        @TypeConverter
        fun saveList(listOfString: List<String?>?): String? {
            return Gson().toJson(listOfString)
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