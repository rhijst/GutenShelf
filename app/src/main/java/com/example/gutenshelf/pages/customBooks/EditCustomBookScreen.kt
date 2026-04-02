package com.example.gutenshelf.pages.customBooks

import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gutenshelf.R
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
    var coverBitmap by remember { mutableStateOf(book.localCover) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            context.contentResolver.openInputStream(it)?.use { stream ->
                coverBitmap = BitmapFactory.decodeStream(stream)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Book") },
                navigationIcon = {
                    IconButton(onClick = { navigator.goBack() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.back),
                            contentDescription = "Back",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            )
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                // Show cover preview
                coverBitmap?.let {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = "Cover Preview",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    )
                }

                Button(onClick = { launcher.launch("image/*") }) {
                    Text("Change Cover Image")
                }

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = authorsText,
                    onValueChange = { authorsText = it },
                    label = { Text("Authors (comma separated)") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = summariesText,
                    onValueChange = { summariesText = it },
                    label = { Text("Summaries") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = subjectsText,
                    onValueChange = { subjectsText = it },
                    label = { Text("Subjects") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = languagesText,
                    onValueChange = { languagesText = it },
                    label = { Text("Languages") },
                    modifier = Modifier.fillMaxWidth()
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
                            localCover = coverBitmap,
                            summaries = summariesText.split(",").map { it.trim() },
                            subjects = subjectsText.split(",").map { it.trim() },
                            languages = languagesText.split(",").map { it.trim() }
                        )

                        navigator.goBack()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Save Changes")
                }
            }
        }
    )
}
