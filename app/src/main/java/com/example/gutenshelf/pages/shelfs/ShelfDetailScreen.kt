package com.example.gutenshelf.pages.shelfs

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gutenshelf.models.*
import com.example.gutenshelf.pages.home.HomeViewModel
import com.example.gutenshelf.network.BookRepository
import com.example.gutenshelf.composables.BookGrid
import com.example.gutenshelf.navigation.AppDestinations
import com.example.gutenshelf.navigation.LocalNavigator
import com.example.gutenshelf.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShelfDetailScreen(
    shelfId: Int,
    shelvesViewModel: ShelvesViewModel = viewModel(),
    customBooksViewModel: CustomBooksViewModel = viewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val navigator = LocalNavigator.current

    val repository = remember { BookRepository(context) }
    val homeViewModel: HomeViewModel = remember { HomeViewModel(context, repository) }

    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        shelvesViewModel.loadShelves(context)
        customBooksViewModel.loadCustomBooks(context)
    }

    val message = shelvesViewModel.message

    LaunchedEffect(message) {
        message?.let {
            snackbarHostState.showSnackbar(it)
            shelvesViewModel.clearMessage()
        }
    }

    val shelf = shelvesViewModel.shelves.find { it.id == shelfId }

    if (shelf == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Shelf not found")
        }
        return
    }

    val books = shelvesViewModel.resolveBooks(
        shelf.bookReferences,
        homeViewModel.books,
        customBooksViewModel.customBooks
    )

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(shelf.name) },
                actions = {
                    IconButton(onClick = {
                        navigator.goToEditShelf(shelfId)
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.pencil),
                            modifier = Modifier.size(24.dp),
                            contentDescription = "Edit"
                        )
                    }

                    IconButton(onClick = {
                        showDialog = true
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.trash),
                            modifier = Modifier.size(24.dp),
                            contentDescription = "Delete"
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = navigator::goBack) {
                        Icon(
                            painter = painterResource(id = R.drawable.back),
                            contentDescription = "Back",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            if (books.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No books in this shelf yet")
                }
            } else {
                BookGrid(
                    books = books,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }

    // Delete confirmation
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Delete Shelf") },
            text = { Text("Are you sure you want to delete this shelf?") },
            confirmButton = {
                TextButton(onClick = {
                    shelvesViewModel.removeShelf(context, shelfId)
                    showDialog = false
                    navigator.goBack()
                }) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}