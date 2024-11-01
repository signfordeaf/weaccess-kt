package com.weaccess.accessibility.wevideo.service

import okhttp3.Call
import okhttp3.Callback
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONException
import java.io.IOException

class ApiService {
    private val client = OkHttpClient()
    private val retryDelayMillis = 1000L
    private var currentCall: Call? = null

    fun getVideoInformantion(videoSubtitleId: String, fdid: Int, tid : Int, callback: (result: JSONArray?, error: Throwable?) -> Unit) {
        val apiUrl = "https://kor01rp02.signfordeaf.com/VideoSubtitle/GetPanel/"
        val urlWithParams = apiUrl.toHttpUrlOrNull()?.newBuilder()?.apply {
            addQueryParameter("videoSubtitleId", videoSubtitleId)
            addQueryParameter("rk", "7DF6679E93BE4391ABBCF5003")
            addQueryParameter("fdid", fdid.toString())
            addQueryParameter("tid", tid.toString())
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

                        if (responseBody != null) {
                            try {
                                val jsonArray = JSONArray(responseBody)
                                for (i in 0 until jsonArray.length()) {
                                    val jsonObject = jsonArray.getJSONObject(i)
                                }
                                callback(jsonArray, null)
                            } catch (e: JSONException) {
                                callback(null, e)
                            }
                        } else {
                            callback(null, Exception("Response body is null"))
                        }
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