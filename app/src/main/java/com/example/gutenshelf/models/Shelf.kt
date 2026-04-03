package com.example.gutenshelf.models

data class Shelf(
    val id: Int,
    val name: String,
    var bookReferences: List<BookReference> = emptyList(),
    val isPinned: Boolean = false
)

data class BookReference(
    val bookId: Int,
    val bookType: BookType
)