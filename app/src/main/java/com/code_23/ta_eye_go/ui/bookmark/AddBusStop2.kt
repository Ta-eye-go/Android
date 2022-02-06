package com.code_23.ta_eye_go.ui.bookmark

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.code_23.ta_eye_go.R
import kotlinx.android.synthetic.main.activity_add_bus_stop2.*

class AddBusStop2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_bus_stop2)

        //일단 돋보기 눌렀을때 별칭 설정 페이지 보이도록 연결, 레이아웃 확인 위해
        search_btn_busstop2.setOnClickListener {
            val intent = Intent(this, AddName::class.java)
            startActivity(intent)
        }
    }
}