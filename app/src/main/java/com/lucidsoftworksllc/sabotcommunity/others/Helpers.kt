package com.lucidsoftworksllc.sabotcommunity.others

import android.content.Context
import android.widget.Toast

fun Context.toastShort(message:String) =
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

fun Context.toastLong(message:String) =
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()

val Context.deviceUserID: String?
    get() { return SharedPrefManager.getInstance(this)!!.username }

val Context.deviceUsername: String?
    get() { return SharedPrefManager.getInstance(this)!!.username }