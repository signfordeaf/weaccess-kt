package com.weaccess.wephototestapp

import android.media.browse.MediaBrowser
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.weaccess.accessibility.WeAccessConfig
import com.weaccess.accessibility.wephoto.DescriptionType
import com.weaccess.accessibility.wephoto.WePhoto
import com.weaccess.accessibility.wephoto.WePhoto.getImageDescription
import com.weaccess.accessibility.wevideo.WeVideo


class MainActivity : ComponentActivity() {

    private lateinit var imageView: ImageView
    private lateinit var playerView: PlayerView
    private lateinit var player: ExoPlayer
    private lateinit var weVideo: WeVideo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
//        imageView = findViewById(R.id.imageView)
        WeAccessConfig.initialize("YOUR_API_KEY")
//        imageView.getImageDescription("https://cdn.pixabay.com/photo/2024/02/22/05/40/natural-scenery-8589165_1280.jpg", DescriptionType.LONG)
        weVideo = findViewById(R.id.weVideo)
        initWeVideo()
        playerView.setFullscreenButtonClickListener { playerView.useController = true }
        player.prepare()
        player.play()
    }

    private fun initWeVideo() {
        playerView = PlayerView(this)
        player = ExoPlayer.Builder(this).build()
        val mediaItem = Uri.parse("http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/VolkswagenGTIReview.mp4")
        player.setMediaItem(MediaItem.fromUri(mediaItem))
        playerView.player = player
        weVideo.setPlayer(playerView)
        weVideo.loadGif(R.drawable.sign_example)
        player.addListener(object : Player.Listener {
            @OptIn(UnstableApi::class)
            override fun onPlayerError(error: PlaybackException) {
                Log.e("PlayerError", "Playback error: ${error.message}")
            }

            @OptIn(UnstableApi::class)
            override fun onPlaybackStateChanged(state: Int) {
                when (state) {
                    ExoPlayer.STATE_READY -> Log.d("PlaybackState", "Ready to play")
                    ExoPlayer.STATE_BUFFERING -> Log.d("PlaybackState", "Buffering")
                    ExoPlayer.STATE_ENDED -> Log.d("PlaybackState", "Playback ended")
                    ExoPlayer.STATE_IDLE -> Log.d("PlaybackState", "Idle state")
                }
            }
        })

    }

    private fun initV2() {

        player = ExoPlayer.Builder(this).build()
        val mediaItem = Uri.parse("http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4")
        player.setMediaItem(MediaItem.fromUri(mediaItem))
        playerView.player = player
        val videoV2 = WeVideo(this)
        videoV2.setPlayer(playerView)
        setContentView(videoV2)
    }

    override fun onDestroy() {
        WePhoto.dispose()
        super.onDestroy()
    }
}