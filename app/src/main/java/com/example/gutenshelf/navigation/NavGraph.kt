package com.example.gutenshelf.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

// Pages
import com.example.gutenshelf.pages.customBooks.CustomBooksScreen
import com.example.gutenshelf.pages.favorite.FavoriteScreen
import com.example.gutenshelf.pages.home.HomeScreen
import com.example.gutenshelf.pages.shelfs.ShelfsScreen
import com.example.gutenshelf.pages.search.SearchScreen
import com.example.gutenshelf.pages.settings.SettingsScreen

@Composable
fun AppNavGraph(navController: NavHostController) {
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