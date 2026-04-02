package com.example.gutenshelf.pages.customBooks

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gutenshelf.models.Author
import com.example.gutenshelf.models.CustomBooksViewModel
import com.example.gutenshelf.navigation.LocalNavigator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditCustomBookScreen(bookId: Int, viewModel: CustomBooksViewModel = viewModel()) {
    val context = LocalContext.current
    val navigator = LocalNavigator.current

    LaunchedEffect(Unit) {
        viewModel.loadCustomBooks(context)
    }

    val book = viewModel.customBooks.find { it.id == bookId }

    if (viewModel.isLoading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    if (book == null) {
        Text("Book not found")
        return
    }

    var title by remember { mutableStateOf(book.title) }
    var authorsText by remember { mutableStateOf(book.authors.joinToString { it.name }) }
    var summariesText by remember { mutableStateOf(book.summaries.joinToString()) }
    var subjectsText by remember { mutableStateOf(book.subjects.joinToString()) }
    var languagesText by remember { mutableStateOf(book.languages.joinToString()) }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Book") }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") }
            )

            OutlinedTextField(
                value = authorsText,
                onValueChange = { authorsText = it },
                label = { Text("Authors (comma separated)") }
            )

            OutlinedTextField(
                value = summariesText,
                onValueChange = { summariesText = it },
                label = { Text("Summaries") }
            )

            OutlinedTextField(
                value = subjectsText,
                onValueChange = { subjectsText = it },
                label = { Text("Subjects") }
            )

            OutlinedTextField(
                value = languagesText,
                onValueChange = { languagesText = it },
                label = { Text("Languages") }
            )

            Button(
                onClick = {
                    val authors = authorsText.split(",")
                        .map { Author(it.trim()) }
                        .filter { it.name.isNotBlank() }

                    viewModel.updateBook(
                        context = context,
                        bookId = bookId,
                        title = title,
                        authors = authors,
                        summaries = summariesText.split(",").map { it.trim() },
                        subjects = subjectsText.split(",").map { it.trim() },
                        languages = languagesText.split(",").map { it.trim() }
                    )

                    navigator.goBack()
                }
            ) {
                Text("Save Changes")
            }
        }
    }
}