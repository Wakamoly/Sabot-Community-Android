package com.lucidsoftworksllc.sabotcommunity.others

import android.app.Application
import android.content.Context
import com.lucidsoftworksllc.sabotcommunity.BuildConfig
import dagger.hilt.android.HiltAndroidApp
import org.acra.ACRA
import org.acra.annotation.AcraCore
import org.acra.annotation.AcraHttpSender
import org.acra.data.StringFormat
import org.acra.sender.HttpSender

/*
@HiltAndroidApp
open class HiltApplication : Application()

@AcraCore(buildConfigClass = BuildConfig::class, reportFormat = StringFormat.JSON)
@AcraHttpSender(uri = "https://collector.tracepot.com/e9d39a3e", httpMethod = HttpSender.Method.POST)
open class ACRAApplication : HiltApplication() {
    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        ACRA.init(this)
    }
}

class SabotApplication : ACRAApplication()*/
