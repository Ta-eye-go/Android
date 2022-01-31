package com.code_23.ta_eye_go.ui.bookbus

data class ChatMessage(
    var message: String,
    var isReceived: Boolean // True : bot, False : user
)