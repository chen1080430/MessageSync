package com.mason.messagesync.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import com.mason.messagesync.model.RetrofitManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okio.Buffer


class HomeViewModel : ViewModel() {

    val telegramApi by lazy { RetrofitManager.INSTANCE.telegramDeafultApi }

    private val _responseBody = MutableLiveData<String>()
    val responseBody: LiveData<String> = _responseBody

    val _testCounter = MutableLiveData<Int>(0)
    val testCounter: LiveData<Int>
        get() = _testCounter


    fun sendMessage(message: String) = CoroutineScope(Dispatchers.IO).launch {

        try {
            var sendMessage = telegramApi.sendMessage(RetrofitManager.INSTANCE.chatId, message)
//                var call = telegramApi.sendText(RetrofitManager.INSTANCE.token, RetrofitManager.INSTANCE.chatId , message)
//            var url = call.request().url
//            Log.d(TAG, "XXXXX> sendMessage: call: $call \nurl: $url\n request: ${call.request()} ")
//            val response = call.execute()
            Log.d(TAG, "XXXXX> sendMessage result: ${sendMessage}")
            _responseBody.postValue(sendMessage.toString())
        } catch (e: Exception) {
            Log.e(TAG, "XXXXX> sendMessage: e: ", e)
        }
        _testCounter.postValue(_testCounter.value?.plus(1))
    }

    fun sendMessageByOkhttp3(message: String) = CoroutineScope(Dispatchers.IO).launch {
        val url = "https://api.telegram.org/bot${RetrofitManager.INSTANCE.token}/sendMessage"
        val client = OkHttpClient().newBuilder().addInterceptor(CustomInterceptor()).build()

        var jsonRequestBody = JsonObject()
        jsonRequestBody.addProperty("chat_id", RetrofitManager.INSTANCE.chatId)
        jsonRequestBody.addProperty("text", message)
        val requestBodyString: String = jsonRequestBody.toString()

        var contentTypeMedia = "application/json".toMediaType()
        val request = Request.Builder()
            .post(requestBodyString.toRequestBody(contentTypeMedia))
            .url(url)
            .build()

        var execute = client.newCall(request).execute()
        Log.d(
            TAG,
            "XXXXX> sendMessageByOkhttp3: execute: $execute \nrequest: ${execute.request}\n body: ${execute.body?.string()}\n isSuccessful: ${execute.isSuccessful}"
        )
        _responseBody.postValue(execute.toString())
        _testCounter.postValue(_testCounter.value?.plus(1))
    }

    fun sendLineByOkhttp3(message: String): Job {
        return CoroutineScope(Dispatchers.IO).launch {
            val url = " https://notify-api.line.me/api/notify"
            val client = OkHttpClient().newBuilder().addInterceptor(CustomInterceptor()).build()

            val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("message", message)
                .build()
            val request = Request.Builder()
                .url(url)
                .header("Authorization", RetrofitManager.INSTANCE.LINE_NOTIFY_TOKEN_MASON_GROUP)
                .post(requestBody)
                .build()

            var execute = client.newCall(request).execute()
            Log.d(
                TAG,
                "XXXXX> sendLineByOkhttp3: \nbody: ${execute.body?.string()}\n isSuccessful: ${execute.isSuccessful}"
            )
            _responseBody.postValue(execute.toString())
            _testCounter.postValue(_testCounter.value?.plus(1))
        }
    }

    fun checkToken() = viewModelScope.launch {
        try {
            var checkToken = telegramApi.checkToken()
            Log.d(
                TAG,
                "XXXXX> checkToken: result: ${checkToken.ok}\ncheckToken: $checkToken\n request: ${checkToken.result}"
            )
            _responseBody.value = checkToken.toString()

        } catch (e: Throwable) {
            Log.e(TAG, "XXXXX> checkToken: e", e)
        }
        _testCounter.postValue(_testCounter.value?.plus(1))
    }

    fun launchDataLoad(block: suspend () -> Unit): Job {
        return viewModelScope.launch {
            try {
                block.invoke()
            } catch (e: Throwable) {
                Log.d(TAG, "XXXXX> launchDataLoad: e: $e")
            } finally {
            }
        }
    }


    companion object {
        private const val TAG = "HomeViewModel"
    }
}

class CustomInterceptor : Interceptor {
    private val TAG = "CustomInterceptor"
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
//        Log.d(TAG, "XXXXX> intercept: request: $request")

        var body = request.body
        body?.let {
            var buffer = Buffer()
            it.writeTo(buffer)
            var requestBodyString = buffer.readUtf8()
            Log.d(TAG, "XXXXX> intercept: requestBodyString: $requestBodyString")
        }

        var proceed = chain.proceed(chain.request())
        Log.d(
            TAG,
            "XXXXX> intercept: request: $request\nCode: ${proceed.code}\nmessage: ${proceed.message}"
        )
        return proceed
    }

}
