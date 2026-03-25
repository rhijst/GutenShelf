package com.example.gutenshelf

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import com.example.gutenshelf.ui.theme.GutenShelfTheme

// Navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

// Pages
import com.example.gutenshelf.customBooks.CustomBooksScreen
import com.example.gutenshelf.favorite.FavoriteScreen
import com.example.gutenshelf.home.HomeScreen
import com.example.gutenshelf.shelfs.ShelfsScreen
import com.example.gutenshelf.search.SearchScreen
import com.example.gutenshelf.settings.SettingsScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GutenShelfTheme {
                GutenShelfApp()
            }
        }
    }
}

@PreviewScreenSizes
@Composable
fun GutenShelfApp() {
    val navController = rememberNavController()
    val currentRoute = navController.currentBackStackEntry?.destination?.route

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            AppDestinations.entries.forEach {
                item(
                    icon = {
                        Icon(
                            painter = painterResource(it.icon),
                            contentDescription = it.label,
                            modifier = Modifier.size(24.dp),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    },
                    label = { Text(it.label) },
                    selected = currentRoute == it.route,
                    onClick = {
                        navController.navigate(it.route) {
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    }
                )
            }
        }
    ) {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

            NavHost(
                navController = navController,
                startDestination = AppDestinations.HOME.route,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(AppDestinations.HOME.route) { HomeScreen() }
                composable(AppDestinations.FAVORITE.route) { FavoriteScreen() }
                composable(AppDestinations.BOOKS.route) { CustomBooksScreen() }
                composable(AppDestinations.SHELFS.route) { ShelfsScreen() }
                composable(AppDestinations.SEARCH.route) { SearchScreen() }
                composable(AppDestinations.SETTINGS.route) { SettingsScreen() }
            }
        }
    }
}

enum class AppDestinations(
    val label: String,
    val icon: Int,
    val route: String
) {
    HOME("Home", R.drawable.home, "home"),
    FAVORITE("Favorite", R.drawable.heart, "favorite"),
    SHELFS("Shelf's", R.drawable.books, "Shelfs"),
    BOOKS("Books", R.drawable.book, "custom_books"),
    SEARCH("Search", R.drawable.search, "search"),
    SETTINGS("Settings", R.drawable.gears, "settings"),
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    GutenShelfTheme {
        Greeting("Android")
    }
}