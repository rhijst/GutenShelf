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
fun EditCustomBookScreenLandscape(
    paddingValues: PaddingValues,
    title: String,
    onTitleChange: (String) -> Unit,
    authorsText: String,
    onAuthorsTextChange: (String) -> Unit,
    summariesText: String,
    onSummariesTextChange: (String) -> Unit,
    subjectsText: String,
    onSubjectsTextChange: (String) -> Unit,
    languagesText: String,
    onLanguagesTextChange: (String) -> Unit,
    coverBitmap: Bitmap?,
    onPickImage: () -> Unit,
    onSaveChanges: () -> Unit
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
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedButton(
                onClick = onPickImage,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Change Cover Image")
            }

            OutlinedTextField(
                value = authorsText,
                onValueChange = onAuthorsTextChange,
                label = { Text("Authors (comma separated)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = summariesText,
                onValueChange = onSummariesTextChange,
                label = { Text("Summaries") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = subjectsText,
                onValueChange = onSubjectsTextChange,
                label = { Text("Subjects") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = languagesText,
                onValueChange = onLanguagesTextChange,
                label = { Text("Languages") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = onSaveChanges,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Changes")
            }
        }
    }
}
