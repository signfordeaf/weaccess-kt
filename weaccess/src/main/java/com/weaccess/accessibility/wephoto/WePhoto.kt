package com.weaccess.accessibility.wephoto

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.weaccess.accessibility.wephoto.model.ImageDescpriptionModel
import com.weaccess.accessibility.wephoto.service.ApiService
import java.util.LinkedList
import java.util.Queue

object WePhoto {
    private val gson = Gson()
    private val apiService = ApiService()

    private val descriptionCache = mutableMapOf<String, String?>()

    private val requestQueue: Queue<RequestTask> = LinkedList()
    private var isRequestInProgress = false

    private const val PREF_NAME = "image_description_cache"
    private const val CACHE_KEY = "cache_data"

    private fun getSharedPreferences(context: Context) = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun dispose() {
        requestQueue.clear()
        isRequestInProgress = false
        apiService.cancelRequest()
    }

    fun ImageView.getImageDescription(imageUrl: String, descriptionType: String) {
        loadCache(this.context)

        Glide.with(this.context).load(imageUrl).into(this)

        val cachedDescription = descriptionCache[imageUrl]

        if (cachedDescription != null) {
            this.contentDescription = cachedDescription
            Log.e("DEVOPS-NEVI", cachedDescription)
        } else {

            enqueueRequest(imageUrl, descriptionType) { description ->
                (this.context as? Activity)?.runOnUiThread {
                    this.contentDescription = description
                    Log.e("DEVOPS-NEVI", description)
                }
                descriptionCache[imageUrl] = description
                saveCache(this.context)
            }
        }
    }

    private fun enqueueRequest(imageUrl: String, descriptionType: String, callback: (String) -> Unit) {
        val task = RequestTask(imageUrl, descriptionType, callback)
        requestQueue.offer(task)
        processNextRequest()
    }

    private fun processNextRequest() {
        if (isRequestInProgress || requestQueue.isEmpty()) return

        val task = requestQueue.poll() ?: return
        isRequestInProgress = true

        fetchImageDescription(task.imageUrl, task.descriptionType) { description ->
            descriptionCache[task.imageUrl] = description
            task.callback(description)

            isRequestInProgress = false
            processNextRequest()
        }
    }

    private fun fetchImageDescription(imageUrl: String, descriptionType: String, callback: (String) -> Unit) {
        apiService.getImageDescription(imageUrl, descriptionType) { result, error ->
            if (error != null) {
                Log.e("DEVOPS-NEVI", error.toString())
                callback("Error fetching description")
            } else {
                val response = gson.fromJson(result, ImageDescpriptionModel::class.java)
                if (response.output != null) {
                    Log.e("DEVOPS-NEVI", response.output.first())
                    callback(response.output.first())
                } else {
                    callback("No description available")
                }
            }
        }
    }

    private fun saveCache(context: Context) {
        val sharedPreferences = getSharedPreferences(context)
        val editor = sharedPreferences.edit()
        val jsonCache = gson.toJson(descriptionCache)
        editor.putString(CACHE_KEY, jsonCache)
        editor.apply()
    }

    private fun loadCache(context: Context) {
        val sharedPreferences = getSharedPreferences(context)
        val jsonCache = sharedPreferences.getString(CACHE_KEY, null)

        if (jsonCache != null) {
            val type = object : TypeToken<MutableMap<String, String?>>() {}.type
            descriptionCache.putAll(gson.fromJson(jsonCache, type))
        }
    }

    data class RequestTask(
        val imageUrl: String,
        val descriptionType: String,
        val callback: (String) -> Unit
    )
}
