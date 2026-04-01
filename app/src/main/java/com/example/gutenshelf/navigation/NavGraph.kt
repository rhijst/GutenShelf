package com.example.gutenshelf.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.gutenshelf.pages.authorPage.AuthorBooksScreen

// Pages
import com.example.gutenshelf.pages.customBooks.CustomBooksScreen
import com.example.gutenshelf.pages.map.MapScreen
import com.example.gutenshelf.pages.home.HomeScreen
import com.example.gutenshelf.pages.shelfs.ShelfsScreen
import com.example.gutenshelf.pages.search.SearchScreen
import com.example.gutenshelf.pages.settings.SettingsScreen
import com.example.gutenshelf.pages.bookDetail.BookDetailScreen
import com.example.gutenshelf.pages.customBooks.AddCustomBookScreen
import com.example.gutenshelf.pages.customBooks.CustomBookDetailScreen

@Composable
fun AppNavGraph(
        navController: NavHostController
    ){
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = AppDestinations.HOME.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(AppDestinations.HOME.route) { HomeScreen() }
            composable(AppDestinations.MAP.route) { MapScreen() }
            composable(AppDestinations.CUSTOM_BOOKS.route) { CustomBooksScreen() }
            composable(AppDestinations.ADD_CUSTOM_BOOK.route) { AddCustomBookScreen() }
            composable(AppDestinations.SHELFS.route) { ShelfsScreen() }
            composable(AppDestinations.SEARCH.route) { SearchScreen() }
            composable(AppDestinations.SETTINGS.route) { SettingsScreen() }

            composable(
                route = AppDestinations.BOOK_DETAIL.route,
                arguments = listOf(navArgument("bookId") { type = NavType.IntType })
            ) { backStackEntry ->
                val bookId = backStackEntry.arguments?.getInt("bookId") ?: 0
                BookDetailScreen(bookId)
            }

            composable(
                route = AppDestinations.AUTHOR_BOOKS.route ,
                arguments = listOf(navArgument("authorName") { type = NavType.StringType })
            ) { backStackEntry ->
                val authorName = backStackEntry.arguments?.getString("authorName") ?: ""
                AuthorBooksScreen(authorName = authorName)
            }

            composable(
                route = AppDestinations.CUSTOM_BOOK_DETAIL.route,
                arguments = listOf(navArgument("bookId") { type = NavType.IntType })
            ) { backStackEntry ->
                val bookId = backStackEntry.arguments?.getInt("bookId") ?: 0
                CustomBookDetailScreen(bookId)
            }

            composable(AppDestinations.CUSTOM_BOOK_EDIT.route) { backStackEntry ->
                val bookId = backStackEntry.arguments?.getString("bookId")?.toIntOrNull()

                if (bookId != null) {
//                    EditCustomBookScreen(bookId = bookId)
                }
            }
        }
    }
}