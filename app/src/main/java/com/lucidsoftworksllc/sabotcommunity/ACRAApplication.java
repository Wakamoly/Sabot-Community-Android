package com.lucidsoftworksllc.sabotcommunity;

import android.app.Application;
import android.content.Context;

import org.acra.*;
import org.acra.annotation.*;
import org.acra.data.StringFormat;
import org.acra.sender.HttpSender;

/*@ReportsCrashes(
        formUri = "http://www.sabotcommunity.com/acra.php",
        formUriBasicAuthLogin = "yeetskeet", // optional
        formUriBasicAuthPassword = "sabot",  // optional
        reportType = org.acra.sender.HttpSender.Type.JSON
)
@AcraMailSender(mailTo = "crashes@sabotcommunity.com", //crash report email
                reportAsFile = true)*/
@AcraCore(buildConfigClass = BuildConfig.class,
        reportFormat = StringFormat.JSON,additionalSharedPreferences={"SharedPrefManager"})
@AcraHttpSender(uri = "http://www.sabotcommunity.com/acra.php",httpMethod = HttpSender.Method.POST,basicAuthLogin = "yeetskeet",basicAuthPassword = "sabot")
public class ACRAApplication extends Application {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        ACRA.init(this);
    }
}