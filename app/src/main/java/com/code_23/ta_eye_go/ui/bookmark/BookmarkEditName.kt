package com.code_23.ta_eye_go.ui.bookmark

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.code_23.ta_eye_go.DB.BookmarkDB
import com.code_23.ta_eye_go.R
import com.code_23.ta_eye_go.data.Favorite
import kotlinx.android.synthetic.main.activity_bookmark_edit_name.*
import kotlinx.android.synthetic.main.activity_bookmark_edit_name.bus_end
import kotlinx.android.synthetic.main.activity_bookmark_edit_name.bus_number
import kotlinx.android.synthetic.main.activity_bookmark_edit_name.bus_start
import kotlinx.android.synthetic.main.bookmark_item.*
import kotlinx.android.synthetic.main.menu_bar.view.*

class BookmarkEditName : AppCompatActivity() {

    lateinit var selected: Favorite

    // BookmarkDB
    private var bookmarkDB : BookmarkDB? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bookmark_edit_name)
        nickname_menu.menu_text.text = "별칭 수정"

        btn_cancel.setOnClickListener{
            startActivity(Intent(this, BookmarkMain::class.java))
            finish()
        }

        bookmarkDB = BookmarkDB.getInstance(this)

        selected = intent.getSerializableExtra("selected") as Favorite
        Log.d("즐찾 별칭 수정", selected.toString())
        bus_number.text = selected.busNm
        bus_start.text = selected.startSttnNm
        bus_end.text = selected.destination

        btn_complete.setOnClickListener {
            //bookmark textview가 변경되도록
            val text = edt_name.text.toString()
            Log.d("즐찾 별칭 수정", selected.toString())
            bookmarkDB?.bookmarkDao()?.updateBookmark(selected.favoriteNm, text)
            val intent = Intent(this, BookmarkMain::class.java)
            intent.putExtra("Data", text)
            setResult(Activity.RESULT_OK, intent)
            finish()
            startActivity(intent)
        }
    }
}
