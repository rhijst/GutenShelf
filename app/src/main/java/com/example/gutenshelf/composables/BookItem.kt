package com.example.gutenshelf.composables

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.android.volley.toolbox.ImageRequest
import com.example.gutenshelf.R
import com.example.gutenshelf.models.Book
import com.example.gutenshelf.navigation.LocalNavigator
import com.example.gutenshelf.cache.ImageCache
import com.example.gutenshelf.network.VolleySingleton

@Composable
fun BookItem(book: Book) {
    key(book.id) {
        val context = LocalContext.current
        val navigator = LocalNavigator.current

        // Only relookup if URL changes
        val cached = remember(book.coverUrl) {
            book.coverUrl?.let { ImageCache.get(it) }
        }

        // Image
        var bitmap by remember { mutableStateOf(cached) }

        LaunchedEffect(book.coverUrl) {
            // URL calidation
            val url = book.coverUrl ?: return@LaunchedEffect

            // If already chached skip
            if (ImageCache.get(url) != null) return@LaunchedEffect

            // Create request for image
            val imageRequest = ImageRequest(
                url,
                { response ->
                    ImageCache.put(url, response) // Cach the image
                    bitmap = response
                },
                0, 0, null, Bitmap.Config.RGB_565,
                { /* error */ }
            )

            // Queue image request
            VolleySingleton.getInstance(context).addToRequestQueue(imageRequest)
        }

        Column(
            modifier = Modifier
                .padding(8.dp)
                .width(120.dp)
                .clickable { (navigator::goToBookDetail)(book.id) }
        ) {
            if (bitmap == null) {
                Image( // Placeholder
                    painter = painterResource(R.drawable.cover),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .height(180.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                )
            } else {
                Image( // Show image
                    bitmap = bitmap!!.asImageBitmap(),
                    contentDescription = book.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .height(180.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                )
            }

            Text(
                text = book.title,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .padding(top = 4.dp)
                    .align(Alignment.CenterHorizontally)
            )
        }
    }
}
