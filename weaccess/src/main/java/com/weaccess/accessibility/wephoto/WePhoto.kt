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

enum class DescriptionType {
    SHORT,
    LONG
}

object WePhoto {
    private val gson = Gson()
    private val apiService = ApiService()

    // Cache'i ImageDescriptionModel olarak tanımlayın
    private val descriptionCache = mutableMapOf<String, ImageDescpriptionModel>()
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

    fun ImageView.getImageDescription(imageUrl: String, type: DescriptionType = DescriptionType.SHORT) {
        val context = this.context
        loadCache(context)

        Glide.with(context).load(imageUrl).into(this)

        val cachedDescription = descriptionCache[imageUrl]

        if (cachedDescription != null) {
            if (type == DescriptionType.SHORT) {
                this.contentDescription = cachedDescription.imageAltText
                Log.d("DEVOPS-NEVI", "short: ${cachedDescription.imageAltText}")
            } else if (type == DescriptionType.LONG) {
                this.contentDescription = cachedDescription.imageDesc
                Log.d("DEVOPS-NEVI", "long: ${cachedDescription.imageDesc}")
            } else {
                this.contentDescription = cachedDescription.imageAltText
            }
            Log.d("DEVOPS-NEVI", "cachedDescription {short: ${cachedDescription.imageAltText} long: ${cachedDescription.imageDesc}}")
        } else {
            enqueueRequest(imageUrl) { description ->
                (context as? Activity)?.runOnUiThread {
                    if (type == DescriptionType.SHORT) {
                        this.contentDescription = description.imageAltText
                        Log.d("DEVOPS-NEVI", "short: ${description.imageAltText}")
                    } else if (type == DescriptionType.LONG) {
                        this.contentDescription = description.imageDesc
                        Log.d("DEVOPS-NEVI", "long: ${description.imageDesc}}")
                    } else {
                        this.contentDescription = description.imageAltText
                        Log.d("DEVOPS-NEVI", "description: {short: ${description.imageAltText} long: ${description.imageDesc}}")
                    }
                }
                if (description.imageAltText != null && description.imageDesc != null) {
                    descriptionCache[imageUrl] = description
                    saveCache(context)
                }
            }
        }
    }

    private fun enqueueRequest(imageUrl: String, callback: (ImageDescpriptionModel) -> Unit) {
        val task = RequestTask(imageUrl, callback)
        requestQueue.offer(task)
        processNextRequest()
    }

    private fun processNextRequest() {
        if (isRequestInProgress || requestQueue.isEmpty()) return

        val task = requestQueue.poll() ?: return
        isRequestInProgress = true

        fetchImageDescription(task.imageUrl) { description ->
            task.callback(description)
            isRequestInProgress = false
            processNextRequest()
        }
    }

    private fun fetchImageDescription(imageUrl: String, callback: (ImageDescpriptionModel) -> Unit) {
        apiService.getImageDescription(imageUrl) { result, error ->
            if (error != null) {
                Log.e("DEVOPS-NEVI", error.toString())
            } else {
                try {
                    val response = gson.fromJson(result, ImageDescpriptionModel::class.java)
                    callback(response)
                } catch (e: Exception) {
                    Log.e("DEVOPS-NEVI", "Parsing error: ${e.message}")
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
            val type = object : TypeToken<MutableMap<String, ImageDescpriptionModel>>() {}.type
            descriptionCache.putAll(gson.fromJson(jsonCache, type))
        }
    }

    data class RequestTask(
        val imageUrl: String,
        val callback: (ImageDescpriptionModel) -> Unit
    )
}