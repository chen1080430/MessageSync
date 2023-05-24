package com.mason.messagesync.model

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.experimental.CoroutineCallAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitManager {
    var telegramApi: TelegramApiClass
    var telegramUrl = "https://api.telegram.org/bot"
    var telegramUrl_nobot = "https://api.telegram.org/"
    var token = "6191791735:AAFITj6a7NM73iix0jseL_SrLO_xLwtgM4A"
    var chatId = 6048802649

    init {
        var buildTelegram = Retrofit.Builder()
            .baseUrl(telegramUrl + token + "/")
//            .baseUrl(telegramUrl_nobot)
//            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        telegramApi = buildTelegram.create(TelegramApiClass::class.java)

    }

    companion object {
        val INSTANCE = RetrofitManager()
    }

}