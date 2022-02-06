package com.code_23.ta_eye_go.ui.bookmark

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.code_23.ta_eye_go.R
import kotlinx.android.synthetic.main.activity_add_bus_stop.*
import kotlinx.android.synthetic.main.activity_add_name.*
import kotlinx.android.synthetic.main.menu_bar.view.*

class AddName : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_name)
        new_name_menu.menu_text.text = "별칭 설정"

        //완료 버튼 누르면 토스트 메시지
        btn_complete2.setOnClickListener {
            Toast.makeText(applicationContext, "추가되었습니다", Toast.LENGTH_SHORT).show()
            finish() //finish 대신에 완료됐을때 이동해야하는 레이아웃 추가해야함
        }
    }
}