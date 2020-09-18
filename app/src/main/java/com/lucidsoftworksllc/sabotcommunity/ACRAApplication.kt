package com.lucidsoftworksllc.sabotcommunity

import android.app.Application
import android.content.Context
import org.acra.ACRA
import org.acra.BuildConfig
import org.acra.annotation.AcraCore
import org.acra.annotation.AcraHttpSender
import org.acra.data.StringFormat
import org.acra.sender.HttpSender

/*
@AcraCore(buildConfigClass = BuildConfig::class, reportFormat = StringFormat.JSON, additionalSharedPreferences = ["SharedPrefManager"])
@AcraHttpSender(uri = "http://www.sabotcommunity.com/acra.php", httpMethod = HttpSender.Method.POST, basicAuthLogin = "yeetskeet", basicAuthPassword = "sabot")
class ACRAApplication : Application() {
    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        ACRA.init(this)
    }
}*/
