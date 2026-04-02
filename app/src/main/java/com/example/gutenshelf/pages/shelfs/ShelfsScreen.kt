package com.example.gutenshelf.pages.shelfs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gutenshelf.models.ShelvesViewModel
import com.example.gutenshelf.navigation.LocalNavigator

@Composable
fun ShelfsScreen(viewModel: ShelvesViewModel = viewModel()) {
    val navigator = LocalNavigator.current
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.loadShelves(context)
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { navigator.navigate("add_shelf") }) {
                Text("+")
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
        content = { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                items(viewModel.shelves) { shelf ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .clickable { navigator.goToShelfDetail(shelf.id) },
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(shelf.name, style = MaterialTheme.typography.titleMedium)
//                        IconButton(onClick = { navigator.goToEditShelf(shelf.id) }) {
//                            Icon(Icons.Default.Edit, contentDescription = "Edit Shelf")
//                        }
                    }
                }
            }
        }
    )
}