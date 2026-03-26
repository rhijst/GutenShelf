package com.example.gutenshelf.pages.home

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import com.example.gutenshelf.composables.HeaderSection
import com.example.gutenshelf.composables.BookRow

@Composable
fun HomeScreen() {
    LazyColumn {
        item {
            HeaderSection("Featured books & shelf's")
        }

        item {
            BookRow(title = "Popular")
        }

        item {
            BookRow(title = "Recommended")
        }

        item {
            BookRow(title = "New Releases")
        }
    }
}