package com.example.gutenshelf.pages.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.gutenshelf.composables.HeaderSection
import com.example.gutenshelf.composables.BookRow
import com.example.gutenshelf.models.BooksViewModel
import com.example.gutenshelf.network.BookRepository

@Composable
fun HomeScreen() {
    val context = LocalContext.current

    val repository = remember { BookRepository(context) }
    val viewModel = remember { BooksViewModel(context, repository) }

    val books = viewModel.books
    val isLoading = viewModel.isLoading
    val errorMessage = viewModel.errorMessage

    Box(modifier = Modifier.fillMaxSize()) {

        if (isLoading && books.isEmpty()) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else if (errorMessage != null && books.isEmpty()) {
            Text(
                text = "Error: $errorMessage",
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            val recommended = remember(books) { books.reversed() }
            val newReleases = remember(books) { books.shuffled() }

            LazyColumn {
                item { HeaderSection("Featured books & shelf's") }
                item { BookRow(title = "Popular", books = books) }
                item { BookRow(title = "Recommended", books = recommended) }
                item { BookRow(title = "New Releases", books = newReleases) }
            }
        }
    }
}