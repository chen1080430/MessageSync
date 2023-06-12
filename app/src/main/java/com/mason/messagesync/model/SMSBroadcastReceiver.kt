package com.mason.messagesync.model

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.provider.Telephony
import android.telephony.SmsMessage
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.mason.messagesync.util.LogUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class SMSBroadcastReceiver : BroadcastReceiver() {
    private var context: Context? = null
    private var smsList = mutableListOf<Sms>()

    override fun onReceive(context: Context?, intent: Intent?) {
        LogUtil.i(
            Companion.TAG,
            "onReceive: intent: ${intent.toString()} , this: $this"
        )
        this.context = context
        intent?.takeIf { it -> it.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION }
            ?.apply {
                context?.let {
                    val bundle = extras
                    bundle?.let {
                        val pdus = bundle["pdus"] as Array<*>
                        for (i in pdus.indices) {
                            val smsMessage = pdus[i] as ByteArray
                            val format = bundle.getString("format")
                            val message = if (format == null) {
                                SmsMessage.createFromPdu(smsMessage)
                            } else {
                                SmsMessage.createFromPdu(smsMessage, format)
                            }
                            val messageBody = message.messageBody
                            val messageFrom = message.originatingAddress
                            LogUtil.d(
                                this::class.java.simpleName,
                                "onReceive: SMS: messageFrom = $messageFrom" +
                                        " , displayOriginatingAddress: ${message.displayOriginatingAddress} " +
                                        " , timestampMillis: ${timeFormat(message.timestampMillis)}"
                            )
//                        messageBody?.let {
//                            messageFrom?.let {
//                                sendSMSAsNotification(message)
//
//                            }
//                        }


                            val smsTag = "sms_${message.timestampMillis}_${messageFrom}"
                            var inputData = Data.Builder()
                                .putString(MESSAGE_BODY_KEY, messageBody)
                                .putString(MESSAGE_ADDRESS_KEY, messageFrom)
                                .putString(
                                    MESSAGE_TIMESTAMP_KEY,
                                    message.timestampMillis.toString()
                                )
                                .build()
                            var constraints = Constraints.Builder()
                                .setRequiredNetworkType(androidx.work.NetworkType.CONNECTED)
                                .build()
                            val smsWorkRequest: WorkRequest =
                                OneTimeWorkRequestBuilder<SMSWorker>()
                                    .setConstraints(constraints)
                                    .setInputData(inputData)
                                    .addTag(smsTag)
                                    .build()
                            WorkManager.getInstance(context).getWorkInfosByTag(smsTag).let {
                                var size = it.get().size
                                LogUtil.d(TAG, "XXXXX> onReceive: workInfo size: $size")
                                if ((size == 0)) {
                                    WorkManager.getInstance(context).enqueue(smsWorkRequest)
                                }
                            }

                        }
                    }
                }
            }

    }

    private fun sendSMSAsNotification(message: SmsMessage) =
        CoroutineScope(Dispatchers.IO).launch {
            var completeSms = getCompleteMessage(message)
            LogUtil.d(
                this::class.java.simpleName,
                "sendSMSAsNotification: completeSms: $completeSms"
            )


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
                        Companion.TAG,
                        "sendSMSAsNotification: sendMessage.ok: ${sendMessage.ok}" +
                                "\n sendMessage.result = ${sendMessage.result}"
                    )
                } catch (e: Throwable) {
                    LogUtil.e(Companion.TAG, "sendSMSAsNotification: e: $e")
                }
            }

        }

    private suspend fun getCompleteMessage(message: SmsMessage): Sms? {
        loadLast5Messages()
        smsList.forEach { sms ->
            message.originatingAddress.takeIf { address -> sms.address == address }
                ?.let { address1 ->
                    message.messageBody.takeIf { body -> sms.body.contains(body) }?.let {
                        LogUtil.d(
                            this::class.java.simpleName,
                            "getCompleteMessage: body same! return this sms"
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
            LogUtil.d(
                this::class.java.simpleName,
                "loadLast5Messages: smsList = ${smsList.size}"
            )
            return smsList
        }
    }

    private fun timeFormat(rawDate: Long): String {
        SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).apply {
            return format(rawDate)
        }
    }

    companion object {
        private const val TAG = "SMSBroadcastReceiver"

        val MESSAGE_BODY_KEY = "message_body"
        val MESSAGE_ADDRESS_KEY = "message_address"
        val MESSAGE_TIMESTAMP_KEY = "message_timestamp"
    }
}