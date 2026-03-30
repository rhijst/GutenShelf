package com.example.gutenshelf.cache

import android.content.Context
import com.example.gutenshelf.models.Author
import com.example.gutenshelf.models.Book
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

object BookDiskCache {

    private const val FILE_NAME = "books_cache.json"

    fun save(context: Context, books: List<Book>) {
        val jsonArray = JSONArray()

        books.forEach { book ->
            val obj = JSONObject().apply {
                put("id", book.id)
                put("title", book.title)
                put("coverUrl", book.coverUrl)
                put("authors", book.authors)
                put("summaries", book.summaries)
                put("languages", book.languages)
                put("subjects", book.subjects)
                put("download_count", book.download_count)
            }
            jsonArray.put(obj)
        }

        val file = File(context.filesDir, FILE_NAME)
        file.writeText(jsonArray.toString())
    }

    fun load(context: Context): List<Book> {
        val file = File(context.filesDir, FILE_NAME)
        if (!file.exists()) return emptyList()

        val text = file.readText()
        val jsonArray = JSONArray(text)

        val books = mutableListOf<Book>()

        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)

            // Parse authors safely
            val authors = obj.optJSONArray("authors")?.let { authorsJson ->
                List(authorsJson.length()) { j ->
                    authorsJson.getJSONObject(j).let { authorObj ->
                        Author(
                            name = authorObj.getString("name"),
                            birth_year = authorObj.optInt("birth_year").takeIf { it != 0 },
                            death_year = authorObj.optInt("death_year").takeIf { it != 0 }
                        )
                    }
                }
            } ?: emptyList()

            // Parse formats safely
            val formats = obj.optJSONObject("formats")?.let { formatsJson ->
                formatsJson.keys().asSequence().associateWith { key ->
                    formatsJson.getString(key)
                }
            } ?: emptyMap()

            // Parse lists safely
            fun jsonArrayToList(jsonKey: String) = obj.optJSONArray(jsonKey)?.let { arr ->
                List(arr.length()) { k -> arr.getString(k) }
            } ?: emptyList()

            books.add(
                Book(
                    id = obj.getInt("id"),
                    title = obj.getString("title"),
                    authors = authors,
                    formats = formats,
                    summaries = jsonArrayToList("summaries"),
                    subjects = jsonArrayToList("subjects"),
                    languages = jsonArrayToList("languages"),
                    download_count = obj.optInt("download_count", 0)
                )
            )
        }

        return books
    }
}