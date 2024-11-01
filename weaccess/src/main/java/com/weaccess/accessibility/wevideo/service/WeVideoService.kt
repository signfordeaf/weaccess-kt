package com.weaccess.accessibility.wevideo.service

import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


import com.weaccess.accessibility.wevideo.model.SignVideoModel

object WeVideoService {
    private val gson = Gson()
    private val apiService = ApiService()

    private val _videoDescriptionList: MutableList<SignVideoModel> = mutableListOf()

    fun fetchVideoDescription(videoSubtitleId: String, fdid: Int, tid: Int,onCompleted: (List<SignVideoModel>) -> Unit) {
        apiService.getVideoInformantion(videoSubtitleId, fdid, tid) { result, error ->
            if (error != null) {
                Log.d("DEVOPS-NEVI", error.toString())
            } else {
                val response: List<SignVideoModel> = gson.fromJson(result.toString(), object : TypeToken<List<SignVideoModel>>() {}.type)
                _videoDescriptionList.clear()
                _videoDescriptionList.addAll(response)
                onCompleted(_videoDescriptionList)
            }
        }
    }
}