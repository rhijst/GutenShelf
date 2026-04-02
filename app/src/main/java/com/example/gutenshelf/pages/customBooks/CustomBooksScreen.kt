package com.example.gutenshelf.pages.customBooks

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gutenshelf.composables.BookGrid
import com.example.gutenshelf.composables.HeaderSection
import com.example.gutenshelf.models.CustomBooksViewModel
import com.example.gutenshelf.navigation.LocalNavigator

@Composable
fun CustomBooksScreen(viewModel: CustomBooksViewModel = viewModel()) {
    val context = LocalContext.current
    val navigator = LocalNavigator.current

    LaunchedEffect(Unit) {
        viewModel.loadCustomBooks(context)
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { navigator.navigate("add_custom_book") }) {
                Text("+")
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                HeaderSection("Your Custom Books")

                if (viewModel.customBooks.isEmpty()) {
                    Text("No custom books yet. Tap + to add one!")
                } else {
                    // Let BookGrid handle its own scrolling
                    BookGrid(
                        books = viewModel.customBooks,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    )
}