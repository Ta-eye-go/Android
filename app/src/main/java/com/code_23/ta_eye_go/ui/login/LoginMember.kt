package com.code_23.ta_eye_go.ui.login

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.code_23.ta_eye_go.R
import com.code_23.ta_eye_go.ui.main.MainActivity
import kotlinx.android.synthetic.main.activity_login_member.*

class LoginMember : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_member)

        //회원가입 -> 메인화면 이동
        member_btn.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        //이전 페이지로 돌아가기
       before_btn.setOnClickListener {
            val intent = Intent(this, LoginEmail::class.java)
            startActivity(intent)
        }

    }
}
