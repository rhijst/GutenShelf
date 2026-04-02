package com.example.gutenshelf.pages.bookDetail

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.gutenshelf.R
import com.example.gutenshelf.models.Book
import com.example.gutenshelf.navigation.LocalNavigator

@Composable
fun BookDetailLandscape(book: Book, bitmap: Bitmap?) {
    val navigator = LocalNavigator.current
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Left side: Image (Appropriate size)
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        ) {
            if (bitmap != null) {
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = book.title,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(8.dp))
                )
            } else {
                Image(
                    painter = painterResource(R.drawable.cover),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(8.dp))
                )
            }
        }

        Spacer(modifier = Modifier.width(24.dp))

        // Right side: Scrollable Text Content
        Column(
            modifier = Modifier
                .weight(1.5f)
                .fillMaxHeight()
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = book.title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            // List of clickable authors
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

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Downloads: ${book.download_count}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Languages: ${book.languages.joinToString()}",
                style = MaterialTheme.typography.bodyMedium
            )

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
