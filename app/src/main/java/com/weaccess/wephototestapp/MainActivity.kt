package com.weaccess.wephototestapp

import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import com.weaccess.accessibility.WeAccessConfig
import com.weaccess.accessibility.wephoto.WePhoto
import com.weaccess.accessibility.wephoto.WePhoto.getImageDescription
import com.weaccess.accessibility.wevideo.WeVideo


class MainActivity : ComponentActivity() {

    private lateinit var imageView: ImageView
    private lateinit var weVideo: WeVideo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
      //  imageView = findViewById(R.id.imageView)
        weVideo = findViewById(R.id.weVideo)
        WeAccessConfig.initialize("YOUR_API_KEY")
       // imageView.getImageDescription("https://cdn.pixabay.com/photo/2024/02/22/05/40/natural-scenery-8589165_1280.jpg", "short")
        initWeVideo()
    }

    private fun initWeVideo() {
        weVideo.loadVideo(Uri.parse("http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4"))
        weVideo.loadGif(R.drawable.sign_example)
        weVideo.playVideo()

    }

    override fun onDestroy() {
        WePhoto.dispose()
        super.onDestroy()
    }
}