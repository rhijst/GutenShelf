package com.example.gutenshelf.pages.authorPage

//package com.example.gutenshelf.pages.author

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.android.volley.toolbox.ImageRequest
import com.example.gutenshelf.models.Book
import com.example.gutenshelf.network.BookRepository
import com.example.gutenshelf.network.VolleySingleton
import com.example.gutenshelf.R

@Composable
fun AuthorBooksScreen(authorName: String, onBackClick: () -> Boolean) {
    val context = LocalContext.current
    val repository = remember { BookRepository(context) }

    var books by remember { mutableStateOf<List<Book>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(authorName) {
        repository.fetchBooksByAuthor(authorName,
            onSuccess = { fetchedBooks ->
                books = fetchedBooks
                isLoading = false
            },
            onError = { error ->
                errorMessage = error
                isLoading = false
            }
        )
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    } else if (errorMessage != null) {
        Box(modifier = Modifier.fillMaxSize()) {
            Text(text = "Error: $errorMessage", modifier = Modifier.align(Alignment.Center))
        }
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(8.dp),
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(books) { book ->
                AuthorBookItem(book)
            }
        }
    }
}

@Composable
fun AuthorBookItem(book: Book) {
    val context = LocalContext.current
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(book.coverUrl) {
        book.coverUrl?.let { url ->
            val imageRequest = ImageRequest(
                url,
                { response -> bitmap = response },
                0, 0, null, Bitmap.Config.RGB_565,
                { /* Handle error */ }
            )
            VolleySingleton.getInstance(context).addToRequestQueue(imageRequest)
        }
    }

    Column(
        modifier = Modifier
            .width(100.dp) // adjust width as needed
    ) {
        if (bitmap != null) {
            Image(
                bitmap = bitmap!!.asImageBitmap(),
                contentDescription = book.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .height(150.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
            )
        } else {
            Image(
                painter = painterResource(R.drawable.cover),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .height(150.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
            )
        }

        Text(
            text = book.title,
            maxLines = 2,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}