package com.code_23.ta_eye_go.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.code_23.ta_eye_go.R
import kotlinx.android.synthetic.main.activity_login_main.*

class LoginMain : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_main)

        //이메일로 로그인하기
        email_btn.setOnClickListener {
            val intent = Intent(this, LoginEmail::class.java)
            startActivity(intent)
        }

    }
}