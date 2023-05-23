package com.mason.messagesync.ui

import android.view.View
import androidx.databinding.BindingAdapter

@BindingAdapter("app:showIfVisible")
fun showIfVisible(view: View, visible: Boolean) {
    view.visibility = if (visible) View.VISIBLE else View.GONE
}