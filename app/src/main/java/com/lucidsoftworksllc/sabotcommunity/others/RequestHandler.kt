package com.lucidsoftworksllc.sabotcommunity.others

import android.content.Context
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley

class RequestHandler private constructor(private var mCtx: Context) {
    private var mRequestQueue: RequestQueue?

    // getApplicationContext() is key, it keeps you from leaking the
    // Activity or BroadcastReceiver if someone passes tone in.
    private val requestQueue: RequestQueue?
        get() {
            if (mRequestQueue == null) {
                // getApplicationContext() is key, it keeps you from leaking the
                // Activity or BroadcastReceiver if someone passes tone in.
                mRequestQueue = Volley.newRequestQueue(mCtx.applicationContext)
            }
            return mRequestQueue
        }

    fun <T> addToRequestQueue(req: Request<T>?) {
        requestQueue!!.add(req)
    }

    companion object {
        private var mInstance: RequestHandler? = null
        @Synchronized
        fun getInstance(context: Context): RequestHandler? {
            if (mInstance == null) {
                mInstance = RequestHandler(context)
            }
            return mInstance
        }
    }

    init {
        mRequestQueue = requestQueue
    }
}