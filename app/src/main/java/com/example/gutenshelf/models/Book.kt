package com.example.gutenshelf.models

import android.graphics.Bitmap

enum class BookType {
    API,
    CUSTOM
}

data class Book(
    val id: Int,
    val type: BookType = BookType.API,
    val title: String,
    val authors: List<Author>,
    val summaries: List<String> = emptyList(),
    val subjects: List<String> = emptyList(),
    val languages: List<String> = emptyList(),
    val download_count: Int = 0,
    val formats: Map<String, String> = emptyMap(), // Includes the url to the cover

//    Local cover
    val localCoverPath: String? = null,
    var localCover: Bitmap? = null, // for custom books
) {
    val coverUrl: String?
        get() = when (type) {
            BookType.API -> formats["image/jpeg"]
            BookType.CUSTOM -> localCoverPath
        }

}

