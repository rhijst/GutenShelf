package com.example.gutenshelf.network

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley

class VolleySingleton private constructor(context: Context) {
    companion object {
        @Volatile
        private var INSTANCE: VolleySingleton? = null
        fun getInstance(context: Context) =
            INSTANCE ?: synchronized(this) {
                VolleySingleton(context).also {
                    INSTANCE = it
                }
            }
    }

    private val requestQueue: RequestQueue by lazy {
        Volley.newRequestQueue(context.applicationContext)
    }

    fun <T> addToRequestQueue(req: Request<T>) {
        val url = try {
            req.url
        } catch (e: Exception) {
            "Unknown URL"
        }
        Log.d("VolleySingleton", "Added request to queue: $url")

        requestQueue.add(req)
    }
}
