package com.code_23.ta_eye_go.ui.bookmark

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.code_23.ta_eye_go.R
import com.code_23.ta_eye_go.data.Favorite
import com.code_23.ta_eye_go.ui.main.MainActivity
import kotlinx.android.synthetic.main.activity_bookmark.*
import kotlinx.android.synthetic.main.activity_bookmark_edit_name.*
import kotlinx.android.synthetic.main.activity_driver_reservation.view.*
import kotlinx.android.synthetic.main.alertdialog_item.view.*
import kotlinx.android.synthetic.main.bookmark_item.*
import kotlinx.android.synthetic.main.menu_bar.*
import kotlinx.android.synthetic.main.menu_bar.view.*
import kotlinx.android.synthetic.main.menu_bar.view.menu_text

class BookmarkList : AppCompatActivity(), View.OnClickListener, View.OnCreateContextMenuListener {

    private lateinit var bookmarkAdapter: BookmarkAdapter
    private var favoriteItems = mutableListOf<Favorite>()
    private lateinit var sttnId : String

    override fun onClick(v: View?) { // 짧은 클릭 (예약 화면 이동)
        val favoriteItem = favoriteItems[rv_favorites.getChildAdapterPosition(v!!)]

        // 현재 정류장과 시작 정류장 일치 검사
        if (sttnId == favoriteItem.startSttnID) {
            // TODO : 클릭 시 예약화면으로 이동하기
            confirmDialog()
            Toast.makeText(this@BookmarkList, "예약 이동 미구현", Toast.LENGTH_SHORT).show()
        }
        else {
            Toast.makeText(this@BookmarkList, "현재 정류장이 항목과 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateContextMenu( // 긴 클릭 (메뉴 띄우기)
        menu: ContextMenu?,
        v: View?,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menuInflater.inflate(R.menu.menu_option, menu) //xml 리소스를 프로그래밍하기위해 객체로 변환
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bookmark)
        bookmark_menu.menu_text.text = "즐겨찾기"
        sttnId = intent.getStringExtra("sttnId").toString()

        bookmarkAdapter = BookmarkAdapter(this)
        rv_favorites.adapter = bookmarkAdapter
        rv_favorites.layoutManager = LinearLayoutManager(applicationContext)
        bookmarkAdapter.setOnItemClickListener(this)
        bookmarkAdapter.setOnCreateContextMenuListener(this)

        // + -> 신규추가 버튼 누를시 이동
        NewBtn.setOnClickListener {
            val intent = Intent(this, BookmarkAdd::class.java)
            startActivity(intent)
            finish()
        }

        back_btn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        //
        //var FavoriteToList =

        // 예시 즐겨찾기 항목들 , 띄어쓰기 기준으로 줄바꿈
        addFavoriteToList("신나는 하굣길", "당하대 주파크빌", "ICB168000392",
            "인천대입구", "ICB164000396", "8")
        addFavoriteToList("이름이 10글자 이상인 즐겨찾기", "산내마을3단지", "12345",
            "인천대학교 공과대학", "12345", "8")
        addFavoriteToList("인입에서해경", "인천대입구", "5678",
            "해양경찰청", "5678", "16")
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun addFavoriteToList(favoriteNm : String, startSttnNm : String, startSttnID : String,
                                  destination : String, destinationID : String, busNm : String) {

        favoriteItems.add(Favorite(favoriteNm, startSttnNm, startSttnID, destination, destinationID, busNm))
        bookmarkAdapter.insertFavorite(Favorite(favoriteNm, startSttnNm, startSttnID, destination, destinationID, busNm))
        bookmarkAdapter.notifyDataSetChanged()
        rv_favorites.scrollToPosition(0)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            //각각 선택했을때 할 작업 설정
            R.id.menu_edit_list -> {
                startActivity(Intent(this, BookmarkEditList::class.java))
            }
            R.id.menu_edit_name -> {
                startActivity(Intent(this, BookmarkEditName::class.java))
                bookmark_name.text = intent.getStringExtra("Data")
            }
            R.id.menu_delete_list -> {
                showSettingPopup()
            }
        }
        return super.onContextItemSelected(item)
    }

    private fun showSettingPopup() {
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

    override fun onBackPressed() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun confirmDialog() {
        val layoutInflater = LayoutInflater.from(this)
        val view = layoutInflater.inflate(R.layout.alertdialog_item, null)

        val alertDialog = AlertDialog.Builder(this)
            .setView(view)
            .create()

        view.menu_name.text = "<예약 확인>"
        view.menu_content.text = "예약하시겠습니까?"

        alertDialog.show()

        view.btn_yes.setOnClickListener {
            alertDialog.dismiss()
            Toast.makeText(this@BookmarkList, "예약 이동 미구현", Toast.LENGTH_SHORT).show()
        }
        view.btn_no.setOnClickListener {
            alertDialog.dismiss()
        }
    }
}