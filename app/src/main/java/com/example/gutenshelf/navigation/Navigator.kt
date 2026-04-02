package com.example.gutenshelf.navigation

import androidx.navigation.NavHostController
import android.net.Uri

interface Navigator {
    fun goToBookDetail(bookId: Int)

    fun goToCustomBookDetail(bookId: Int)
    fun goToCustomBookEdit(bookId: Int)

    fun goToAuthorBooks(authorName: String)

    fun goToShelfDetail(shelfId: Int)

    fun goBack()
    fun navigate(route: String)
}

class NavigatorImpl(
    private val navController: NavHostController
) : Navigator {

    override fun goToBookDetail(bookId: Int) {
        navController.navigate("book_detail/$bookId")
    }

    override fun goToCustomBookDetail(bookId: Int) {
        navController.navigate("custom_book_detail/$bookId")
    }

    override fun goToCustomBookEdit(bookId: Int) {
        navController.navigate("custom_book_edit/$bookId")
    }

    override fun goToAuthorBooks(authorName: String) {
        val encoded = Uri.encode(authorName)
        navController.navigate("author_books/$encoded")
    }


    override fun goToShelfDetail(shelfId: Int){
        navController.navigate("shelf_detail/$shelfId")
    }

    override fun goBack() {
        navController.popBackStack()
    }

    override fun navigate(route: String) {
        navController.navigate(route) {
            popUpTo(navController.graph.startDestinationId) {
                inclusive = false
            }
            launchSingleTop = true
        }
    }
}