package com.example.gutenshelf.pages.shelfs

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gutenshelf.R
import com.example.gutenshelf.models.ShelvesViewModel
import com.example.gutenshelf.navigation.LocalNavigator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditShelfScreen(
    shelfId: Int,
    viewModel: ShelvesViewModel = viewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val navigator = LocalNavigator.current

    LaunchedEffect(Unit) {
        viewModel.loadShelves(context)

        viewModel.message?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessage()
        }
    }

    val shelf = viewModel.shelves.find { it.id == shelfId }

    if (shelf == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Shelf not found")
        }
        return
    }

    var name by remember { mutableStateOf(shelf.name) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Shelf") },
                navigationIcon = {
                    IconButton(onClick = navigator::goBack) {
                        Icon(
                            painter = painterResource(id = R.drawable.back),
                            contentDescription = "Back",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            )
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Shelf Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = {
                        viewModel.updateShelf(context, shelfId, name)
                        navigator.goBack()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Save Changes")
                }
            }
        }
    )
}