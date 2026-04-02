package com.example.gutenshelf.pages.bookDetail

import android.app.Application
import android.graphics.Bitmap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.android.volley.toolbox.ImageRequest
import com.example.gutenshelf.models.Book
import com.example.gutenshelf.network.BookRepository
import com.example.gutenshelf.network.VolleySingleton

class BookDetailViewModel(
    application: Application,
    private val repository: BookRepository,
    private val bookId: Int
) : AndroidViewModel(application) {

    var book by mutableStateOf<Book?>(null)
        private set

    var isLoading by mutableStateOf(true)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var bitmap by mutableStateOf<Bitmap?>(null)
        private set

    init {
        loadBook()
    }

    private fun loadBook() {
        repository.fetchBookById(
            id = bookId,
            onSuccess = { fetchedBook ->
                book = fetchedBook
                isLoading = false
                fetchCoverImage(fetchedBook.coverUrl)
            },
            onError = { error ->
                errorMessage = error
                isLoading = false
            }
        )
    }

    private fun fetchCoverImage(url: String?) {
        url?.let {
            val imageRequest = ImageRequest(
                it,
                { response -> bitmap = response },
                0, 0, null, Bitmap.Config.RGB_565,
                { /* Handle error */ }
            )
            VolleySingleton.getInstance(getApplication()).addToRequestQueue(imageRequest)
        }
    }

    class Factory(
        private val application: Application,
        private val repository: BookRepository,
        private val bookId: Int
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(BookDetailViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return BookDetailViewModel(application, repository, bookId) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
