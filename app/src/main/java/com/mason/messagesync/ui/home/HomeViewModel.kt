package com.mason.messagesync.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mason.messagesync.model.RetrofitManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttp
import okhttp3.OkHttpClient
import okhttp3.Request
import kotlin.math.log


class HomeViewModel : ViewModel() {

    val telegramApi by lazy { RetrofitManager.INSTANCE.telegramApi }

    private val _text = MutableLiveData<String>().apply {
        value = "Welcome to message sync app\nplease sign in/up first."
    }
    val text: LiveData<String> = _text

    fun sendMessage(message: String) = viewModelScope.launch {

        try {
//                var call = telegramApi.postMessage(RetrofitManager.INSTANCE.chatId , message)
            var call = telegramApi.sendMessage(RetrofitManager.INSTANCE.chatId, message)
//                var call = telegramApi.sendText(RetrofitManager.INSTANCE.token, RetrofitManager.INSTANCE.chatId , message)
            var url = call.request().url
            Log.d(TAG, "XXXXX> sendMessage: call: $call \nurl: $url\n request: ${call.request()} ")
            val response = call.execute()

            if (response.isSuccessful) {
                val responseData = response.body()
                Log.d(TAG, "XXXXX> sendMessage: responseData: $responseData")
            } else {
            }
        } catch (e: Exception) {
            Log.e(TAG, "XXXXX> sendMessage: e: ", e)
        }
    }

    fun sendMessageByOkhttp3(message: String) = CoroutineScope(Dispatchers.IO).launch {
        // make connection to telegram api by okhttp
        val url =
            "https://api.telegram.org/bot${RetrofitManager.INSTANCE.token}/sendMessage?chat_id=${RetrofitManager.INSTANCE.chatId}&text=$message"
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(url)
            .build()
        var execute = client.newCall(request).execute()
        Log.d(
            TAG,
            "XXXXX> sendMessageByOkhttp3: execute: $execute \nrequest: $request\n isSuccessful: ${execute.isSuccessful}"
        )
    }


    companion object {
        private const val TAG = "HomeViewModel"
    }
}