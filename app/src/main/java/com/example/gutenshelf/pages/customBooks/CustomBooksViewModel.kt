package com.example.gutenshelf.models

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gutenshelf.cache.BookDiskCache
import com.example.gutenshelf.cache.CacheType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class CustomBooksViewModel : ViewModel() {
    var customBooks by mutableStateOf<List<Book>>(emptyList())
        private set

    var message by mutableStateOf<String?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    fun loadCustomBooks(context: Context) {
        if (customBooks.isNotEmpty()) return

        isLoading = true

        // CoroutineScope to load books
        viewModelScope.launch(Dispatchers.IO) {
            val loaded = BookDiskCache.load(context, CacheType.CUSTOM_BOOKS)

            // Update main thread
            withContext(Dispatchers.Main) {
                isLoading = false
                customBooks = loaded
            }
        }
    }


    fun addBook(
        context: Context,
        title: String,
        authors: List<Author>,
        localCover: Bitmap? = null,
        summaries: List<String> = emptyList(),
        subjects: List<String> = emptyList(),
        languages: List<String> = emptyList()
    ) {
        if (title.isBlank() || authors.isEmpty()) {
            message = "Please fill in at least title and one author"
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            val books = customBooks.toMutableList()
            val newBookId = (books.maxOfOrNull { it.id } ?: 0) + 1

            // Save bitmap to file if exists
            val coverPath = localCover?.let { saveBitmapToFile(context, it, newBookId) }

            val newBook = Book(
                id = newBookId,
                title = title,
                authors = authors,
                localCover = localCover,
                formats = coverPath?.let { mapOf("image/jpeg" to it) } ?: emptyMap(),
                summaries = summaries,
                subjects = subjects,
                languages = languages
            )

            books.add(newBook)
            BookDiskCache.save(context, books, CacheType.CUSTOM_BOOKS)

            withContext(Dispatchers.Main) {
                customBooks = books
                message = "Book added successfully!"
            }
        }
    }

    fun updateBook(
        context: Context,
        bookId: Int,
        title: String,
        authors: List<Author>,
        localCover: Bitmap? = null,
        summaries: List<String> = emptyList(),
        subjects: List<String> = emptyList(),
        languages: List<String> = emptyList()
    ) {
        if (title.isBlank() || authors.isEmpty()) {
            message = "Please fill in at least title and one author"
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            val books = customBooks.toMutableList()
            val index = books.indexOfFirst { it.id == bookId }

            if (index != -1) {
                val existing = books[index]

                // If new cover provided → overwrite file
                val coverPath = localCover?.let {
                    saveBitmapToFile(context, it, bookId)
                } ?: existing.formats["image/jpeg"]

                val updatedBook = existing.copy(
                    title = title,
                    authors = authors,
                    localCover = localCover ?: existing.localCover,
                    formats = coverPath?.let { mapOf("image/jpeg" to it) } ?: emptyMap(),
                    summaries = summaries,
                    subjects = subjects,
                    languages = languages
                )

                books[index] = updatedBook
                BookDiskCache.save(context, books, CacheType.CUSTOM_BOOKS)

                withContext(Dispatchers.Main) {
                    customBooks = books
                    message = "Book updated successfully!"
                }
            }
        }
    }

    fun removeBook(
        context: Context,
        bookId: Int
    )
    {
        viewModelScope.launch(Dispatchers.IO) {
            val books = customBooks.toMutableList()
            val bookToRemove = books.find { it.id == bookId }

            if (bookToRemove != null) {

                // Delete cover file if it exists
                bookToRemove.formats["image/jpeg"]?.let { path ->
                    val file = File(path)
                    if (file.exists()) file.delete()
                }

                books.remove(bookToRemove)
                BookDiskCache.save(context, books, CacheType.CUSTOM_BOOKS)

                withContext(Dispatchers.Main) {
                    customBooks = books
                    message = "Book removed successfully!"
                }
            }
        }
    }

    fun clearMessage() {
        message = null
    }

    fun saveBitmapToFile(context: Context, bitmap: Bitmap, bookId: Int): String {
        val file = File(context.filesDir, "cover_$bookId.png")
        file.outputStream().use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }
        return file.absolutePath
    }

}