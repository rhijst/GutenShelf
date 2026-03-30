package com.example.gutenshelf.cache

enum class CacheType(val fileName: String) {
    NETWORK_BOOKS("books_cache.json"),
    CUSTOM_BOOKS("custom_books.json")
}