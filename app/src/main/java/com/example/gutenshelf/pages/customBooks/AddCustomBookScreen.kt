package com.example.gutenshelf.pages.customBooks

import android.content.res.Configuration
import android.graphics.Bitmap
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gutenshelf.R
import com.example.gutenshelf.models.Author
import com.example.gutenshelf.models.CustomBooksViewModel
import com.example.gutenshelf.navigation.AppDestinations
import com.example.gutenshelf.navigation.LocalNavigator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCustomBookScreen(viewModel: CustomBooksViewModel = viewModel()) {
    val context = LocalContext.current
    val navigator = LocalNavigator.current
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

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

    val onAddBook = {
        val authors = authorsInput.split(",")
            .map { Author(it.trim()) }
            .filter { it.name.isNotEmpty() }
        val summaries = summariesInput.split(",")
            .map { it.trim() }.filter { it.isNotEmpty() }
        val subjects = subjectsInput.split(",")
            .map { it.trim() }.filter { it.isNotEmpty() }
        val languages = languagesInput.split(",")
            .map { it.trim() }.filter { it.isNotEmpty() }

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
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Custom Book") },
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
        content = { paddingValues ->
            if (isLandscape) {
                AddCustomBookScreenLandscape(
                    paddingValues = paddingValues,
                    title = title,
                    onTitleChange = { title = it },
                    authorsInput = authorsInput,
                    onAuthorsInputChange = { authorsInput = it },
                    summariesInput = summariesInput,
                    onSummariesInputChange = { summariesInput = it },
                    subjectsInput = subjectsInput,
                    onSubjectsInputChange = { subjectsInput = it },
                    languagesInput = languagesInput,
                    onLanguagesInputChange = { languagesInput = it },
                    coverBitmap = coverBitmap,
                    onPickImage = { launcher.launch("image/*") },
                    onAddBook = onAddBook
                )
            } else {
                AddCustomBookPortrait(
                    paddingValues = paddingValues,
                    title = title,
                    onTitleChange = { title = it },
                    authorsInput = authorsInput,
                    onAuthorsInputChange = { authorsInput = it },
                    summariesInput = summariesInput,
                    onSummariesInputChange = { summariesInput = it },
                    subjectsInput = subjectsInput,
                    onSubjectsInputChange = { subjectsInput = it },
                    languagesInput = languagesInput,
                    onLanguagesInputChange = { languagesInput = it },
                    coverBitmap = coverBitmap,
                    onPickImage = { launcher.launch("image/*") },
                    onAddBook = onAddBook
                )
            }
        }
    )
}

@Composable
fun AddCustomBookPortrait(
    paddingValues: PaddingValues,
    title: String,
    onTitleChange: (String) -> Unit,
    authorsInput: String,
    onAuthorsInputChange: (String) -> Unit,
    summariesInput: String,
    onSummariesInputChange: (String) -> Unit,
    subjectsInput: String,
    onSubjectsInputChange: (String) -> Unit,
    languagesInput: String,
    onLanguagesInputChange: (String) -> Unit,
    coverBitmap: Bitmap?,
    onPickImage: () -> Unit,
    onAddBook: () -> Unit
) {
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
            onValueChange = onTitleChange,
            label = { Text("Book Title") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = authorsInput,
            onValueChange = onAuthorsInputChange,
            label = { Text("Authors (comma-separated)") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = summariesInput,
            onValueChange = onSummariesInputChange,
            label = { Text("Summaries (comma-separated)") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = subjectsInput,
            onValueChange = onSubjectsInputChange,
            label = { Text("Subjects (comma-separated)") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = languagesInput,
            onValueChange = onLanguagesInputChange,
            label = { Text("Languages (comma-separated)") },
            modifier = Modifier.fillMaxWidth()
        )

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            coverBitmap?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = "Cover Preview",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth(0.4f)
                        .aspectRatio(0.67f)
                )
            }
        }

        Button(
            onClick = onPickImage,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Pick Cover Image")
        }

        Button(
            onClick = onAddBook,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add Book")
        }
    }
}
