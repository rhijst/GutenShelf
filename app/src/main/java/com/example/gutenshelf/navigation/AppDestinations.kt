package com.example.gutenshelf.navigation
import com.example.gutenshelf.R

enum class AppDestinations(
    val label: String,
    val icon: Int,
    val route: String
) {
    HOME("Home", R.drawable.home, "home"),
    FAVORITE("Favorite", R.drawable.heart, "favorite"),
    SHELFS("Shelf's", R.drawable.books, "shelfs"),
    BOOKS("Books", R.drawable.book, "custom_books"),
    SEARCH("Search", R.drawable.search, "search"),
    SETTINGS("Settings", R.drawable.gears, "settings"),
    BOOK_DETAIL("Book Detail", R.drawable.book, "book_detail/{bookId}")
}