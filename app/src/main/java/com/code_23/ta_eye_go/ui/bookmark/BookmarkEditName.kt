package com.code_23.ta_eye_go.ui.bookmark

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.code_23.ta_eye_go.R
import kotlinx.android.synthetic.main.activity_bookmark_edit_name.*

class BookmarkEditName : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bookmark_edit_name)

        btn_cancel.setOnClickListener{
            startActivity(Intent(this, BookmarkList::class.java))
            finish()
        }

        btn_complete.setOnClickListener {
            //bookmark textview가 변경되도록
            val text = edt_name.text.toString()
            val intent = Intent(this, BookmarkList::class.java)
            intent.putExtra("Data", text)
            startActivity(intent)

        }
    }
    //뒤로가기 버튼 비활성화
    override fun onBackPressed() {
        //super.onBackPressed()
    }
}