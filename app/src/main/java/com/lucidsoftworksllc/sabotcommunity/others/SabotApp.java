package com.lucidsoftworksllc.sabotcommunity.others;

import android.app.Application;
import android.content.Context;

import org.acra.ACRA;
import org.acra.BuildConfig;
import org.acra.annotation.AcraCore;
import org.acra.annotation.AcraHttpSender;
import org.acra.data.StringFormat;
import org.acra.sender.HttpSender;

import dagger.hilt.android.HiltAndroidApp;

/*
@HiltAndroidApp
class HiltApp extends Application{}

@AcraCore(buildConfigClass = BuildConfig.class, reportFormat = StringFormat.JSON)
@AcraHttpSender(uri = "https://collector.tracepot.com/e9d39a3e", httpMethod = HttpSender.Method.POST)
class ACRAApp extends HiltApp {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        ACRA.init(this);
    }
}

public class SabotApp extends ACRAApp{}*/
