package com.mason.messagesync.model

data class TelegramResponse(
    var ok: Boolean,
    var result: TelegramResult
)

data class TelegramResult(
    val message_id: Int,
    val from: TelegramFrom,
    val chat: TelegramChat,
    val date: Long,
    val text: String
)

data class TelegramFrom(
    val id: Long,
    val is_bot: Boolean,
    val first_name: String,
    val username: String
)

data class TelegramChat(
    val id: Long,
    val first_name: String,
    val type: String
)

data class TelegramTokenStatus(
    val ok: Boolean,
    val result: TokenUser
)

data class TokenUser(
    val id: Long,
    val is_bot: Boolean,
    val first_name: String,
    val username: String,
    val can_join_groups: Boolean,
    val can_read_all_group_messages: Boolean,
    val supports_inline_queries: Boolean
)
