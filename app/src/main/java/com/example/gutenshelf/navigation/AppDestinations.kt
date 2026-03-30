package com.example.gutenshelf.navigation
import com.example.gutenshelf.R

enum class AppDestinations(val route: String, ) {
    HOME("home"),
    FAVORITE("favorite"),
    SHELFS("shelfs"),
    CUSTOM_BOOKS("custom_books"),
    ADD_CUSTOM_BOOK("add_custom_book"),

    CUSTOM_BOOK_DETAIL("custom_book_detail/{bookId}"),

    SEARCH("search"),
    SETTINGS("settings" ),
    BOOK_DETAIL("book_detail/{bookId}"),
    AUTHOR_BOOKS("author_books/{authorName}")
}

data class NavBarDestination(
    val route: String,
    val icon: Int,
    val label: String
)

// Only the screens that appear in the nav bar
val NavBarDestinations = listOf(
    NavBarDestination(AppDestinations.HOME.route, R.drawable.home, "Home"),
    NavBarDestination(AppDestinations.FAVORITE.route, R.drawable.heart, "Favorite"),
    NavBarDestination(AppDestinations.SHELFS.route, R.drawable.books, "Shelf's"),
    NavBarDestination(AppDestinations.CUSTOM_BOOKS.route, R.drawable.book, "Books"),
    NavBarDestination(AppDestinations.SEARCH.route, R.drawable.search, "Search"),
    NavBarDestination(AppDestinations.SETTINGS.route, R.drawable.gears, "Settings")
)