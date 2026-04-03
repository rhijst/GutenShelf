package com.example.gutenshelf.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.gutenshelf.cache.ShelfDiskCache
import com.example.gutenshelf.models.BookReference
import com.example.gutenshelf.models.BookType

@Composable
fun AddBookToShelfDialog(
    bookId: Int,
    bookType: BookType,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    val context = LocalContext.current
    val shelves = remember { ShelfDiskCache.load(context) }

    // Track selected shelves locally in dialog
    var selectedShelves by remember {
        mutableStateOf(
            shelves.filter { shelf ->
                shelf.bookReferences.any { it.bookId == bookId && it.bookType == bookType }
            }.map { it.name }
        )
    }


    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add to Shelf") },
        text = {
            Column {
                shelves.forEach { shelf ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Checkbox(
                            checked = selectedShelves.contains(shelf.name),
                            onCheckedChange = {
                                selectedShelves = if (selectedShelves.contains(shelf.name)) {
                                    selectedShelves - shelf.name
                                } else {
                                    selectedShelves + shelf.name
                                }
                            }
                        )
                        Text(text = shelf.name)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                // Sync selections with disk
                val updatedShelves = shelves.map { shelf ->
                    val containsBook = shelf.bookReferences.any { it.bookId == bookId && it.bookType == bookType }
                    val shouldContainBook = selectedShelves.contains(shelf.name)

                    val newBookRefs = when {
                        shouldContainBook && !containsBook -> shelf.bookReferences + BookReference(
                            bookId,
                            bookType
                        )
                        !shouldContainBook && containsBook -> shelf.bookReferences.filterNot { it.bookId == bookId && it.bookType == bookType }
                        else -> shelf.bookReferences
                    }

                    shelf.copy(bookReferences = newBookRefs)
                }

                ShelfDiskCache.save(context, updatedShelves)
                onConfirm()
            }) { Text("Add") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}