package com.example.gutenshelf.pages.customBooks

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp

@Composable
fun AddCustomBookScreenLandscape(
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
                        .fillMaxWidth(0.4f)
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
                onValueChange = onTitleChange,
                label = { Text("Book Title") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedButton(
                onClick = onPickImage,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Change Cover Image")
            }

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

            Button(
                onClick = onAddBook,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Add Book")
            }
        }
    }
}
