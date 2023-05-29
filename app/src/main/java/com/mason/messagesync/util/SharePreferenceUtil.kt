package com.mason.messagesync.util

import android.content.Context

class SharePreferenceUtil {
    companion object {
        private val DB_USER_DATA = "user_data"
        private val TELEGRAM_TOKEN = "telegram_token"

        fun putTelegramToken(context: Context, token: String) {

            context.getSharedPreferences(DB_USER_DATA, Context.MODE_PRIVATE).edit()
                .putString(TELEGRAM_TOKEN, token).apply()
        }

        fun getTelegramToken(context: Context): String? {
            return context.getSharedPreferences(DB_USER_DATA, Context.MODE_PRIVATE)
                .getString(TELEGRAM_TOKEN, null)
        }
    }
}