package com.example.gutenshelf.cache

import android.content.Context
import com.example.gutenshelf.models.Shelf
import com.example.gutenshelf.models.BookReference
import com.example.gutenshelf.models.BookType
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

object ShelfDiskCache {

    private const val FILE_NAME = "shelves_cache.json"

    fun save(context: Context, shelves: List<Shelf>) {
        val jsonArray = JSONArray()
        shelves.forEach { shelf ->
            val obj = JSONObject().apply {
                put("id", shelf.id)
                put("name", shelf.name)
                put("bookReferences", JSONArray(shelf.bookReferences.map { ref ->
                    JSONObject().apply {
                        put("bookId", ref.bookId)
                        put("bookType", ref.bookType.name)
                    }
                }))
            }
            jsonArray.put(obj)
        }

        File(context.filesDir, FILE_NAME).writeText(jsonArray.toString())
    }

    fun load(context: Context): List<Shelf> {
        val file = File(context.filesDir, FILE_NAME)
        if (!file.exists()) return emptyList()

        val jsonArray = JSONArray(file.readText())
        val shelves = mutableListOf<Shelf>()
        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)
            val bookRefsJson = obj.optJSONArray("bookReferences") ?: JSONArray()
            val bookRefs = List(bookRefsJson.length()) { j ->
                bookRefsJson.getJSONObject(j).let { refObj ->
                    BookReference(
                        bookId = refObj.getInt("bookId"),
                        bookType = BookType.valueOf(refObj.getString("bookType"))
                    )
                }
            }

            shelves.add(Shelf(
                id = obj.getInt("id"),
                name = obj.getString("name"),
                bookReferences = bookRefs
            ))
        }

        return shelves
    }
}