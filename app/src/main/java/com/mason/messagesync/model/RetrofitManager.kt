package com.mason.messagesync.model

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitManager {
    lateinit var telegramDeafultApi: TelegramApiClass
    private var telegramUrl = "https://api.telegram.org/bot"
    private var _token = ""
    val token: String
        get() = _token
    private var _chatId = ""
    val chatId: String
        get() = _chatId

    init {
        recreateApi()
    }


    fun updateTelegramToken(token: String) {
        this._token = token
        recreateApi()
    }

    fun updateTelegramChatID(chatid: String) {
        this._chatId = chatid
    }

    fun recreateApi() {
        var retrofit = Retrofit.Builder()
            .baseUrl(telegramUrl + _token + "/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        telegramDeafultApi = retrofit.create(TelegramApiClass::class.java)
    }

    companion object {
        val INSTANCE = RetrofitManager()
    }

}