package com.example.gutenshelf.cache

import android.content.Context
import android.graphics.BitmapFactory
import com.example.gutenshelf.models.Author
import com.example.gutenshelf.models.Book
import com.example.gutenshelf.models.BookType
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

object BookDiskCache {

    /** Save books to JSON file. Covers are stored separately in covers/custom/ */
    fun save(context: Context, books: List<Book>, file: CacheType) {
        val jsonArray = JSONArray()

        books.forEach { book ->
            val obj = JSONObject().apply {
                put("id", book.id)
                put("type", book.type.name)
                put("title", book.title)
                put("formats", JSONObject(book.formats))
                put("localCoverPath", book.localCoverPath)
                put("authors", JSONArray(book.authors.map { author ->
                    JSONObject().apply {
                        put("name", author.name)
                        put("birth_year", author.birth_year)
                        put("death_year", author.death_year)
                    }
                }))
                put("summaries", JSONArray(book.summaries))
                put("languages", JSONArray(book.languages))
                put("subjects", JSONArray(book.subjects))
                put("download_count", book.download_count)
            }
            jsonArray.put(obj)
        }

        // Save JSON file to app's root filesDir
        val fileObj = File(context.filesDir, file.fileName)
        fileObj.writeText(jsonArray.toString())

        // Ensure custom covers directory exists
        getCustomCoverDir(context)
    }

    /** Load books from JSON file */
    fun load(context: Context, file: CacheType): List<Book> {
        val fileObj = File(context.filesDir, file.fileName)
        if (!fileObj.exists()) return emptyList()

        val text = fileObj.readText()
        val jsonArray = JSONArray(text)
        val books = mutableListOf<Book>()

        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)

            val type = obj.optString("type", "API")

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

            val formats = obj.optJSONObject("formats")?.let { formatsJson ->
                formatsJson.keys().asSequence().associateWith { key ->
                    formatsJson.getString(key)
                }
            } ?: emptyMap()

            val localCoverPath = obj.optString("localCoverPath").takeIf { it.isNotBlank() }

            fun jsonArrayToList(jsonKey: String) = obj.optJSONArray(jsonKey)?.let { arr ->
                List(arr.length()) { k -> arr.getString(k) }
            } ?: emptyList()

            val book = Book(
                id = obj.getInt("id"),
                type = BookType.valueOf(type),
                title = obj.getString("title"),
                authors = authors,
                formats = formats,
                localCoverPath = localCoverPath,
                summaries = jsonArrayToList("summaries"),
                subjects = jsonArrayToList("subjects"),
                languages = jsonArrayToList("languages"),
                download_count = obj.optInt("download_count", 0)
            )

            // Load local cover bitmap if it exists
            book.localCover = localCoverPath?.let { path ->
                val file = File(path)
                if (file.exists()) BitmapFactory.decodeFile(file.absolutePath) else null
            }

            books.add(book)
        }

        return books
    }

    /** Helper: get the custom cover directory */
    fun getCustomCoverDir(context: Context): File {
        val dir = File(context.filesDir, "covers/custom")
        if (!dir.exists()) dir.mkdirs()
        return dir
    }

    /** Helper: get file for a specific book cover */
    fun getCustomCoverFile(context: Context, bookId: Int): File {
        return File(getCustomCoverDir(context), "cover_$bookId.png")
    }
}