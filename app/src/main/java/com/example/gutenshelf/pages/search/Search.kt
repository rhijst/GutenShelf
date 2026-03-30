package com.example.gutenshelf.pages.search

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gutenshelf.R
import com.example.gutenshelf.composables.BookGrid

@Composable
fun SearchScreen() {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    
    // Using viewModel() ensures the state persists when navigating back from details
    val viewModel: SearchViewModel = viewModel {
        SearchViewModel(context.applicationContext as Application)
    }
    
    val searchQuery = viewModel.searchQuery
    val lastSearchedQuery = viewModel.lastSearchedQuery
    val books = viewModel.filteredBooks
    val isSearching = viewModel.isSearching
    val errorMessage = viewModel.errorMessage

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.onSearchQueryChange(it) },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search by title or author...") },
            leadingIcon = { 
                Icon(
                    painter = painterResource(R.drawable.search), 
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                ) 
            },
            trailingIcon = {
                if (isSearching) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                } else if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { 
                        viewModel.onSearchTriggered()
                        focusManager.clearFocus()
                    }) {
                        Icon(
                            painter = painterResource(R.drawable.search),
                            contentDescription = "Search",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = {
                viewModel.onSearchTriggered()
                focusManager.clearFocus()
            })
        )

        Spacer(modifier = Modifier.height(16.dp))

        Box(modifier = Modifier.fillMaxSize()) {
            if (errorMessage != null) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else if (isSearching && books.isEmpty()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (books.isEmpty() && lastSearchedQuery.isNotEmpty() && searchQuery == lastSearchedQuery) {
                Text(
                    text = "No results found for \"$lastSearchedQuery\"",
                    modifier = Modifier.align(Alignment.Center)
                )
            } else if (books.isEmpty()) {
                Text(
                    text = "Type and press the search icon to find books",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.align(Alignment.Center),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                BookGrid(books = books)
            }
        }
    }
}
