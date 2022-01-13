package com.code_23.ta_eye_go.ui.bookbus

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.code_23.ta_eye_go.R
import kotlinx.android.synthetic.main.activity_bookbus.*
import kotlinx.android.synthetic.main.activity_main.*

class ChatbotMain : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bookbus)

        // 타이핑으로 예약하기
        text_chat_btn.setOnClickListener {
            val intent = Intent(this, Chatbot_typing::class.java)
            startActivity(intent)
        }
    }
}
