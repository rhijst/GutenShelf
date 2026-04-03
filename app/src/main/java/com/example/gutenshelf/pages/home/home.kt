package com.example.gutenshelf.pages.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.gutenshelf.composables.HeaderSection
import com.example.gutenshelf.composables.BookRow
import com.example.gutenshelf.pages.home.HomeViewModel
import com.example.gutenshelf.network.BookRepository
import com.example.gutenshelf.cache.PreferenceStore

@Composable
fun HomeScreen() {
    val context = LocalContext.current

    val repository = remember { BookRepository(context) }
    val viewModel: HomeViewModel = remember { HomeViewModel(context, repository) }
    val preferenceStore = remember { PreferenceStore(context) }

    val books = viewModel.books
    val pinnedShelvesWithBooks = viewModel.pinnedShelvesWithBooks
    val isLoading = viewModel.isLoading
    val errorMessage = viewModel.errorMessage

    val showPopularRow by remember { mutableStateOf(preferenceStore.isPopularRowEnabled()) }

    LaunchedEffect(Unit) {
        viewModel.refreshPinnedShelves()
    }

    Box(modifier = Modifier.fillMaxSize()) {

        if (isLoading && books.isEmpty()) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else if (errorMessage != null && books.isEmpty()) {
            Text(
                text = "Error: $errorMessage",
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            LazyColumn {
                item { HeaderSection("Featured books & shelf's") }
                
                if (showPopularRow) {
                    item { BookRow(title = "Popular", books = books) }
                }

                items(pinnedShelvesWithBooks) { (shelf, shelfBooks) ->
                    if (shelfBooks.isNotEmpty()) {
                        BookRow(title = shelf.name, books = shelfBooks)
                    }
                }
            }
        }
    }
}
