package com.code_23.ta_eye_go.ui.settings

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.code_23.ta_eye_go.R
import kotlinx.android.synthetic.main.activity_guide1.*

class Guide1 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guide1)

        // >누를시에 다음페이지로 이동
        next_page.setOnClickListener {
            val intent = Intent(this, Guide2::class.java)
            startActivity(intent)
        }
    }
}