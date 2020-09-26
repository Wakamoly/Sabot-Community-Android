package com.lucidsoftworksllc.sabotcommunity.others;

import android.app.Application;
import android.content.Context;

import com.lucidsoftworksllc.sabotcommunity.BuildConfig;

import org.acra.*;
import org.acra.annotation.*;
import org.acra.data.StringFormat;
import org.acra.sender.HttpSender;

@AcraCore(buildConfigClass = BuildConfig.class,
        reportFormat = StringFormat.JSON)
@AcraHttpSender(uri = "https://collector.tracepot.com/e9d39a3e",httpMethod = HttpSender.Method.POST)
public class ACRAApplication extends Application {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        ACRA.init(this);
    }
}