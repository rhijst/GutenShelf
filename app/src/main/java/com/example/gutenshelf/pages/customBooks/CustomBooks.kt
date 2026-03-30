package com.example.gutenshelf.pages.customBooks

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.gutenshelf.models.CustomBooksViewModel
import com.example.gutenshelf.models.Book
import com.example.gutenshelf.navigation.LocalNavigator

@Composable
fun CustomBooksScreen() {
    val context = LocalContext.current
    val navigator = LocalNavigator.current
    val viewModel = remember { CustomBooksViewModel(context) }


    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navigator.navigate("add_custom_book") }
            ) {
                Text("+")
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Your Custom Books", style = MaterialTheme.typography.titleLarge)

                if (viewModel.customBooks.isEmpty()) {
                    Text("No custom books yet. Tap + to add one!")
                }

                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(viewModel.customBooks) { book ->
                        Text(
                            text = "${book.title} by ${book.authors.joinToString { it.name }}",
                            modifier = Modifier.clickable {
                                navigator.goToCustomBookDetail(book.id)
                            },
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.bodyLarge.copy(textDecoration = TextDecoration.Underline)
                        )
                    }
                }
            }
        }
    )
}