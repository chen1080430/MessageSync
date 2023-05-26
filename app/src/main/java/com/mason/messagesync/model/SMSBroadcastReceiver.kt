package com.mason.messagesync.model

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.telephony.SmsMessage
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SMSBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.i(Companion.TAG, "XXXXX> onReceive: intent: ${intent.toString()}")
        //check if is sms action and print message body
        intent?.takeIf { it -> it.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION }
            ?.apply {
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
                        Log.d(
                            Companion.TAG,
                            "XXXXX> onReceive: SMS: messageBody = $messageBody, messageFrom = $messageFrom" +
                                    "\n displayMessageBody: ${message.displayMessageBody}  " +
                                    ", displayOriginatingAddress: ${message.displayOriginatingAddress} " +
                                    ", timestampMillis = ${message.timestampMillis}"
                        )
                        messageBody?.let {
                            messageFrom?.let {
                                sendSMSAsNotification(message)

                            }
                        }

                    }
                }

            }

    }

    private fun sendSMSAsNotification(message: SmsMessage) =
        CoroutineScope(Dispatchers.IO).launch {
            Log.d(Companion.TAG, "XXXXX> sendSMSAsNotification: messageBody = ${message.messageBody}, messageFrom = ${message.originatingAddress}")
            var telegramDeafultApi = RetrofitManager.INSTANCE.telegramDeafultApi
            try {


                var sendMessage = telegramDeafultApi.sendMessage(
                    RetrofitManager.INSTANCE.chatId,
                    "新簡訊來囉!\nFrom:  ${message.originatingAddress}\nMessage: ${message.messageBody}"
                )
                Log.d(
                    Companion.TAG,
                    "XXXXX> sendSMSAsNotification: sendMessage.ok: ${sendMessage.ok}\n sendMessage.result = ${sendMessage.result}"
                )
            } catch (e: Throwable) {
                Log.e(Companion.TAG, "XXXXX> sendSMSAsNotification: e: ", e)
            }

        }

    companion object {
        private const val TAG = "SMSBroadcastReceiver"
    }
}