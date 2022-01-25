package com.code_23.ta_eye_go.ui.settings

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.code_23.ta_eye_go.R
import kotlinx.android.synthetic.main.activity_guide2.*
import kotlinx.android.synthetic.main.activity_guide2.next_page

class Guide2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guide1)

        //<누르면 이전페이지 이동
        previous_page.setOnClickListener {
            val intent = Intent(this, Guide1::class.java)
            startActivity(intent)
            //overridePendingTransition(0, 0)
        }

        //>누르면 다음페이지 이동
        next_page.setOnClickListener {
            val intent = Intent(this, Guide3::class.java)
            startActivity(intent)
            //overridePendingTransition(0, 0)
        }
    }
}
