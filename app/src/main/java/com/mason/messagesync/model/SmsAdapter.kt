package com.mason.messagesync.model

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mason.messagesync.R
import com.mason.messagesync.databinding.SmsItemBinding
import com.mason.messagesync.util.LogUtil
import java.text.SimpleDateFormat

class SmsAdapter : ListAdapter<Sms, SmsViewHolder>(SmsComparator()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SmsViewHolder {
        var inflate = LayoutInflater.from(parent.context).inflate(R.layout.sms_item, parent, false)

        return SmsViewHolder(inflate)
    }

    override fun onBindViewHolder(holder: SmsViewHolder, position: Int) {
        var timeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(getItem(position).date.toLong())
        LogUtil.d(this::class.java.simpleName, "onBindViewHolder: message: ${getItem(position).body}\ntime: ${getItem(position).date} -> formated: $timeFormat")
        holder.binding.textViewDateTime.text = timeFormat
        holder.binding.textViewMessageBody.text = getItem(position).body
        holder.binding.textViewPhoneNumber.text = getItem(position).address

    }

    companion object {
        private const val TAG = "SmsAdapter"
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
