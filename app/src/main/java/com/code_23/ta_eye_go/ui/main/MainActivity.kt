package com.code_23.ta_eye_go.ui.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.code_23.ta_eye_go.R
import com.code_23.ta_eye_go.ui.bookbus.ChatbotMain
import com.code_23.ta_eye_go.ui.bookmark.BookmarkList
import com.code_23.ta_eye_go.ui.login.LoginEmail
import com.code_23.ta_eye_go.ui.settings.Settings
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 예약 창 이동
        bookBusBtn.setOnClickListener {
            val intent = Intent(this, ChatbotMain::class.java)
            startActivity(intent)
        }
        // 설정 창 이동
        settingBtn.setOnClickListener {
            val intent = Intent(this, Settings::class.java)
            startActivity(intent)
        }
        // 즐겨찾기 창 이동
        bookmarkBtn.setOnClickListener {
            val intent = Intent(this, BookmarkList::class.java)
            startActivity(intent)
        }
        // 현 위치 새로고침
        refreshBtn.setOnClickListener {

        }
    }


}
