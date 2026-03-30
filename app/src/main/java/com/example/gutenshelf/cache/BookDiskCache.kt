package com.example.gutenshelf.cache

import android.content.Context
import android.graphics.BitmapFactory
import com.example.gutenshelf.models.Author
import com.example.gutenshelf.models.Book
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

object BookDiskCache {

    fun save(context: Context, books: List<Book>, file: CacheType) {
        val jsonArray = JSONArray()

        books.forEach { book ->
            val obj = JSONObject().apply {
                put("id", book.id)
                put("title", book.title)
                put("formats", JSONObject(book.formats))
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

        val fileObj = File(context.filesDir, file.fileName)
        fileObj.writeText(jsonArray.toString())
    }

    fun load(context: Context, file: CacheType): List<Book> {
        val fileObj = File(context.filesDir, file.fileName)
        if (!fileObj.exists()) return emptyList()

        val text = fileObj.readText()
        val jsonArray = JSONArray(text)
        val books = mutableListOf<Book>()

        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)

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

            fun jsonArrayToList(jsonKey: String) = obj.optJSONArray(jsonKey)?.let { arr ->
                List(arr.length()) { k -> arr.getString(k) }
            } ?: emptyList()

            val book = Book(
                id = obj.getInt("id"),
                title = obj.getString("title"),
                authors = authors,
                formats = formats,
                summaries = jsonArrayToList("summaries"),
                subjects = jsonArrayToList("subjects"),
                languages = jsonArrayToList("languages"),
                download_count = obj.optInt("download_count", 0)
            )

            // Restore local cover if exists
            book.localCover = formats["image/jpeg"]?.let { path ->
                val file = File(path)
                if (file.exists()) BitmapFactory.decodeFile(file.absolutePath) else null
            }

            books.add(book)
        }

        return books
    }
}