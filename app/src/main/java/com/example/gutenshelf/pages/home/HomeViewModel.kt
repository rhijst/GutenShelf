package com.example.gutenshelf.pages.home

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gutenshelf.cache.BookDiskCache
import com.example.gutenshelf.cache.CacheType
import com.example.gutenshelf.cache.ShelfDiskCache
import com.example.gutenshelf.models.Book
import com.example.gutenshelf.models.BookType
import com.example.gutenshelf.models.Shelf
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

    var pinnedShelvesWithBooks by mutableStateOf<List<Pair<Shelf, List<Book>>>>(emptyList())
        private set

    var isLoading by mutableStateOf(true)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch(Dispatchers.IO) {
            val cachedBooks = BookDiskCache.load(context, CacheType.NETWORK_BOOKS)
            val customBooks = BookDiskCache.load(context, CacheType.CUSTOM_BOOKS)
            val shelves = ShelfDiskCache.load(context)

            withContext(Dispatchers.Main) {
                if (cachedBooks.isNotEmpty()) {
                    books = cachedBooks
                    isLoading = false
                }
                updatePinnedShelves(shelves, books, customBooks)
            }

            fetchFromApi()
        }
    }

    private fun updatePinnedShelves(shelves: List<Shelf>, apiBooks: List<Book>, customBooks: List<Book>) {
        pinnedShelvesWithBooks = shelves.filter { it.isPinned }.map { shelf ->
            val resolvedBooks = shelf.bookReferences.mapNotNull { ref ->
                when (ref.bookType) {
                    BookType.API -> apiBooks.find { it.id == ref.bookId }
                    BookType.CUSTOM -> customBooks.find { it.id == ref.bookId }
                }
            }
            shelf to resolvedBooks
        }
    }

    fun refreshPinnedShelves() {
        viewModelScope.launch(Dispatchers.IO) {
            val shelves = ShelfDiskCache.load(context)
            val customBooks = BookDiskCache.load(context, CacheType.CUSTOM_BOOKS)
            withContext(Dispatchers.Main) {
                updatePinnedShelves(shelves, books, customBooks)
            }
        }
    }

    private fun fetchFromApi() {
        isLoading = books.isEmpty()

        repository.fetchBooks(
            onSuccess = { freshBooks ->
                books = freshBooks
                isLoading = false
                BookDiskCache.save(context, freshBooks, CacheType.NETWORK_BOOKS)
                
                viewModelScope.launch(Dispatchers.IO) {
                    val shelves = ShelfDiskCache.load(context)
                    val customBooks = BookDiskCache.load(context, CacheType.CUSTOM_BOOKS)
                    withContext(Dispatchers.Main) {
                        updatePinnedShelves(shelves, freshBooks, customBooks)
                    }
                }
            },
            onError = { error ->
                errorMessage = error
                isLoading = false
            }
        )
    }
}