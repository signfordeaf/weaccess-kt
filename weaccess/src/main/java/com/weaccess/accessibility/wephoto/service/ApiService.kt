package com.weaccess.accessibility.wephoto.service

import com.weaccess.accessibility.WeAccessConfig
import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.util.concurrent.TimeUnit


class ApiService {
    private val client = OkHttpClient.Builder()
        .connectTimeout(300, TimeUnit.SECONDS)
        .readTimeout(300, TimeUnit.SECONDS)
        .writeTimeout(300, TimeUnit.SECONDS)
        .build()
    private val retryDelayMillis = 1000L
    private var currentCall: Call? = null

    fun getImageDescription(imageUrl: String, callback: (result: String?, error: Throwable?) -> Unit) {
        val apiUrl = "https://pl.weaccess.ai/mobile/api/wephoto-create/"
        val body = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart("create_types","[\"alt\",\"desc\"]")
            .addFormDataPart("image_url",imageUrl)
            .addFormDataPart("lang","en")
            .addFormDataPart("api_key",WeAccessConfig.requestKey ?: "")
            .build()
        val request = Request.Builder()
            .url(apiUrl)
            .post(body)
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