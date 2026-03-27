package com.example.gutenshelf.network

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.example.gutenshelf.models.Author
import com.example.gutenshelf.models.Book
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.Executors

class BookRepository(private val context: Context) {

    private val baseUrl = "https://gutendex.com/books/"
    private val TAG = "BookRepository"
    private val executor = Executors.newSingleThreadExecutor()

    fun fetchBooks(onSuccess: (List<Book>) -> Unit, onError: (String) -> Unit) {
        Log.d(TAG, "Fetching books from $baseUrl")
        
        val request = object : JsonObjectRequest(
            Request.Method.GET, baseUrl, null,
            { response ->
                // Perform heavy parsing on a background thread
                executor.execute {
                    try {
                        val books = parseBooks(response)
                        Log.d(TAG, "Successfully parsed ${books.size} books on background thread")
                        
                        // Callback to main thread for UI updates
                        Handler(Looper.getMainLooper()).post {
                            onSuccess(books)
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Parsing error", e)
                        Handler(Looper.getMainLooper()).post {
                            onError("Data processing error")
                        }
                    }
                }
            },
            { error ->
                val message = getVolleyErrorMessage(error)
                Log.e(TAG, "Volley error: $message")
                onError(message)
            }
        ) {
        }

        // Increase timeout to 20 seconds and allow 1 retry
        request.retryPolicy = DefaultRetryPolicy(
            20000,
            1,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )

        VolleySingleton.getInstance(context).addToRequestQueue(request)
    }

    fun fetchBookById(id: Int, onSuccess: (Book) -> Unit, onError: (String) -> Unit) {
        val url = "$baseUrl$id/"
        Log.d(TAG, "Fetching book from $url")

        val request = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                executor.execute {
                    try {
                        val book = parseBookObject(response)
                        Handler(Looper.getMainLooper()).post {
                            onSuccess(book)
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Parsing error", e)
                        Handler(Looper.getMainLooper()).post {
                            onError("Data processing error")
                        }
                    }
                }
            },
            { error ->
                val message = getVolleyErrorMessage(error)
                Log.e(TAG, "Volley error: $message")
                onError(message)
            }
        )

        request.retryPolicy = DefaultRetryPolicy(
            20000,
            1,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )

        VolleySingleton.getInstance(context).addToRequestQueue(request)
    }

    fun fetchBooksByAuthor(authorName: String, onSuccess: (List<Book>) -> Unit, onError: (String) -> Unit) {
        val url = "https://gutendex.com/books?search=${authorName.replace(" ", "%20")}"

        val request = object : JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                // Perform heavy parsing on a background thread
                executor.execute {
                    try {
                        val books = parseBooks(response)
                        Log.d(TAG, "Successfully parsed ${books.size} books on background thread")

                        // Callback to main thread for UI updates
                        Handler(Looper.getMainLooper()).post {
                            onSuccess(books)
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Parsing error", e)
                        Handler(Looper.getMainLooper()).post {
                            onError("Data processing error")
                        }
                    }
                }
            },
            { error ->
                val message = getVolleyErrorMessage(error)
                Log.e(TAG, "Volley error: $message")
                onError(message)
            }
        ) {
        }

        // Increase timeout to 20 seconds and allow 1 retry
        request.retryPolicy = DefaultRetryPolicy(
            20000,
            1,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )

        VolleySingleton.getInstance(context).addToRequestQueue(request)

    }

    private fun getVolleyErrorMessage(error: VolleyError): String {
        return when {
            error is com.android.volley.TimeoutError -> "Connection timed out. The server is taking too long."
            error is com.android.volley.NoConnectionError -> "No internet connection."
            error is com.android.volley.NetworkError -> "Network error. Please check your connection."
            error.networkResponse != null -> "Server error: ${error.networkResponse.statusCode}"
            else -> "Unexpected error: ${error.localizedMessage ?: "Check logs"}"
        }
    }

    private fun parseBooks(response: JSONObject): List<Book> {
        val books = mutableListOf<Book>()
        val results = response.optJSONArray("results") ?: return books
        
        for (i in 0 until results.length()) {
            books.add(parseBookObject(results.getJSONObject(i)))
        }
        return books
    }

    private fun parseBookObject(bookObject: JSONObject): Book {
        val authors = mutableListOf<Author>()
        val authorsArray = bookObject.optJSONArray("authors")
        if (authorsArray != null) {
            for (j in 0 until authorsArray.length()) {
                val authorObject = authorsArray.getJSONObject(j)
                authors.add(
                    Author(
                        name = authorObject.optString("name", "Unknown Author"),
                        birth_year = if (authorObject.isNull("birth_year")) null else authorObject.optInt("birth_year"),
                        death_year = if (authorObject.isNull("death_year")) null else authorObject.optInt("death_year")
                    )
                )
            }
        }

        val formats = mutableMapOf<String, String>()
        val formatsObject = bookObject.optJSONObject("formats")
        if (formatsObject != null) {
            val keys = formatsObject.keys()
            while (keys.hasNext()) {
                val key = keys.next()
                formats[key] = formatsObject.getString(key)
            }
        }

        return Book(
            id = bookObject.getInt("id"),
            title = bookObject.optString("title", "Untitled"),
            authors = authors,
            formats = formats,
            summaries = jsonArrayToStringList(bookObject.optJSONArray("summaries")),
            subjects = jsonArrayToStringList(bookObject.optJSONArray("subjects")),
            languages = jsonArrayToStringList(bookObject.optJSONArray("languages")),
            download_count = bookObject.optInt("download_count", 0)
        )
    }

    private fun jsonArrayToStringList(array: JSONArray?): List<String> {
        val list = mutableListOf<String>()
        if (array != null) {
            for (i in 0 until array.length()) {
                list.add(array.getString(i))
            }
        }
        return list
    }
}
