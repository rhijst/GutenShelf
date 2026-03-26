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
import com.example.gutenshelf.network.VolleySingleton

@Composable
fun BookItem(book: Book, onClick: (Int) -> Unit = {}) {
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
            .padding(8.dp)
            .width(120.dp)
            .clickable { onClick(book.id) }
    ) {
        if (bitmap != null) {
            Image(
                bitmap = bitmap!!.asImageBitmap(),
                contentDescription = book.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .height(180.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
            )
        } else {
            Image(
                painter = painterResource(R.drawable.cover),
                contentDescription = null,
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
