package com.weaccess.wephototestapp

import android.os.Bundle
import android.widget.ImageView
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import com.weaccess.accessibility.WeAccessConfig
import com.weaccess.accessibility.wephoto.DescriptionType
import com.weaccess.accessibility.wephoto.WePhoto
import com.weaccess.accessibility.wephoto.WePhoto.getImageDescription



class MainActivity : ComponentActivity() {

    private lateinit var imageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        imageView = findViewById(R.id.imageView)
        WeAccessConfig.initialize("YOUR_API_KEY")
        imageView.getImageDescription("https://cdn.pixabay.com/photo/2024/02/22/05/40/natural-scenery-8589165_1280.jpg", DescriptionType.LONG)
    }

    override fun onDestroy() {
        WePhoto.dispose()
        super.onDestroy()
    }
}