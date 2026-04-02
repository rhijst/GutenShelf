package com.example.gutenshelf.pages.home

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gutenshelf.cache.BookDiskCache
import com.example.gutenshelf.cache.CacheType
import com.example.gutenshelf.models.Book
import com.example.gutenshelf.network.BookRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel(
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
        // Load cached books from disk on background thread
        viewModelScope.launch(Dispatchers.IO) {
            val cachedBooks = BookDiskCache.load(context, CacheType.NETWORK_BOOKS)

            // Update variables on the main thread
            withContext(Dispatchers.Main) {
                if (cachedBooks.isNotEmpty()) {
                    books = cachedBooks
                    isLoading = false
                }
            }

            // Fetch new data (network call can stay as-is, Volley handles it on background)
            fetchFromApi()
        }
    }

    private fun fetchFromApi() {
        // Show loading spinner if there are no books yet
        isLoading = books.isEmpty()

        repository.fetchBooks(
            onSuccess = { freshBooks ->
                books = freshBooks
                isLoading = false
                BookDiskCache.save(context, freshBooks, CacheType.NETWORK_BOOKS)
            },
            onError = { error ->
                errorMessage = error
                isLoading = false
            }
        )
    }
}