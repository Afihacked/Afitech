package com.afitech.ui.downloads

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.afitech.databinding.ItemDownloadProgressBinding

class DownloadAdapter :

    androidx.recyclerview.widget.ListAdapter<
            DownloadItem,
            DownloadAdapter.ViewHolder
            >(DownloadDiffCallback) {

    inner class ViewHolder(

        private val binding:
        ItemDownloadProgressBinding

    ) : RecyclerView.ViewHolder(
        binding.root
    ) {

        fun bind(item: DownloadItem) {

            binding.txtFileName.text =
                item.fileName

            binding.progressBar.progress =
                item.progress

            binding.txtProgress.text =

                if (item.completed) {

                    "Completed ✓"

                } else {

                    "Downloading... ${item.progress}%"
                }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        return ViewHolder(

            ItemDownloadProgressBinding.inflate(

                LayoutInflater.from(
                    parent.context
                ),

                parent,

                false
            )
        )
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {

        holder.bind(
            getItem(position)
        )
    }
}