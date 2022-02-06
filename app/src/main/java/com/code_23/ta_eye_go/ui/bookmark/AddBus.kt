package com.code_23.ta_eye_go.ui.bookmark

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.code_23.ta_eye_go.R
import kotlinx.android.synthetic.main.activity_add_bus.*
import kotlinx.android.synthetic.main.menu_bar.view.*

class AddBus : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_bus)
        new_bus_menu.menu_text.text = "즐겨찾기 신규추가"

        //일단 돋보기 눌렀을때 도착정류장 설정 페이지 보이도록 연결, 레이아웃 확인 위해
        search_btn_bus.setOnClickListener {
            val intent = Intent(this, AddBusStop2::class.java)
            startActivity(intent)
        }
    }
}