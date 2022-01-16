package com.code_23.ta_eye_go.util

object BotResponse {

    fun basicResponses(_message: String): String {


        return when {

            _message.contains("현재 위치") -> {
                val result = "인천대 정문"

                "현재 정류장은 $result 입니다!"
            }

            else -> "메시지 받기 성공"
        }


    }
}