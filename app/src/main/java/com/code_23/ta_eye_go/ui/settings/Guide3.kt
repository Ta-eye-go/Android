package com.code_23.ta_eye_go.ui.settings

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import com.code_23.ta_eye_go.R
import com.code_23.ta_eye_go.ui.main.MainActivity
import kotlinx.android.synthetic.main.activity_guide3.*
import kotlinx.android.synthetic.main.menu_bar.*
import kotlinx.android.synthetic.main.menu_bar.view.*

class Guide3 : AppCompatActivity() {
    var x1 : Float = 0.0f
    var x2 : Float = 0.0f
    var y1 : Float = 0.0f
    var y2 : Float = 0.0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guide3)
        guide_menu.menu_text.text = "간단 사용 설명서"

        // < 누를시에 이전페이지로 이동
        previous_page.setOnClickListener {
            val intent = Intent(this, Guide2::class.java)
            startActivity(intent)
            finish()
        }
        back_btn.setOnClickListener {
            startActivity(Intent(this, Settings::class.java))
            finish()
        }
    }
    // 화면전환 애니메이션 제거
    override fun onStart() {
        super.onStart()
        overridePendingTransition(0, 0)
    }
    // 핸드폰의 뒤로가기 누르면 설정으로 이동
    override fun onBackPressed() {
        startActivity(Intent(this, Settings::class.java))
        finish()
    }
    // 옆으로 넘기기 모션
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                x1 = event.x
                y1 = event.y
            }
            MotionEvent.ACTION_UP -> {
                x2 = event.x
                y2 = event.y
                if (x1 < x2) {
                    val intent = Intent(this@Guide3, Guide2::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }
        return false
    }
}