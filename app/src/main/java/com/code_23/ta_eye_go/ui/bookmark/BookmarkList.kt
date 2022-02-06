package com.code_23.ta_eye_go.ui.bookmark

import android.content.Intent
import android.os.Bundle
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.code_23.ta_eye_go.R
import kotlinx.android.synthetic.main.activity_bookmark.*
import kotlinx.android.synthetic.main.menu_bar.view.*

class BookmarkList : AppCompatActivity(){

    private lateinit var menu : ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bookmark)
        bookmark_menu.menu_text.text = "즐겨찾기"

        // + -> 신규추가 버튼 누를시 이동
        NewBtn.setOnClickListener {
            val intent = Intent(this, BookmarkNew::class.java)
            startActivity(intent)
        }

        // 메뉴버튼
        menu = findViewById(R.id.menu_btn)
        registerForContextMenu(menu) //컨텍스트 메뉴 사용 view

        menu.setOnClickListener {
            Toast.makeText(this@BookmarkList, "길게 눌러주세요", Toast.LENGTH_SHORT).show()
        }

        bookmark_name.text = intent.getStringExtra("Data")
    }

    //onCreateContextMenu 오버라이딩 -> 컨텍스트 메뉴 생성
    override fun onCreateContextMenu(
        menu: ContextMenu?,
        v: View?,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menuInflater.inflate(R.menu.menu_option, menu) //xml 리소스를 프로그래밍하기위해 객체로 변환
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            //각각 선택했을때 할 작업 설정
            R.id.menu_edit_list -> {
                startActivity(Intent(this, BookmarkEditList::class.java))
                finish()
            }
            R.id.menu_edit_name -> {
                startActivity(Intent(this, BookmarkEditName::class.java))
                finish()
            }
            R.id.menu_delete_list -> {
                startActivity(Intent(this, BookmarkEditList::class.java))
                finish()
            }
        }
        return super.onContextItemSelected(item)
    }
}