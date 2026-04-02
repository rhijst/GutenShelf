package com.example.gutenshelf.models

data class Author(
    val name: String,
    val birth_year: Int? = null,
    val death_year: Int? = null
)
