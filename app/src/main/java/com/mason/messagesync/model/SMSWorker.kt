package com.mason.messagesync.model

import android.content.Context
import android.database.Cursor
import android.net.Uri
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.mason.messagesync.model.SMSBroadcastReceiver.Companion.MESSAGE_ADDRESS_KEY
import com.mason.messagesync.model.SMSBroadcastReceiver.Companion.MESSAGE_BODY_KEY
import com.mason.messagesync.model.SMSBroadcastReceiver.Companion.MESSAGE_TIMESTAMP_KEY
import com.mason.messagesync.util.LogUtil
import kotlinx.coroutines.InternalCoroutinesApi
import java.text.SimpleDateFormat
import java.util.Locale

class SMSWorker(val context: Context, workerParams: WorkerParameters) :
    CoroutineWorker(context, workerParams) {
    companion object {
        private const val TAG = "SMSWorker"
    }

    private var smsList = mutableListOf<Sms>()


    override suspend fun doWork(): Result {
//        if (!NetworkUtil.isNetworkConnected(context)) {
//            Log.d(TAG, "XXXXX> doWork: finished  result failure: network not connected.")
//            return Result.failure()
//        }

        var messageBody = inputData.getString(MESSAGE_BODY_KEY) ?: kotlin.run {
            return Result.failure()
        }
        var messageAddress = inputData.getString(MESSAGE_ADDRESS_KEY) ?: ""
        var messageTimestamp = inputData.getString(MESSAGE_TIMESTAMP_KEY) ?: ""


        return sendSMSasNotification(messageAddress, messageBody, messageTimestamp)
    }

    private suspend fun sendSMSasNotification(
        messageAddress: String,
        messageBody: String,
        messageTimestamp: String
    ): Result {
        var completeSms = getCompleteMessage(messageAddress, messageBody, messageTimestamp)
        completeSms?.let { sms ->
            var telegramDeafultApi = RetrofitManager.INSTANCE.telegramDeafultApi
            try {
                var sendMessage = telegramDeafultApi.sendMessage(
                    RetrofitManager.INSTANCE.chatId,
                    "新簡訊來囉!\nFrom:  ${completeSms.address}" +
                            "\nMessage: \n${sms.body}\nTime:   ${
                                timeFormat(
                                    sms.date.toLong()
                                )
                            }"
                )
                LogUtil.i(
                    TAG,
                    "sendSMSAsNotification: sendMessage.ok: ${sendMessage.ok}" +
                            "\n sendMessage.result = ${sendMessage.result}"
                )
            } catch (e: Throwable) {
                LogUtil.e(TAG, "sendSMSAsNotification: e: $e")
                return Result.failure()
            }
            return Result.success()
        } ?: kotlin.run {
            LogUtil.e(TAG, "sendSMSAsNotification: completeSms is null")
            return Result.failure()
        }
    }


    private suspend fun getCompleteMessage(
        messageAddress: String,
        messageBody: String,
        messageTimestamp: String
    ): Sms? {
        loadLast5Messages()
        smsList.forEach { sms ->
            messageAddress.takeIf { address -> sms.address == address }
                ?.let { address1 ->
                    messageBody.takeIf { body -> sms.body.contains(body) }?.let {
                        LogUtil.d(
                            this::class.java.simpleName,
                            "getCompleteMessage: got complete message."
                        )
                        return sms
                    }
                }
        }
        LogUtil.e(this::class.java.simpleName, "getCompleteMessage: no match sms found")
        return null
    }

    @OptIn(InternalCoroutinesApi::class)
    private suspend fun loadLast5Messages(): MutableList<Sms> {
        kotlinx.coroutines.internal.synchronized(smsList) {
            smsList.clear()
            val cursor: Cursor? = context?.contentResolver?.query(
                Uri.parse("content://sms/inbox"),
                null,
                null,
                null,
                "date DESC"
            )

            cursor?.use {
                val count = it.count
                val maxCount = if (count > 5) 5 else count

                for (i in 0 until maxCount) {
                    if (it.moveToNext()) {
                        val id = it.getLong(it.getColumnIndexOrThrow("_id"))
                        val address = it.getString(it.getColumnIndexOrThrow("address")).toString()
                        val body = it.getString(it.getColumnIndexOrThrow("body")).toString()
                        val date = it.getString(it.getColumnIndexOrThrow("date")).toString()
                        val type = it.getInt(it.getColumnIndexOrThrow("type"))
                        val sms = Sms(id, address, body, date, type)
                        smsList.add(sms)
                    }
                }
            }
            /*
                        LogUtil.d(
                            this::class.java.simpleName,
                            "loadLast5Messages: smsList = ${smsList.size}"
                        )
            */
            return smsList
        }
    }

    private fun timeFormat(rawDate: Long): String {
        SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).apply {
            return format(rawDate)
        }
    }

}