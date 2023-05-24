package com.mason.messagesync.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mason.messagesync.model.RetrofitManager
import kotlinx.coroutines.launch


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

    companion object {
        private const val TAG = "HomeViewModel"
    }
}