package com.afitech.ui.status

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import coil.load
import com.afitech.data.repository.HistoryRepository
import com.afitech.databinding.ActivityImagePreviewBinding
import com.afitech.utils.StatusFileChecker
import com.afitech.utils.StatusSaveHelper
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ImagePreviewActivity : AppCompatActivity() {

    private lateinit var binding:
            ActivityImagePreviewBinding

    private lateinit var imageUri: Uri

    private lateinit var fileName: String

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)

        binding =
            ActivityImagePreviewBinding.inflate(
                layoutInflater
            )

        setContentView(binding.root)

        imageUri = Uri.parse(
            intent.getStringExtra("uri")
        )

        fileName =
            intent.getStringExtra("name")
                ?: "status"

        binding.imgPreview.load(
            imageUri
        )

        updateSavedState()

        binding.btnClose.setOnClickListener {
            finish()
        }

        binding.btnShare.setOnClickListener {
            shareImage()
        }

        binding.btnSave.setOnClickListener {
            saveImage()
        }
    }

    private fun saveImage() {

        CoroutineScope(
            Dispatchers.IO
        ).launch {

            try {

                val path =

                    StatusSaveHelper.saveStatus(

                        this@ImagePreviewActivity,

                        imageUri,

                        fileName
                    )

                HistoryRepository(
                    this@ImagePreviewActivity
                ).saveHistory(

                    fileName = fileName,

                    fileType = "Status Image",

                    filePath = path
                )

                runOnUiThread {

                    Snackbar.make(

                        binding.root,

                        "Saved successfully",

                        Snackbar.LENGTH_SHORT

                    ).show()

                    updateSavedState()
                }

            } catch (e: Exception) {

                runOnUiThread {

                    Snackbar.make(

                        binding.root,

                        e.message ?: "Save failed",

                        Snackbar.LENGTH_LONG

                    ).show()
                }
            }
        }
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

    private fun shareImage() {

        val intent = Intent(
            Intent.ACTION_SEND
        )

        intent.type = "image/*"

        intent.putExtra(
            Intent.EXTRA_STREAM,
            imageUri
        )

        intent.addFlags(
            Intent.FLAG_GRANT_READ_URI_PERMISSION
        )

        startActivity(
            Intent.createChooser(
                intent,
                "Share Status"
            )
        )
    }
}