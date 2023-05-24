package com.mason.messagesync.model

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query


interface TelegramApiClass {

    @GET("sendMessage")
    suspend fun postMessage(
        @Query("chat_id") id: Long, @Query("text") text: String
    ): Call<ResponseBody>
//    suspend fun postMessage(@Query("chat_id") id: Long, @Query("text") text: String): Call<String>

    @FormUrlEncoded
    @POST("sendMessage")
    suspend fun sendMessage(
        @Field("chat_id") id: Long, @Field("text") text: String
    ): Call<String>


    @FormUrlEncoded
    @POST("bot{botid}/sendMessage")
    suspend fun sendText(
        @Path("botid") botid: String, @Field("chat_id") id: Long, @Field("text") text: String
    ): Call<String>

    /*
    //    for telegramUrl_nobot
        @POST("bot6191791735:AAFITj6a7NM73iix0jseL_SrLO_xLwtgM4A/sendMessage")
        suspend fun sendMessage(
            @Body request: SendMessageRequest
        )
    */

}
