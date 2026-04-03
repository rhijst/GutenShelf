package com.example.gutenshelf.pages.shelfs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gutenshelf.models.ShelvesViewModel
import com.example.gutenshelf.navigation.AppDestinations
import com.example.gutenshelf.navigation.LocalNavigator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddShelfScreen(viewModel: ShelvesViewModel = viewModel()) {
    val context = LocalContext.current
    val navigator = LocalNavigator.current
    var name by remember { mutableStateOf("") }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Add Shelf") }) },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Shelf Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = {
                        viewModel.addShelf(context, name)
                        navigator.goBack()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Create Shelf")
                }
            }
        }
    )
}