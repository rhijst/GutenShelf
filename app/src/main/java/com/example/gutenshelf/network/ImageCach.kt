package com.example.gutenshelf.network

import android.graphics.Bitmap

object ImageCache {
    private val cache = mutableMapOf<String, Bitmap>()

    fun get(url: String): Bitmap? = cache[url]

    fun put(url: String, bitmap: Bitmap) {
        cache[url] = bitmap
    }
}