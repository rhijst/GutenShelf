package com.example.gutenshelf.models

import android.graphics.Bitmap

data class Book(
    val id: Int,
    val title: String,
    val authors: List<Author>,
    val formats: Map<String, String> = emptyMap(), // remote URLs
    var localCover: Bitmap? = null, // for custom books
    val summaries: List<String> = emptyList(),
    val subjects: List<String> = emptyList(),
    val languages: List<String> = emptyList(),
    val download_count: Int = 0
) {
    val coverUrl: String?
        get() = formats["image/jpeg"]
}

data class Author(
    val name: String,
    val birth_year: Int? = null,
    val death_year: Int? = null
)
