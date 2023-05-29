package com.mason.messagesync.model

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path


interface TelegramApiClass {
    @FormUrlEncoded
    @POST("sendMessage")
    suspend fun sendMessage(
        @Field("chat_id") id: String, @Field("text") text: String
    ): TelegramResponse


    @FormUrlEncoded
    @POST("sendMessage")
    suspend fun sendText(
        @Path("botid") botid: String, @Field("chat_id") id: Long, @Field("text") text: String
    ): TelegramResponse

    @GET("getMe")
    suspend fun checkToken(): TelegramTokenStatus

    @get:GET("getMe")
    var checkTokenStatus: Call<ResponseBody>

}
