package com.code_23.ta_eye_go.data

data class ChatMessage(
    var message: String,
    var isReceived: Boolean // True : bot, False : user
)