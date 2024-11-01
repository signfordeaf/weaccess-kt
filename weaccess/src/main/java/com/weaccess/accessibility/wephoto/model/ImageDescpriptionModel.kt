package com.weaccess.accessibility.wephoto.model

import com.google.gson.annotations.SerializedName

data class ImageDescpriptionModel(
    @SerializedName("image_id")
    val imageId: String?,
    @SerializedName("source_path")
    val sourcePath: String?,
    @SerializedName("image_desc")
    val imageDesc: String?,
    @SerializedName("image_alt_text")
    val imageAltText: String?,
)
