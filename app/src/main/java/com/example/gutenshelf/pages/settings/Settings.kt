package com.example.gutenshelf.pages.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.gutenshelf.cache.PreferenceStore
import com.example.gutenshelf.composables.HeaderSection

@Composable
fun SettingsScreen() {
    val context = LocalContext.current
    val preferenceStore = remember { PreferenceStore(context) }
    var isPopularRowEnabled by remember { mutableStateOf(preferenceStore.isPopularRowEnabled()) }

    Column(

        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Show Popular Books Row")
            Switch(
                checked = isPopularRowEnabled,
                onCheckedChange = {
                    isPopularRowEnabled = it
                    preferenceStore.setPopularRowEnabled(it)
                }
            )
        }
    }
}
