package com.lucidsoftworksllc.sabotcommunity.others

import android.content.Context
import android.graphics.Bitmap
import android.util.LruCache
import com.android.volley.Cache
import com.android.volley.Network
import com.android.volley.RequestQueue
import com.android.volley.toolbox.*

class DashSliderRequest private constructor(context: Context) {
    private var requestQueue: RequestQueue?
    private val imageLoader: ImageLoader
    private fun getRequestQueue(): RequestQueue {
        if (requestQueue == null) {
            val cache: Cache = DiskBasedCache(context!!.cacheDir, 10 * 1024 * 1024)
            val network: Network = BasicNetwork(HurlStack())
            requestQueue = RequestQueue(cache, network)
            requestQueue!!.start()
        }
        return requestQueue!!
    }

    fun addToRequestQueue(req: JsonArrayRequest) {
        getRequestQueue().add(req)
    }

    companion object {
        private var dashSliderRequest: DashSliderRequest? = null
        private var context: Context? = null
        @Synchronized
        fun getInstance(context: Context): DashSliderRequest? {
            if (dashSliderRequest == null) {
                dashSliderRequest = DashSliderRequest(context)
            }
            return dashSliderRequest
        }
    }

    init {
        Companion.context = context
        requestQueue = getRequestQueue()
        imageLoader = ImageLoader(requestQueue, object : ImageLoader.ImageCache {
            private val cache = LruCache<String, Bitmap>(20)
            override fun getBitmap(url: String): Bitmap {
                return cache[url]
            }

            override fun putBitmap(url: String, bitmap: Bitmap) {
                cache.put(url, bitmap)
            }
        })
    }
}