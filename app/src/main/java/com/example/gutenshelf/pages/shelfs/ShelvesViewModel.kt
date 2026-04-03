package com.example.gutenshelf.models

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gutenshelf.cache.ShelfDiskCache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ShelvesViewModel : ViewModel() {

    var shelves by mutableStateOf<List<Shelf>>(emptyList())
        private set

    var message by mutableStateOf<String?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    fun loadShelves(context: Context) {
        isLoading = true
        viewModelScope.launch(Dispatchers.IO) {
            val loaded = ShelfDiskCache.load(context)
            withContext(Dispatchers.Main) {
                shelves = loaded
                isLoading = false
            }
        }
    }

    fun addShelf(context: Context, name: String) {
        if (name.isBlank()) {
            message = "Shelf name cannot be empty"
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            val newShelves = shelves.toMutableList()
            val newId = (newShelves.maxOfOrNull { it.id } ?: 0) + 1
            val newShelf = Shelf(id = newId, name = name)
            newShelves.add(newShelf)
            ShelfDiskCache.save(context, newShelves)
            withContext(Dispatchers.Main) {
                shelves = newShelves
                message = "Shelf added successfully"
            }
        }
    }

    fun updateShelf(context: Context, shelfId: Int, name: String) {
        if (name.isBlank()) {
            message = "Shelf name cannot be empty"
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            val newShelves = shelves.toMutableList()
            val index = newShelves.indexOfFirst { it.id == shelfId }
            if (index != -1) {
                newShelves[index] = newShelves[index].copy(name = name)
                ShelfDiskCache.save(context, newShelves)
                withContext(Dispatchers.Main) {
                    shelves = newShelves
                    message = "Shelf updated successfully"
                }
            }
        }
    }

    fun removeShelf(context: Context, shelfId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val newShelves = shelves.toMutableList()
            newShelves.removeAll { it.id == shelfId }
            ShelfDiskCache.save(context, newShelves)
            withContext(Dispatchers.Main) {
                shelves = newShelves
                message = "Shelf removed successfully"
            }
        }
    }

    fun resolveBooks(
        references: List<BookReference>,
        apiBooks: List<Book>,
        customBooks: List<Book>
    ): List<Book> {
        return references.mapNotNull { ref ->
            when (ref.bookType) {
                BookType.API -> apiBooks.find { it.id == ref.bookId }
                BookType.CUSTOM -> customBooks.find { it.id == ref.bookId }
            }
        }
    }

    fun clearMessage() {
        message = null
    }
}