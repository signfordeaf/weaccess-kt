package com.weaccess.accessibility.wevideo.model

import com.google.gson.annotations.SerializedName

data class SignVideoModel (
    @SerializedName("st" ) var startTime : Double? = null,
    @SerializedName("et" ) var endTime : Double? = null,
    @SerializedName("vu" ) var videoUrl : String? = null,
    @SerializedName("vd" ) var videoDuration : Double? = null,
    @SerializedName("s"  ) var signText  : String? = null,
    @SerializedName("q"  ) var queue  : Int?    = null
)
