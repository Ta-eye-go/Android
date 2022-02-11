package com.code_23.ta_eye_go.ui.settings

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.code_23.ta_eye_go.R
import com.code_23.ta_eye_go.ui.main.MainActivity
import kotlinx.android.synthetic.main.activity_add_name.*
import kotlinx.android.synthetic.main.activity_guide1.*
import kotlinx.android.synthetic.main.menu_bar.view.*

class Guide1 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guide1)
        guide_menu.menu_text.text = "간단 사용 설명서"

        // >누를시에 다음페이지로 이동
        next_page.setOnClickListener {
            val intent = Intent(this, Guide2::class.java)
            intent.addFlags (Intent.FLAG_ACTIVITY_NO_ANIMATION) //화면전환 애니메이션 제거
            startActivity(intent)
        }
    }
}