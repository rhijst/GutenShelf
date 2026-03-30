package com.example.gutenshelf.models

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.gutenshelf.cache.BookDiskCache
import com.example.gutenshelf.network.BookRepository

class BooksViewModel(
    private val context: Context,
    private val repository: BookRepository
) : ViewModel() {

    var books by mutableStateOf<List<Book>>(emptyList())
        private set

    var isLoading by mutableStateOf(true)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    init {
        loadBooks()
    }

    private fun loadBooks() {
        // Load cached books
        val cachedBooks = BookDiskCache.load(context)

        if (cachedBooks.isNotEmpty()) {
            books = cachedBooks
            isLoading = false // show immediately in the UI
        }

        // fetch new data from UI
        fetchFromApi()
    }

    private fun fetchFromApi() {
        // Show loading spinner if there are no books yet
        isLoading = books.isEmpty()

        repository.fetchBooks(
            onSuccess = { freshBooks ->
                books = freshBooks
                isLoading = false

                // Refresh data on disk
                BookDiskCache.save(context, freshBooks)
            },
            onError = { error ->
                errorMessage = error
                isLoading = false
            }
        )
    }
}