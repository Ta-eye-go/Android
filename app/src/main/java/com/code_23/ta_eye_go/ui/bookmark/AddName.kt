package com.code_23.ta_eye_go.ui.bookmark

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import com.code_23.ta_eye_go.DB.Bookmark
import com.code_23.ta_eye_go.DB.BookmarkDB
import com.code_23.ta_eye_go.R
import com.code_23.ta_eye_go.data.Favorite
import kotlinx.android.synthetic.main.activity_add_name.*
import kotlinx.android.synthetic.main.alertdialog_item.view.*
import kotlinx.android.synthetic.main.menu_bar.*
import kotlinx.android.synthetic.main.menu_bar.view.*

class AddName : AppCompatActivity() {

    lateinit var newFavorite: Favorite

    // BookmarkDB
    private var bookmarkDB : BookmarkDB? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_name)
        new_name_menu.menu_text.text = "별칭 설정"

        bookmarkDB = BookmarkDB.getInstance(this)

        newFavorite = intent.getSerializableExtra("newFavorite") as Favorite
        bus_number.text = newFavorite.busNm
        bus_start.text = newFavorite.startSttnNm
        bus_end.text = newFavorite.destination

        btn_complete2.setOnClickListener {
            if (edt_name_new.text.isNotEmpty()) { // 데이터를 입력했으면

                newFavorite.favoriteNm = edt_name_new.text.toString()
                Log.d("newFavorite", newFavorite.toString())

                val bookmark = Bookmark(newFavorite.favoriteNm, newFavorite.startSttnNm,
                    newFavorite.startSttnID, newFavorite.destination , newFavorite.destinationID , newFavorite.busNm)
                bookmarkDB?.bookmarkDao()?.insert(bookmark)
                Toast.makeText(applicationContext, "즐겨찾기가 추가되었습니다.", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, BookmarkMain::class.java)
                startActivity(intent)
                finish()
            } else {
                confirmDialog()
            }
        }

        back_btn.setOnClickListener {
            val intent = Intent(this, BookmarkNew::class.java)
            startActivity(intent)
            finish()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun confirmDialog() {
        val layoutInflater = LayoutInflater.from(this)
        val view = layoutInflater.inflate(R.layout.alertdialog_item, null)

        val alertDialog = AlertDialog.Builder(this)
            .setView(view)
            .create()

        view.menu_name.text = ""
        view.menu_content.text = "별칭을 입력하신 후\n완료 버튼을 눌러주세요."
        view.btn_no.visibility = View.GONE

        alertDialog.show()

        view.btn_yes.setOnClickListener {
            alertDialog.dismiss()
        }
    }
}