package com.example.gutenshelf.pages.bookDetail

import android.content.res.Configuration
import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.IconButton
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
import com.android.volley.toolbox.ImageRequest
import com.example.gutenshelf.R
import com.example.gutenshelf.composables.AddBookToShelfDialog
import com.example.gutenshelf.models.Book
import com.example.gutenshelf.navigation.LocalNavigator
import com.example.gutenshelf.network.BookRepository
import com.example.gutenshelf.network.VolleySingleton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailScreen(bookId: Int) {
    val context = LocalContext.current
    val navigator = LocalNavigator.current
    val repository = remember { BookRepository(context) }
    var book by remember { mutableStateOf<Book?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    var showAddToShelfDialog by remember { mutableStateOf(false) }

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    LaunchedEffect(bookId) {
        repository.fetchBookById(
            id = bookId,
            onSuccess = { fetchedBook ->
                book = fetchedBook
                isLoading = false

                // Fetch cover image if available
                fetchedBook.coverUrl?.let { url ->
                    val imageRequest = ImageRequest(
                        url,
                        { response -> bitmap = response },
                        0, 0, null, Bitmap.Config.RGB_565,
                        { /* Handle error */ }
                    )
                    VolleySingleton.getInstance(context).addToRequestQueue(imageRequest)
                }
            },
            onError = { error ->
                errorMessage = error
                isLoading = false
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Book Details") },
                navigationIcon = {
                    IconButton(onClick = navigator::goBack) {
                        Icon(
                            painter = painterResource(id = R.drawable.back),
                            contentDescription = "Back",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                },
                actions = {
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
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (errorMessage != null) {
                Text(text = "Error: $errorMessage", modifier = Modifier.align(Alignment.Center))
            } else if (book != null) {
                val currentBook = book!!

                if (isLandscape) {
                    BookDetailLandscape(book = currentBook, bitmap = bitmap)
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp)
                    ) {
                        Row(modifier = Modifier.fillMaxWidth()) {
                            if (bitmap != null) {
                                Image(
                                    bitmap = bitmap!!.asImageBitmap(),
                                    contentDescription = currentBook.title,
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
                                    text = currentBook.title,
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                // List of clickable authors
                                Column {
                                    currentBook.authors.forEach { author ->
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

                                Text(
                                    text = "Downloads: ${currentBook.download_count}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = "Languages: ${currentBook.languages.joinToString()}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        if (currentBook.summaries.isNotEmpty()) {
                            Text(
                                text = "Summary",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            currentBook.summaries.forEach { summary ->
                                Text(
                                    text = summary,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }

                        if (currentBook.subjects.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Subjects",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = currentBook.subjects.joinToString("\n"),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
                if (showAddToShelfDialog) {
                    AddBookToShelfDialog(
                        bookId = book!!.id,
                        bookType = book!!.type,
                        onDismiss = { showAddToShelfDialog = false },
                        onConfirm = { showAddToShelfDialog = false }
                    )
                }
            }
        }
    }
}
