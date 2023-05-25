package com.mason.messagesync.model

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitManager {
    var telegramDeafultApi: TelegramApiClass
    var telegramUrl = "https://api.telegram.org/bot"
    var token = "6191791735:AAFITj6a7NM73iix0jseL_SrLO_xLwtgM4A"
    var chatId = 6048802649

    val LINE_NOTIFY_TOKEN_MASON_ANDROID = "Bearer YRt98neeg5N94tomYeRO1HdUX7YE23HjkjB3TB2zfX7"
    val LINE_NOTIFY_TOKEN_MASON_GROUP = "Bearer Bk7mlxnuzRwVdsJ0zVXFFuqOBpDd61iBVbNEiawoKKv"


    init {
        var buildTelegram = Retrofit.Builder()
            .baseUrl(telegramUrl + token + "/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        telegramDeafultApi = buildTelegram.create(TelegramApiClass::class.java)

    }

    companion object {
        val INSTANCE = RetrofitManager()
    }

}