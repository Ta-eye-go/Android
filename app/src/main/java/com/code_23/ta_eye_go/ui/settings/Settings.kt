package com.code_23.ta_eye_go.ui.settings

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.code_23.ta_eye_go.R
import com.code_23.ta_eye_go.ui.login.LoginMain
import com.kakao.sdk.user.UserApiClient
import kotlinx.android.synthetic.main.activity_settings.*

class Settings : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        //로그아웃 눌렀을때 로그아웃+팝업, 로그인 화면으로 전환
        text_logout.setOnClickListener {
            UserApiClient.instance.logout { error ->
                if (error != null) {
                    Toast.makeText(this, "로그아웃 실패 $error", Toast.LENGTH_SHORT).show()
                }else {
                    Toast.makeText(this, "로그아웃 성공", Toast.LENGTH_SHORT).show()
                }
                val intent = Intent(this, LoginMain::class.java)
                startActivity(intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP))
                finish()
            }
        }

        //간단 사용 설명서
        text_guide.setOnClickListener {
                val intent = Intent(this, Guide1::class.java)
                startActivity(intent)
        }
    }
}
