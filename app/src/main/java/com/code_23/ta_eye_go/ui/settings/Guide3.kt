package com.code_23.ta_eye_go.ui.settings

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.code_23.ta_eye_go.R
import com.code_23.ta_eye_go.ui.main.MainActivity
import kotlinx.android.synthetic.main.activity_guide3.*
import kotlinx.android.synthetic.main.menu_bar.view.*

class Guide3 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guide3)
        guide_menu.menu_text.text = "간단 사용 설명서"

        // <누를시에 이전페이지로 이동
        previous_page.setOnClickListener {
            val intent = Intent(this, Guide2::class.java)
            intent.addFlags (Intent.FLAG_ACTIVITY_NO_ANIMATION)//화면전환 애니메이션 제거
            startActivity(intent)
        }
    }
    // 핸드폰의 뒤로가기 누르면 메인액티비티로 이동
    override fun onBackPressed() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}