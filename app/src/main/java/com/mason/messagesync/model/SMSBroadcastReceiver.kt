package com.mason.messagesync.model

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.telephony.SmsMessage
import android.util.Log
import androidx.annotation.RequiresPermission

class SMSBroadcastReceiver: BroadcastReceiver() {

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
                    }
                }

        }

    }

    companion object {
        private const val TAG = "SMSBroadcastReceiver"
    }
}