package com.example.gutenshelf.pages.customBooks

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gutenshelf.models.CustomBooksViewModel
import com.example.gutenshelf.navigation.LocalNavigator
import com.example.gutenshelf.R
import com.example.gutenshelf.cache.ShelfDiskCache
import com.example.gutenshelf.composables.AddBookToShelfDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomBookDetailScreen(
    bookId: Int,
    viewModel: CustomBooksViewModel = viewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val navigator = LocalNavigator.current
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showAddToShelfDialog by remember { mutableStateOf(false) }

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    LaunchedEffect(Unit) {
        viewModel.loadCustomBooks(context)
    }

    LaunchedEffect(viewModel.message) {
        viewModel.message?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearMessage()
        }
    }

    val book = viewModel.customBooks.find { it.id == bookId }

    if (book == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Book not found")
        }
        return
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Book Details") },
                navigationIcon = {
                    IconButton(onClick = { navigator.goBack() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.back),
                            contentDescription = "Back",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        navigator.goToCustomBookEdit(bookId)
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.pencil),
                            modifier = Modifier.size(24.dp),
                            contentDescription = "Edit"
                        )
                    }

                    IconButton(onClick = {
                        showDeleteDialog = true
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.trash),
                            modifier = Modifier.size(24.dp),
                            contentDescription = "Delete"
                        )
                    }

                    IconButton(onClick = { showAddToShelfDialog = true }) {
                        Icon(
                            painter = painterResource(id = R.drawable.books),
                            modifier = Modifier.size(24.dp),
                            contentDescription = "Add to Shelf"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            if (isLandscape) {
                CustomBookDetailLandscape(book = book)
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        if (book.localCover != null) {
                            Image(
                                bitmap = book.localCover!!.asImageBitmap(),
                                contentDescription = book.title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .width(120.dp)
                                    .height(180.dp)
                                    .clip(RoundedCornerShape(8.dp))
                            )
                        } else {
                            Image(
                                painter = painterResource(R.drawable.cover),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .width(120.dp)
                                    .height(180.dp)
                                    .clip(RoundedCornerShape(8.dp))
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column {
                            Text(
                                text = book.title,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Column {
                                book.authors.forEach { author ->
                                    Text(
                                        text = author.name,
                                        style = MaterialTheme.typography.bodyLarge.copy(
                                            color = MaterialTheme.colorScheme.secondary
                                        ),
                                        modifier = Modifier.clickable {
                                            navigator.goToAuthorBooks(author.name)
                                        }
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            if (book.languages.isNotEmpty()) {
                                Text(
                                    text = "Languages: ${book.languages.joinToString()}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    if (book.summaries.isNotEmpty()) {
                        Text(
                            text = "Summary",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        book.summaries.forEach { summary ->
                            Text(
                                text = summary,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }

                    if (book.subjects.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Subjects",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = book.subjects.joinToString("\n"),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Book") },
            text = { Text("Are you sure you want to delete this book?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.removeBook(context, bookId)
                    showDeleteDialog = false
                    navigator.goBack()
                }) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showAddToShelfDialog) {
        AddBookToShelfDialog(
            bookId = book.id,
            bookType = book.type,
            onDismiss = { showAddToShelfDialog = false },
            onConfirm = { showAddToShelfDialog = false }
        )
    }
}
