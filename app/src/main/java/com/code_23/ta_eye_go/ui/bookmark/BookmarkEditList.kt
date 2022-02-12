package com.code_23.ta_eye_go.ui.bookmark

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
        bookmark_menu.menu_text.text = "즐겨찾기"

        //삭제 버튼 눌렀을때 실행
        btn_delete.setOnClickListener {
            showSettingPopup()
        }
    }

    //팝업창으로 사용한 xml들 : popup_shape(팝업창모양) , popup(팝업창 사용자화), bookmark_delete

    //삭제 눌렀을때 나올 함수 선언
    private fun showSettingPopup(){
        val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.popup, null)

        //팝업창 제목과 이름
        val textView: TextView = view.findViewById(R.id.textView)
        textView.text="<즐겨찾기 삭제>"
        val textView2: TextView = view.findViewById(R.id.textView2)
        textView2.text = "삭제하시겠습니까?"

        //팝업창 설정
        val alertDialog = AlertDialog.Builder(this)
            .create()

        //"예" 눌렀을때 팝업창 띄워주는 형식으로 일단 설정, 삭제되는 액션 안에 넣어주면 됨
        val btn_yes = view.findViewById<Button>(R.id.btn_yes)
        btn_yes.setOnClickListener{
            Toast.makeText(applicationContext, "삭제되었습니다", Toast.LENGTH_SHORT).show()
            alertDialog.dismiss()
        }

        //"아니오" 눌렀을때 -> 변화없음(dismiss)
        val btn_no = view.findViewById<Button>(R.id.btn_no)
        btn_no.setOnClickListener{
            alertDialog.dismiss()
        }

        alertDialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)) //팝업창 모양설정
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE) //팝업창 타이틀바 제거
        alertDialog.setCancelable(false) //팝업창 바깥 눌렀을때 종료되지 않도록
        alertDialog.setView(view)
        alertDialog.show()
    }
}