package com.mason.messagesync.util

import android.util.Log
import com.mason.messagesync.BuildConfig
import kotlin.math.log

class LogUtil {
    init {
        Log.v("LogUtil", "XXXXX> : BuildConfig.DEBUG: ${BuildConfig.DEBUG} \nLOG_TYPE: $LOG_TYPE")
    }

    companion object{
        private val LOGTAG = "${LogUtil::class.simpleName}: "
        private val PREFIX = "XXXXX> "
        private val LOG_TYPE = BuildConfig.DEBUG

        fun d(tag: String, msg: String) {
            if (!LOG_TYPE) return
            Log.d(LOGTAG + tag, "${PREFIX} $msg")
        }

        fun i(tag: String, msg: String) {
            if (!LOG_TYPE) return
            Log.i(LOGTAG + tag, "${PREFIX} $msg")
        }

        fun e(tag: String, msg: String) {
            if (!LOG_TYPE) return
            Log.e(LOGTAG + tag, "${PREFIX} $msg")
        }
    }
}