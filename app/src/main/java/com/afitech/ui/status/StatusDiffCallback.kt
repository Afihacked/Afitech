package com.afitech.ui.status

import androidx.recyclerview.widget.DiffUtil
import com.afitech.data.model.WhatsappStatus

class StatusDiffCallback :
    DiffUtil.ItemCallback<WhatsappStatus>() {

    override fun areItemsTheSame(
        oldItem: WhatsappStatus,
        newItem: WhatsappStatus
    ): Boolean {

        return oldItem.uri ==
                newItem.uri
    }

    override fun areContentsTheSame(
        oldItem: WhatsappStatus,
        newItem: WhatsappStatus
    ): Boolean {

        return oldItem == newItem
    }
}