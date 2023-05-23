package com.mason.messagesync.model

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mason.messagesync.R
import com.mason.messagesync.databinding.SmsItemBinding

class SmsAdapter : ListAdapter<Sms, SmsViewHolder>(SmsComparator()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SmsViewHolder {
        var inflate = LayoutInflater.from(parent.context).inflate(R.layout.sms_item, parent, false)

        return SmsViewHolder(inflate)
    }

    override fun onBindViewHolder(holder: SmsViewHolder, position: Int) {
        holder.binding.textViewDateTime.text = getItem(position).date
        holder.binding.textViewMessageBody.text = getItem(position).body
        holder.binding.textViewPhoneNumber.text = getItem(position).address
    }
}

class SmsComparator : DiffUtil.ItemCallback<Sms>() {
    override fun areItemsTheSame(oldItem: Sms, newItem: Sms): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Sms, newItem: Sms): Boolean {
        return oldItem.id == newItem.id
    }

}

class SmsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var binding: SmsItemBinding
    init {
        binding = SmsItemBinding.bind(itemView)
    }
}
