package com.example.gutenshelf.pages.search

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.example.gutenshelf.models.Book
import com.example.gutenshelf.network.BookRepository

class SearchViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = BookRepository(application)
    
    var searchQuery by mutableStateOf("")
        private set

    var lastSearchedQuery by mutableStateOf("")
        private set

    var filteredBooks by mutableStateOf<List<Book>>(emptyList())
        private set

    var isSearching by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun onSearchQueryChange(newQuery: String) {
        searchQuery = newQuery
        // Reset everything if the query is cleared
        if (newQuery.isBlank()) {
            filteredBooks = emptyList()
            lastSearchedQuery = ""
            errorMessage = null
        }
    }

    fun onSearchTriggered() {
        val query = searchQuery.trim()
        if (query.isBlank()) return

        isSearching = true
        errorMessage = null
        lastSearchedQuery = query
        
        // Using fetchBooksByAuthor which uses the 'search' API parameter internally
        repository.fetchBooksBy(
            searchQuery = query,
            onSuccess = { books ->
                filteredBooks = books
                isSearching = false
            },
            onError = { error ->
                errorMessage = error
                isSearching = false
            }
        )
    }
}
