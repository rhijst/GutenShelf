package com.example.gutenshelf.pages.customBooks

import androidx.compose.foundation.Image
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gutenshelf.models.CustomBooksViewModel
import com.example.gutenshelf.navigation.LocalNavigator
import com.example.gutenshelf.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomBookDetailScreen(bookId: Int, viewModel: CustomBooksViewModel = viewModel()) {
    val context = LocalContext.current
    val navigator = LocalNavigator.current
    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadCustomBooks(context)
    }

    val book = viewModel.customBooks.find { it.id == bookId }

    if (book == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Book not found")
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(book.title) },
                navigationIcon = {
                    IconButton(onClick = { navigator.goBack() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.back),
                            contentDescription = "Back"
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
                        showDialog = true
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.trash),
                            modifier = Modifier.size(24.dp),
                            contentDescription = "Delete"
                        )
                    }
                }
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                book.localCover?.let { bitmap ->
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "Cover",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                }

                Text(
                    "Title: ${book.title}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Text("Authors: ${book.authors.joinToString { it.name }}")

                if (book.summaries.isNotEmpty())
                    Text("Summaries: ${book.summaries.joinToString()}")

                if (book.subjects.isNotEmpty())
                    Text("Subjects: ${book.subjects.joinToString()}")

                if (book.languages.isNotEmpty())
                    Text("Languages: ${book.languages.joinToString()}")
            }
        }
    )

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Delete Book") },
            text = { Text("Are you sure you want to delete this book?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.removeBook(context, bookId)
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