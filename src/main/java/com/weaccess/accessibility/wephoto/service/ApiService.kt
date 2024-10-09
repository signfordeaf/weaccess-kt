package com.weaccess.accessibility.wephoto.service

import com.weaccess.accessibility.WeAccessConfig
import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import java.io.IOException


class ApiService {
    private val client = OkHttpClient()
    private val retryDelayMillis = 1000L
    private var currentCall: Call? = null

    fun getImageDescription(imageUrl: String, descriptionType: String, callback: (result: String?, error: Throwable?) -> Unit) {
        val apiUrl = "http://68.154.90.84:8081/api/describe-image"
        val urlWithParams = apiUrl.toHttpUrlOrNull()?.newBuilder()?.apply {
            addQueryParameter("image_url", imageUrl)
            addQueryParameter("api_key", WeAccessConfig.requestKey)
            addQueryParameter("dest", "en")
            addQueryParameter("description_type", descriptionType)
        }?.build().toString()

        val request = Request.Builder()
            .url(urlWithParams)
            .get()
            .build()

        currentCall = client.newCall(request)
        currentCall?.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                if (call.isCanceled()) {
                    callback(null, null)
                } else {
                    callback(null, e)
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (!call.isCanceled()) {
                    if (response.isSuccessful) {
                        val responseBody = response.body?.string()
                        //val jsonResponse = JSONObject(responseBody)
                        callback(responseBody, null)
                    } else {
                        callback(null, IOException("Unexpected code $response"))
                    }
                }
            }
        })
    }

    fun cancelRequest() {
        currentCall?.cancel()
        currentCall = null
    }
}