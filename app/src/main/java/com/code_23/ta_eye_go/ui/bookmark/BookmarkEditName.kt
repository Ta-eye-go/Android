package com.code_23.ta_eye_go.ui.bookmark

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.code_23.ta_eye_go.DB.Bookmark
import com.code_23.ta_eye_go.DB.BookmarkDB
import com.code_23.ta_eye_go.R
import com.code_23.ta_eye_go.data.Favorite
import com.code_23.ta_eye_go.data.ReservationData
import kotlinx.android.synthetic.main.activity_add_name.*
import kotlinx.android.synthetic.main.activity_bookmark_edit_name.*
import kotlinx.android.synthetic.main.activity_bookmark_edit_name.bus_end
import kotlinx.android.synthetic.main.activity_bookmark_edit_name.bus_number
import kotlinx.android.synthetic.main.activity_bookmark_edit_name.bus_start
import kotlinx.android.synthetic.main.activity_bookmark_new.*
import kotlinx.android.synthetic.main.alertdialog_item.view.*
import kotlinx.android.synthetic.main.menu_bar.view.*
import org.w3c.dom.Text

class BookmarkEditName : AppCompatActivity() {

    lateinit var newFavorite: Favorite

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

//        bookmarkDB = BookmarkDB.getInstance(this)
//
//        newFavorite = intent.getSerializableExtra("Favorite") as Favorite
//        bus_number.text = newFavorite.busNm
//        bus_start.text = newFavorite.startSttnNm
//        bus_end.text = newFavorite.destination

        btn_complete.setOnClickListener {
            //bookmark textview가 변경되도록
            val text = edt_name.text.toString()
            val intent = Intent(this, BookmarkMain::class.java)
            intent.putExtra("Data", text)
            setResult(Activity.RESULT_OK, intent)
            finish()
            startActivity(intent)
        }
    }
}
