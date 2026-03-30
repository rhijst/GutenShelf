package com.example.gutenshelf.pages.customBooks

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
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
import com.example.gutenshelf.models.CustomBooksViewModel
import com.example.gutenshelf.navigation.LocalNavigator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomBookDetailScreen(bookId: Int) {
    val context = LocalContext.current
    val viewModel = remember { CustomBooksViewModel(context) }
    val navigator = LocalNavigator.current

    val book by remember { derivedStateOf { viewModel.customBooks.find { it.id == bookId } } }

    if (book == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Book not found")
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(book!!.title) },
                navigationIcon = {
                    IconButton(onClick = { navigator.goBack() }) {
                        Icon(
                            painter = painterResource(id = com.example.gutenshelf.R.drawable.back),
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()) // Scrollable
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Show cover
                book!!.localCover?.let { bitmap ->
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
                    "Title: ${book!!.title}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Text("Authors: ${book!!.authors.joinToString { it.name }}")

                if (book!!.summaries.isNotEmpty())
                    Text("Summaries: ${book!!.summaries.joinToString()}")

                if (book!!.subjects.isNotEmpty())
                    Text("Subjects: ${book!!.subjects.joinToString()}")

                if (book!!.languages.isNotEmpty())
                    Text("Languages: ${book!!.languages.joinToString()}")
            }
        }
    )
}