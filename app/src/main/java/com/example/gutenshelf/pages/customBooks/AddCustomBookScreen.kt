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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Custom Book") },
                navigationIcon = {
                    IconButton(onClick = { navigator.navigate(AppDestinations.CUSTOM_BOOKS.route) }) {
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
                // Landscape: split layout
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Left: Cover
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Top
                    ) {
                        coverBitmap?.let {
                            Image(
                                bitmap = it.asImageBitmap(),
                                contentDescription = "Cover Preview",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxWidth( 0.4f )
                                    .aspectRatio(0.67f)
                            )
                        }
                    }

                    // Right: scrollable Column
                    Column(
                        modifier = Modifier
                            .weight(2f)
                            .fillMaxHeight()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text("Book Title") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedButton(
                            onClick = { launcher.launch("image/*") },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Change Cover Image")
                        }

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

                        Button(
                            onClick = {
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

                                navigator.navigate(AppDestinations.CUSTOM_BOOKS.route)
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Add Book")
                        }
                    }
                }
            } else {
                // Portrait: scrollable Column
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
                        onClick = { launcher.launch("image/*") },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Pick Cover Image")
                    }

                    Button(
                        onClick = {
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

                            navigator.navigate(AppDestinations.CUSTOM_BOOKS.route)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Add Book")
                    }
                }
            }
        }
    )
}