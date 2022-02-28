package com.code_23.ta_eye_go.ui.bookmark

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Window
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.code_23.ta_eye_go.R
import kotlinx.android.synthetic.main.activity_bookmark_edit_list.*
import kotlinx.android.synthetic.main.menu_bar.view.*

class BookmarkEditList : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bookmark_edit_list)
        bookmark_edit_menu.menu_text.text = "즐겨찾기"

    }
}