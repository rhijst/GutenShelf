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
import com.example.gutenshelf.pages.favorite.FavoriteScreen
import com.example.gutenshelf.pages.home.HomeScreen
import com.example.gutenshelf.pages.shelfs.ShelfsScreen
import com.example.gutenshelf.pages.search.SearchScreen
import com.example.gutenshelf.pages.settings.SettingsScreen
import com.example.gutenshelf.pages.bookDetail.BookDetailScreen

@Composable
fun AppNavGraph(navController: NavHostController) {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = AppDestinations.HOME.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(AppDestinations.HOME.route) {
                HomeScreen(onBookClick = { bookId ->
                    navController.navigate("book_detail/$bookId")
                })
            }
            composable(AppDestinations.FAVORITE.route) { FavoriteScreen() }
            composable(AppDestinations.BOOKS.route) { CustomBooksScreen() }
            composable(AppDestinations.SHELFS.route) { ShelfsScreen() }
            composable(AppDestinations.SEARCH.route) { SearchScreen() }
            composable(AppDestinations.SETTINGS.route) { SettingsScreen() }

            composable(
                route = AppDestinations.BOOK_DETAIL.route,
                arguments = listOf(navArgument("bookId") { type = NavType.IntType })
            ) { backStackEntry ->
                val bookId = backStackEntry.arguments?.getInt("bookId") ?: 0
                BookDetailScreen(
                    bookId = bookId,
                    onBackClick = { navController.popBackStack() },
                    onAuthorClick = { authorName ->
                        navController.navigate("author_books/$authorName")
                    }
                )
            }

            composable(
                route = AppDestinations.AUTHOR_BOOKS.route ,
                arguments = listOf(navArgument("authorName") { type = NavType.StringType })
            ) { backStackEntry ->
                val authorName = backStackEntry.arguments?.getString("authorName") ?: ""
                AuthorBooksScreen(
                    authorName = authorName,
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}