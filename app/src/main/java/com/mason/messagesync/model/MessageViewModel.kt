package com.mason.messagesync.model

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.mason.messagesync.util.LogUtil
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MessageViewModel(application: Application) : AndroidViewModel(application) {
    companion object {
        private const val TAG = "MessageViewModel"
    }

    val loading: LiveData<Boolean>
        get() = _loading

    private var _loading: MutableLiveData<Boolean> = MutableLiveData()
    private val context = application

    private val _smsList: MutableLiveData<MutableList<Sms>> = MutableLiveData(mutableListOf())

    val smsListLiveData: LiveData<MutableList<Sms>>
        get() = _smsList

    fun loadSMS() {
        launchDataLoad {
            loadAllSms()
        }
    }

    private suspend fun loadAllSms() {
        val cursor = context.contentResolver.query(
            Uri.parse("content://sms/"),
            null,
            null,
            null,
            "date DESC"
        )
        cursor?.let {
            var smslist = _smsList.value
            smslist?.let { smslist ->

                smslist.clear()
                while (it.moveToNext()) {
                    val id = it.getLong(it.getColumnIndexOrThrow("_id"))
                    val address = it.getString(it.getColumnIndexOrThrow("address")).toString()
                    val body = it.getString(it.getColumnIndexOrThrow("body")).toString()
                    val date = it.getString(it.getColumnIndexOrThrow("date")).toString()
                    val type = it.getInt(it.getColumnIndexOrThrow("type"))
                    val sms = Sms(id, address, body, date, type)
                    smslist.add(sms)
                }
                it.close()
                LogUtil.d(TAG, "XXXXX> readAllSms: smsAdapter.itemCound = ${smslist.size}")
                _smsList.postValue(smslist)
            }
        }

    }

    fun launchDataLoad(block: suspend () -> Unit): Job {
        return viewModelScope.launch {
            try {
                _loading.value = true
                block.invoke()
            } catch (e: Throwable) {
                LogUtil.d(Companion.TAG, "XXXXX> launchDataLoad: e: $e")
            } finally {
                _loading.value = false
            }
        }
    }
}

class MessageViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MessageViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MessageViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

