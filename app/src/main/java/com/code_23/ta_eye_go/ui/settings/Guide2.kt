package com.code_23.ta_eye_go.ui.settings

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.constraintlayout.widget.Guideline
import com.code_23.ta_eye_go.R
import com.code_23.ta_eye_go.ui.main.MainActivity
import kotlinx.android.synthetic.main.activity_guide2.*
import kotlinx.android.synthetic.main.activity_guide2.next_page
import kotlinx.android.synthetic.main.menu_bar.view.*

class Guide2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guide2)
        guide_menu.menu_text.text = "간단 사용 설명서"

        // >누를시에 다음페이지로 이동
        next_page.setOnClickListener {
            val intent = Intent(this, Guide3::class.java)
            intent.addFlags (Intent.FLAG_ACTIVITY_NO_ANIMATION)//화면전환 애니메이션 제거
            startActivity(intent)
        }

        // <누를시에 이전페이지로 이동
        previous_page.setOnClickListener {
            val intent = Intent(this, Guide1::class.java)
            intent.addFlags (Intent.FLAG_ACTIVITY_NO_ANIMATION)//화면전환 애니메이션 제거
            startActivity(intent)
        }
    }
    //뒤로가기 누르면 가이드1로 이동
    override fun onBackPressed() {
        startActivity(Intent(this, Guide1::class.java))
        finish()
    }
}
