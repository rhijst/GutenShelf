package com.example.gutenshelf.pages.shelfs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gutenshelf.composables.HeaderSection
import com.example.gutenshelf.models.ShelvesViewModel
import com.example.gutenshelf.navigation.LocalNavigator
import com.example.gutenshelf.R
import com.example.gutenshelf.navigation.AppDestinations

@Composable
fun ShelfsScreen(viewModel: ShelvesViewModel = viewModel()) {
    val snackbarHostState = remember { SnackbarHostState() }
    val navigator = LocalNavigator.current
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.loadShelves(context)
    }

    LaunchedEffect(viewModel.message) {
        viewModel.message?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessage()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(onClick = { navigator.navigate(AppDestinations.ADD_SHELF.route) }) {
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
                item { HeaderSection("Shelf's") }
                items(viewModel.shelves) { shelf ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                            .clickable { navigator.goToShelfDetail(shelf.id) },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = shelf.name,
                            style = MaterialTheme.typography.titleLarge
                        )

                        IconButton(
                            onClick = { navigator.goToEditShelf(shelf.id) }
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.pencil),
                                contentDescription = "Edit",
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    )
}