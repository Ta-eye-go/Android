package com.code_23.ta_eye_go.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.code_23.ta_eye_go.R
import com.code_23.ta_eye_go.ui.bookbus.ChatbotMain
import com.code_23.ta_eye_go.ui.bookmark.BookmarkList
import com.code_23.ta_eye_go.ui.main.MainActivity
import com.code_23.ta_eye_go.ui.settings.Settings
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_login_main.*
import kotlinx.android.synthetic.main.activity_main.*

class LoginMain : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_main)

        //이메일로 로그인하기
        email_btn.setOnClickListener {
            val intent = Intent(this, LoginEmail::class.java)
            startActivity(intent)
        }
        // 아직 기사용 화면은 없어서 메인화면으로 이동되게 만들어뒀음
        bus_btn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

    }
}