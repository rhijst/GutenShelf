package com.example.gutenshelf.models

data class Book(
    val id: Int,
    val title: String,
    val authors: List<Author>,
    val formats: Map<String, String>
) {
    val coverUrl: String?
        get() = formats["image/jpeg"]
}

data class Author(
    val name: String
)
