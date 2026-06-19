package com.afitech.ui.player

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.afitech.databinding.ActivityVideoPreviewBinding

class VideoPreviewActivity : androidx.appcompat.app.AppCompatActivity() {

    private lateinit var binding:
            ActivityVideoPreviewBinding

    private var player:
            ExoPlayer? = null

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)

        binding =
            ActivityVideoPreviewBinding.inflate(
                layoutInflater
            )

        setContentView(
            binding.root
        )

        val url =
            intent.getStringExtra(
                "video_url"
            ) ?: return

        player =
            ExoPlayer.Builder(this)
                .build()

        binding.playerView.player =
            player

        player?.setMediaItem(
            MediaItem.fromUri(
                Uri.parse(url)
            )
        )

        player?.prepare()

        player?.play()
    }

    override fun onStop() {

        player?.release()

        player = null

        super.onStop()
    }
}