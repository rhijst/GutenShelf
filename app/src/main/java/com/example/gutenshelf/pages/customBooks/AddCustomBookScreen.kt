package com.example.gutenshelf.pages.customBooks

import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.layout.ContentScale
import android.graphics.BitmapFactory
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.graphics.asImageBitmap
import com.example.gutenshelf.models.Author
import com.example.gutenshelf.models.CustomBooksViewModel
import com.example.gutenshelf.navigation.LocalNavigator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCustomBookScreen() {
    val context = LocalContext.current
    val navigator = LocalNavigator.current
    val viewModel: CustomBooksViewModel = androidx.lifecycle.viewmodel.compose.viewModel()

    var title by remember { mutableStateOf("") }
    var authorsInput by remember { mutableStateOf("") }
    var summariesInput by remember { mutableStateOf("") }
    var subjectsInput by remember { mutableStateOf("") }
    var languagesInput by remember { mutableStateOf("") }
    var coverBitmap by remember { mutableStateOf<Bitmap?>(null) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            context.contentResolver.openInputStream(it)?.use { stream ->
                coverBitmap = BitmapFactory.decodeStream(stream)
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.loadCustomBooks(context)
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Add Custom Book") }) },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Book Title") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = authorsInput,
                    onValueChange = { authorsInput = it },
                    label = { Text("Authors (comma-separated)") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = summariesInput,
                    onValueChange = { summariesInput = it },
                    label = { Text("Summaries (comma-separated)") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = subjectsInput,
                    onValueChange = { subjectsInput = it },
                    label = { Text("Subjects (comma-separated)") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = languagesInput,
                    onValueChange = { languagesInput = it },
                    label = { Text("Languages (comma-separated)") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Show preview image
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
                    Text("Pick Cover Image")
                }

                Button(
                    onClick = {
                        val authors = authorsInput.split(",").map { Author(it.trim()) }.filter { it.name.isNotEmpty() }
                        val summaries = summariesInput.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                        val subjects = subjectsInput.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                        val languages = languagesInput.split(",").map { it.trim() }.filter { it.isNotEmpty() }

                        viewModel.addBook(
                            context = context,
                            title = title,
                            authors = authors,
                            localCover = coverBitmap,
                            summaries = summaries,
                            subjects = subjects,
                            languages = languages
                        )

                        navigator.goBack()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Add Book")
                }

                viewModel.message?.let {
                    Text(it, color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    )


}