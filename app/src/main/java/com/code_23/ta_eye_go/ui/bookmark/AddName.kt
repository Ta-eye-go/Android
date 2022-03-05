package com.code_23.ta_eye_go.ui.bookmark

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import com.code_23.ta_eye_go.R
import com.code_23.ta_eye_go.data.Favorite
import com.code_23.ta_eye_go.ui.main.MainActivity
import kotlinx.android.synthetic.main.activity_add_name.*
import kotlinx.android.synthetic.main.alertdialog_item.view.*
import kotlinx.android.synthetic.main.menu_bar.*
import kotlinx.android.synthetic.main.menu_bar.view.*

class AddName : AppCompatActivity() {

    lateinit var newFavorite: Favorite

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_name)
        new_name_menu.menu_text.text = "별칭 설정"

        newFavorite = intent.getSerializableExtra("newFavorite") as Favorite
        bus_number.text = newFavorite.busNm
        bus_start.text = newFavorite.startSttnNm
        bus_end.text = newFavorite.destination

        btn_complete2.setOnClickListener {
            if (edt_name_new.text.isNotEmpty()) { // 데이터를 입력했으면
                // TODO : 새로운 즐겨찾기 DB 추가 처리
                newFavorite.favoriteNm = edt_name_new.text.toString()
                Log.d("newFavorite", newFavorite.toString())

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

    private fun confirmDialog() {
        val layoutInflater = LayoutInflater.from(this)
        val view = layoutInflater.inflate(R.layout.alertdialog_item, null)

        val alertDialog = AlertDialog.Builder(this)
            .setView(view)
            .create()

        view.menu_name.text = " "
        view.menu_content.text = "별칭 없이 추가하시겠습니까?"

        alertDialog.show()

        view.btn_yes.setOnClickListener {
            alertDialog.dismiss()
            Log.d("newFavorite", newFavorite.toString())
            val intent = Intent(this, BookmarkMain::class.java)
            startActivity(intent)
            finish()
        }
        view.btn_no.setOnClickListener {
            alertDialog.dismiss()
        }
    }
}