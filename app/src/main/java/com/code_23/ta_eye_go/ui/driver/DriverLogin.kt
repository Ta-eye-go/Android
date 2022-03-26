package com.code_23.ta_eye_go.ui.driver

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.KeyEvent.KEYCODE_ENTER
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.code_23.ta_eye_go.R
import kotlinx.android.synthetic.main.activity_driver_login.*

class DriverLogin : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_driver_login)

        busNm_bar.setOnKeyListener { v, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KEYCODE_ENTER) {
                // 엔터 눌렀을때 행동
                if (busNm_bar.text.isNotEmpty()) { // 데이터를 입력했으면
                    val intent = Intent(this, DriverMain::class.java)
                    val busNm : String = busNm_bar.text.toString()
                    intent.putExtra("busNm", busNm)
                    Toast.makeText(applicationContext, "로그인이 완료되었습니다.", Toast.LENGTH_SHORT).show()
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(applicationContext, "버스 번호를 확인해주세요.", Toast.LENGTH_SHORT).show()
                }
            }
            true
        }

        btn_login.setOnClickListener {
            if (busNm_bar.text.isNotEmpty()) { // 데이터를 입력했으면
                val intent = Intent(this, DriverMain::class.java)
                val busNm : String = busNm_bar.text.toString()
                intent.putExtra("busNm", busNm)
                Toast.makeText(applicationContext, "로그인이 완료되었습니다.", Toast.LENGTH_SHORT).show()
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(applicationContext, "버스 번호를 확인해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}