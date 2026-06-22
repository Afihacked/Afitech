package com.afitech.ui.player

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.afitech.data.repository.HistoryRepository
import com.afitech.databinding.ActivityVideoPreviewBinding
import com.afitech.utils.StatusFileChecker
import com.afitech.utils.StatusSaveHelper
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class VideoPreviewActivity : AppCompatActivity() {

    private lateinit var binding:
            ActivityVideoPreviewBinding

    private val timeoutHandler =
        Handler(
            Looper.getMainLooper()
        )
    private var player:
            ExoPlayer? = null
    private lateinit var videoUri: Uri

    private lateinit var fileName: String
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

        val videoUrl =
            intent.getStringExtra(
                "video_url"
            )

        val videoUriString =
            intent.getStringExtra(
                "video_uri"
            )

        fileName =
            intent.getStringExtra(
                "file_name"
            ) ?: "status_video"

        if (
            videoUrl.isNullOrBlank() &&
            videoUriString.isNullOrBlank()
        ) {

            showMessage(
                "Link video tidak tersedia."
            )

            finish()

            return

        }
        player =
            ExoPlayer.Builder(this)
                .build()

        player?.addListener(
            object : Player.Listener {

                override fun onPlaybackStateChanged(
                    state: Int
                ) {

                    when (state) {

                        Player.STATE_BUFFERING -> {

                            Log.d(
                                "VIDEO_PLAYER",
                                "Buffering"
                            )
                        }

                        Player.STATE_READY -> {

                            timeoutHandler.removeCallbacksAndMessages(
                                null
                            )

                            Log.d(
                                "VIDEO_PLAYER",
                                "Ready"
                            )
                        }

                        Player.STATE_ENDED -> {

                            Log.d(
                                "VIDEO_PLAYER",
                                "Ended"
                            )
                        }
                    }
                }

                override fun onPlayerError(
                    error: PlaybackException
                ) {

                    Log.e(
                        "VIDEO_PLAYER",
                        "Playback Error",
                        error
                    )

                    val message = when {
                        error.errorCode == PlaybackException.ERROR_CODE_DECODING_FAILED ->
                            "Video terlalu berat atau tidak didukung perangkat ini. Anda tetap dapat mengunduh dan memutarnya menggunakan pemutar video lain."

                        error.message?.contains(
                            "NO_EXCEEDS_CAPABILITIES",
                            true
                        ) == true ->
                            "Video memiliki kualitas yang terlalu tinggi untuk perangkat ini."

                        error.errorCodeName.contains(
                            "IO_NETWORK_CONNECTION_FAILED",
                            true
                        ) ->
                            "Tidak dapat terhubung ke internet."

                        error.errorCodeName.contains(
                            "IO_NETWORK_CONNECTION_TIMEOUT",
                            true
                        ) ->
                            "Koneksi internet terlalu lambat atau terputus."

                        error.errorCodeName.contains(
                            "IO_BAD_HTTP_STATUS",
                            true
                        ) ->
                            "Video tidak tersedia atau server menolak permintaan."

                        error.errorCodeName.contains(
                            "IO_FILE_NOT_FOUND",
                            true
                        ) ->
                            "Video tidak ditemukan atau tautan sudah tidak berlaku."

                        error.errorCodeName.contains(
                            "PARSING",
                            true
                        ) ->
                            "Format video tidak didukung."

                        error.errorCodeName.contains(
                            "DECODER",
                            true
                        ) ->
                            "Perangkat tidak mendukung format video ini."
                        error.errorCodeName.contains(
                            "IO_INVALID_HTTP_CONTENT_TYPE",
                            true
                        ) ->
                            "Server mengirim format yang tidak dapat diputar."

                        error.errorCodeName.contains(
                            "IO_READ_POSITION_OUT_OF_RANGE",
                            true
                        ) ->
                            "Video tidak lengkap atau sudah tidak tersedia."

                        error.errorCodeName.contains(
                            "IO_CLEARTEXT_NOT_PERMITTED",
                            true
                        ) ->
                            "Koneksi ke server video ditolak."

                        error.errorCodeName.contains(
                            "BEHIND_LIVE_WINDOW",
                            true
                        ) ->
                            "Video sudah tidak tersedia untuk diputar."
                        else ->
                            "Gagal memutar video. Periksa koneksi atau coba lagi nanti."
                    }

                    showMessage(message)
                    Log.e(
                        "VIDEO_PLAYER_CODE",
                        error.errorCodeName
                    )
                }
            }
        )

        binding.playerView.player =
            player

        videoUri =

            if (!videoUrl.isNullOrBlank()) {

                Uri.parse(videoUrl)

            } else {

                Uri.parse(videoUriString)
            }

        player?.setMediaItem(

            MediaItem.fromUri(
                videoUri
            )
        )

        player?.prepare()

        player?.play()

        timeoutHandler.postDelayed({

            if (
                player != null &&
                player?.playbackState ==
                Player.STATE_BUFFERING
            ) {

                showMessage(
                    "Video masih memuat. Periksa koneksi internet atau coba video lain."
                )
            }

        }, 10000)

        binding.btnClose.setOnClickListener {

            finish()
        }

        binding.btnShare.setOnClickListener {

            shareVideo()
        }

        binding.btnSave.setOnClickListener {

            saveVideo()
        }

        updateSavedState()

    }
    private fun updateSavedState() {

        val saved =

            StatusFileChecker.isSaved(
                fileName
            )

        binding.btnSave.text =

            if (saved)
                "Saved ✓"
            else
                "Save"

        binding.btnSave.isEnabled =
            !saved
    }

    private fun shareVideo() {

        val intent =
            Intent(Intent.ACTION_SEND)

        intent.type = "video/*"

        intent.putExtra(
            Intent.EXTRA_STREAM,
            videoUri
        )

        intent.addFlags(
            Intent.FLAG_GRANT_READ_URI_PERMISSION
        )

        startActivity(
            Intent.createChooser(
                intent,
                "Share Video"
            )
        )
    }

    private fun saveVideo() {

        CoroutineScope(
            Dispatchers.IO
        ).launch {

            try {

                val path =

                    StatusSaveHelper.saveStatus(

                        this@VideoPreviewActivity,

                        videoUri,

                        fileName
                    )

                HistoryRepository(
                    this@VideoPreviewActivity
                ).saveHistory(

                    fileName = fileName,

                    fileType = "Status Video",

                    filePath = path
                )

                runOnUiThread {

                    showMessage(
                        "Saved successfully"
                    )

                    updateSavedState()
                }

            } catch (e: Exception) {

                runOnUiThread {

                    showMessage(

                        e.message
                            ?: "Save failed"
                    )
                }
            }
        }
    }
    private fun showMessage(
        message: String
    ) {
        Snackbar.make(
            binding.root,
            message,
            Snackbar.LENGTH_LONG
        ).show()
    }

    override fun onStop() {

        timeoutHandler.removeCallbacksAndMessages(
            null
        )

        player?.release()

        player = null

        super.onStop()
    }
}