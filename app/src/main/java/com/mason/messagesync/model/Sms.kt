package com.mason.messagesync.model

data class Sms(val id: Long, val address: String, val body: String, val date: String, val type: Int) {
    constructor() : this(0, "", "", "", 0)

}
