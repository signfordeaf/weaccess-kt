package com.weaccess.accessibility.wevideo

import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.bumptech.glide.Glide
import com.weaccess.accessibility.R

class WeVideo @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    private val playerView: PlayerView
    private val gifImageView: ImageView
    private val player: ExoPlayer

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.wevideo_frame, this, true)
        playerView = view.findViewById(R.id.player_view)
        gifImageView = view.findViewById(R.id.gif_image)
        player = ExoPlayer.Builder(context).build()
        playerView.player = player
    }

    fun loadVideo(videoUri: Uri) {
        val mediaItem = MediaItem.fromUri(videoUri)
        player.setMediaItem(mediaItem)
        player.prepare()
    }

    fun playVideo() {
        player.play()
    }

    fun stopVideo() {
        player.stop()
    }

    fun loadGif(gifResource: Int) {
        Glide.with(context)
            .asGif()
            .load(gifResource)
            .into(gifImageView)
    }

}