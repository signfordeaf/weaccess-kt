package com.weaccess.wephototestapp

import android.os.Bundle
import android.widget.ImageView
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import com.weaccess.accessibility.WeAccessConfig
import com.weaccess.accessibility.wephoto.WePhoto
import com.weaccess.accessibility.wephoto.WePhoto.getImageDescription


class MainActivity : ComponentActivity() {

    private lateinit var imageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        imageView = findViewById(R.id.imageView)
        WeAccessConfig.initialize("YOUR-API-KEY")
        imageView.getImageDescription("https://picsum.photos/200", "short")

    }

    override fun onDestroy() {
        WePhoto.dispose()
        super.onDestroy()
    }
}