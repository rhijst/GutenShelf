package com.example.gutenshelf.models

data class Shelf(
    val id: Int,
    val name: String,
    val bookReferences: List<BookReference> = emptyList()
)

data class BookReference(
    val bookId: Int,
    val bookType: BookType
)