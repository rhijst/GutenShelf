package com.example.gutenshelf.navigation

import androidx.navigation.NavHostController
import android.net.Uri

interface Navigator {
    fun goToBookDetail(bookId: Int)
    fun goToAuthorBooks(authorName: String)
    fun goBack()
    fun navigateTo(route: String)
}

class NavigatorImpl(
    private val navController: NavHostController
) : Navigator {

    override fun goToBookDetail(bookId: Int) {
        navController.navigate("book_detail/$bookId")
    }

    override fun goToAuthorBooks(authorName: String) {
        val encoded = Uri.encode(authorName)
        navController.navigate("author_books/$encoded")
    }

    override fun goBack() {
        navController.popBackStack()
    }

    override fun navigateTo(route: String) {
        navController.navigate(route) {
            popUpTo(navController.graph.startDestinationId) {
                inclusive = false
            }
            launchSingleTop = true
        }
    }
}