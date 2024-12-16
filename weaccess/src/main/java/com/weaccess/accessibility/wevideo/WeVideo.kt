package com.weaccess.accessibility.wevideo

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.ui.PlayerView
import com.bumptech.glide.Glide
import com.weaccess.accessibility.R
import com.weaccess.accessibility.wevideo.model.SignVideoModel
import com.weaccess.accessibility.wevideo.service.WeVideoService

class WeVideo @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,

) : FrameLayout(context, attrs, defStyleAttr) {

    private val gifImageView: ImageView
    private  val buttonImageView: ImageView
    private lateinit var outPlayer: PlayerView
    private var isZoomed = false
    private var textView: TextView
    private val videoService: WeVideoService = WeVideoService

    var signVideoList: List<SignVideoModel> = emptyList()

    init {
        Log.d("DEVOPS-NEVI", "WeVideo init")
        val view = LayoutInflater.from(context).inflate(R.layout.wevideo_frame, this, true)
        gifImageView = view.findViewById(R.id.gif_image)
        buttonImageView = view.findViewById(R.id.controller_button)
        buttonImageView.setOnClickListener{
            signVideoVisibility(gifImageView.visibility)
        }
        textView = view.findViewById<TextView>(R.id.text_view)
        videoService.fetchVideoDescription("730", 18,  26, onCompleted = {
            signVideoList = it
        })

    }

    private fun signVideoVisibility(visibility: Int) {
        if (visibility == View.VISIBLE) {
            gifImageView.visibility = View.GONE
        } else {
            gifImageView.visibility = View.VISIBLE
        }
    }

    fun setPlayer(player: PlayerView) {
        player.parent?.let {
            (it as ViewGroup).removeView(player)
        }
        val container = findViewById<FrameLayout>(R.id.video_container)
        container.removeAllViews()
        container.addView(player)
        outPlayer = player
        dragAndMoveSignGIF(player)
        visibleSignTranslateButton(player)
        demoTextView(signVideoList, player)
        }

    private fun getSignVideos(list: List<SignVideoModel>) {
        list.forEach {
            Log.d("DEVOPS-NEVI", "service gelen ${it.videoDuration.toString()}")
        }
    }

    private fun visibleSignTranslateButton(player: PlayerView) {
        val controllerVisibilityListener = object : PlayerView.ControllerVisibilityListener {
            fun onVisibilityChange(visibility: Int) {
                if (visibility == View.VISIBLE) {
                    buttonImageView.visibility = View.VISIBLE
                } else {
                    buttonImageView.visibility = View.GONE
                }
            }
            override fun onVisibilityChanged(visibility: Int) {
                if (visibility == View.VISIBLE) {
                    buttonImageView.visibility = View.VISIBLE
                } else {
                    buttonImageView.visibility = View.GONE
                }
            }
        }
        player.setControllerVisibilityListener(controllerVisibilityListener)
    }


    @SuppressLint("ClickableViewAccessibility")
    private fun dragAndMoveSignGIF(playerView: PlayerView) {
        gifImageView.setOnTouchListener(object : OnTouchListener {
            var dX = 0f
            var dY = 0f
            var parentWidth = 0
            var parentHeight = 0
            var startX = 0f
            var startY = 0f
            var startTime = 0L
            val CLICK_THRESHOLD = 10
            val TIME_THRESHOLD = 200

            override fun onTouch(view: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        startX = event.rawX
                        startY = event.rawY
                        startTime = System.currentTimeMillis()
                        dX = view.x - event.rawX
                        dY = view.y - event.rawY
                        parentWidth = playerView.width
                        parentHeight = playerView.height
                    }
                    MotionEvent.ACTION_MOVE -> {
                        var newX = event.rawX + dX
                        var newY = event.rawY + dY
                        val gifWidth = view.width
                        val gifHeight = view.height
                        newX = when {
                            newX < 0 -> 0f
                            newX + gifWidth > parentWidth -> (parentWidth - gifWidth).toFloat()
                            else -> newX
                        }
                        newY = when {
                            newY < 0 -> 0f
                            newY + gifHeight > parentHeight -> (parentHeight - gifHeight).toFloat()
                            else -> newY
                        }
                        view.animate()
                            .x(newX)
                            .y(newY)
                            .setDuration(0)
                            .start()
                    }
                    MotionEvent.ACTION_UP -> {
                        val distanceX = Math.abs(event.rawX - startX)
                        val distanceY = Math.abs(event.rawY - startY)
                        val elapsedTime = System.currentTimeMillis() - startTime
                        if (distanceX < CLICK_THRESHOLD && distanceY < CLICK_THRESHOLD && elapsedTime < TIME_THRESHOLD) {
                            view.performClick()
                            gifImageView.setOnClickListener {
                                toggleGifSize()
                            }
                        }
                    }
                }
                return true
            }
        })
    }

    private fun demoTextView(list: List<SignVideoModel>, player: PlayerView) {
        val playerInstance = player.player ?: return  // Player nesnesini kontrol edin

        // Dinleyici ekleyin
        playerInstance.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                // Eğer oynatılma başladıysa (Player.STATE_READY), süreyi dinlemeye başlayın
                if (playbackState == Player.STATE_READY) {
                    // Videonun oynatılma süresini kontrol edin
                    playerInstance.playWhenReady = true

                    // Zamanlayıcı başlatın, her 500 ms'de bir kontrol
                    val handler = Handler(Looper.getMainLooper())
                    handler.post(object : Runnable {
                        override fun run() {
                            // Şu anki oynatma süresi
                            val currentPosition = playerInstance.currentPosition
                            // Doğru zaman dilimindeki signText'i bulun
                            val currentSignText = getCurrentSignText(list, currentPosition)
                            textView.text = currentSignText

                            // 500 ms sonra tekrar çalıştır
                            handler.postDelayed(this, 500)
                        }
                    })
                }
            }
        })
    }

    private fun getCurrentSignText(list: List<SignVideoModel>, currentPosition: Long): String? {
        list.forEach { signVideoModel ->
            val startTime = signVideoModel.startTime?.times(1000)
            val endTime = signVideoModel.endTime?.times(1000)
            if (startTime != null && endTime != null) {
                if (currentPosition.toDouble() in startTime..endTime) {
                    return signVideoModel.signText
                }
            }
        }
        return "BOŞ ALAN --- BOŞ ALAN --- BOŞ ALAN --- BOŞ ALAN"
    }

    fun loadGif(gifResource: Int) {
        gifImageView.visibility = View.VISIBLE
        Glide.with(context)
            .asGif()
            .load(gifResource)
            .into(gifImageView)
        buttonImageView.visibility = View.VISIBLE
        Glide.with(context)
            .asDrawable()
            .load(R.drawable.engelsizceviri)
            .into(buttonImageView)
    }

    private fun dpToPx(dp: Int, context: Context): Int {
        val density = context.resources.displayMetrics.density
        return (dp * density).toInt()
    }

    private fun toggleGifSize() {
        isZoomed = !isZoomed
        val newSizeDp = if (isZoomed) 200 else 100
        val newSizePx = dpToPx(newSizeDp, gifImageView.context)
            // Normal boyuta geri dön
        gifImageView.layoutParams.width = newSizePx // Normal genişlik
        gifImageView.layoutParams.height = newSizePx // Normal yükseklik
        gifImageView.requestLayout() // Değişiklikleri uygula
    }
}