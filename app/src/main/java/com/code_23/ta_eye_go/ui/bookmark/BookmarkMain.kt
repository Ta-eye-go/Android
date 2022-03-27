package com.code_23.ta_eye_go.ui.bookmark

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.code_23.ta_eye_go.DB.BookmarkDB
import com.code_23.ta_eye_go.DB.DataModel
import com.code_23.ta_eye_go.DB.DataModelDB
import com.code_23.ta_eye_go.R
import com.code_23.ta_eye_go.data.Favorite
import com.code_23.ta_eye_go.ui.bookbus.AfterReservation
import com.code_23.ta_eye_go.ui.bookbus.InBus
import com.code_23.ta_eye_go.ui.driver.BookerAdapter
import com.code_23.ta_eye_go.ui.main.MainActivity
import kotlinx.android.synthetic.main.activity_bookmark.*
import kotlinx.android.synthetic.main.activity_bookmark_edit_name.*
import kotlinx.android.synthetic.main.activity_driver_main.*
import kotlinx.android.synthetic.main.activity_driver_reservation.view.*
import kotlinx.android.synthetic.main.alertdialog_item.view.*
import kotlinx.android.synthetic.main.bookmark_item.*
import kotlinx.android.synthetic.main.menu_bar.*
import kotlinx.android.synthetic.main.menu_bar.view.*
import kotlinx.android.synthetic.main.menu_bar.view.menu_text

class BookmarkMain : AppCompatActivity(), View.OnClickListener, View.OnCreateContextMenuListener {

    private lateinit var bookmarkAdapter: BookmarkAdapter
    private var favoriteItems = mutableListOf<Favorite>()
    private lateinit var sttnId : String
    private var selectedView: View? = null
    private lateinit var favoriteItemNm : String

    // BookmarkDB
    private var bookmarkDB : BookmarkDB? = null
    private var datamodelDB : DataModelDB? = null

    override fun onClick(v: View?) { // 짧은 클릭 (예약 화면 이동)
        val favoriteItem = favoriteItems[rv_favorites.getChildAdapterPosition(v!!)]
        favoriteItemNm = favoriteItems[rv_favorites.getChildAdapterPosition(v!!)].favoriteNm

        // 현재 정류장과 시작 정류장 일치 검사
        if (sttnId == favoriteItem.startSttnID) {
            confirmDialog()
        }
        else {
            Toast.makeText(this@BookmarkMain, "현재 정류장이 항목과 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateContextMenu( // 긴 클릭 (메뉴 띄우기)
        menu: ContextMenu?,
        v: View?,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        selectedView = v
        menuInflater.inflate(R.menu.menu_option, menu) //xml 리소스를 프로그래밍하기위해 객체로 변환
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bookmark)
        bookmark_menu.menu_text.text = "즐겨찾기"
        sttnId = intent.getStringExtra("sttnId").toString()

        bookmarkDB = BookmarkDB.getInstance(this)
        datamodelDB = DataModelDB.getInstance(this)

        bookmarkAdapter = BookmarkAdapter(this)
        rv_favorites.adapter = bookmarkAdapter
        rv_favorites.layoutManager = LinearLayoutManager(applicationContext)
        bookmarkAdapter.setOnItemClickListener(this)
        bookmarkAdapter.setOnCreateContextMenuListener(this)

        // + -> 신규추가 버튼 누를시 이동
        NewBtn.setOnClickListener {
            val intent = Intent(this, BookmarkNew::class.java)
            startActivity(intent)
            finish()
        }

        back_btn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        // 즐겨찾기 adapter에 DB연결
        val bookmarklist = bookmarkDB?.bookmarkDao()?.getAll()
        if (bookmarklist != null){
            for (index in bookmarklist.indices){
                addFavoriteToList(bookmarklist[index].favoriteNm ,bookmarklist[index].startNodenm,bookmarklist[index].startNodeID,
                    bookmarklist[index].endNodenm,bookmarklist[index].endNodeID,bookmarklist[index].routeID)
            }
        }else{
            Log.d("realtime db", "수정이한테 연락바람")
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun addFavoriteToList(favoriteNm : String, startSttnNm : String, startSttnID : String,
                                  destination : String, destinationID : String, busNm : String) {

        favoriteItems.add(Favorite(favoriteNm, startSttnNm, startSttnID, destination, destinationID, busNm))
        bookmarkAdapter.insertFavorite(Favorite(favoriteNm, startSttnNm, startSttnID, destination, destinationID, busNm))
        bookmarkAdapter.notifyDataSetChanged()
        rv_favorites.scrollToPosition(0)
    }

    //별칭 수정하는 이벤트 (안에서 호출하면 오류)
    val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult())
    { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            bookmark_name.text = result.data?.getStringExtra("Data")
        }
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            //각각 선택했을때 할 작업 설정
            R.id.menu_edit_list -> { //목록편집
                startActivity(Intent(this, BookmarkEditList::class.java))
            }

            R.id.menu_edit_name -> { //별칭수정
                val intent = Intent(this,BookmarkEditName::class.java)
                resultLauncher.launch(intent)
            }

            R.id.menu_delete_list -> { //북마크삭제
                showSettingPopup(selectedView)
            }
        }
        return super.onContextItemSelected(item)
    }

    private fun showSettingPopup(v: View?) {
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
            favoriteItemNm = favoriteItems[rv_favorites.getChildAdapterPosition(v!!)].favoriteNm
            Log.d("즐찾 삭제 별칭", favoriteItemNm)
            removeBookmarkList(rv_favorites.getChildAdapterPosition(v!!))
            bookmarkDB?.bookmarkDao()?.delete(favoriteItemNm)
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

    //즐겨찾기 삭제
    private fun removeBookmarkList(position : Int) {
        bookmarkAdapter.removeBookmark(position)
        favoriteItems.removeAt(position)
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
            val favoriteItemList = bookmarkDB?.bookmarkDao()?.bookmarkdata(favoriteItemNm)
            if (favoriteItemList != null) {
                val datamodellist = DataModel(favoriteItemList[0].endNodeID,favoriteItemList[0].endNodenm," "
                    ,favoriteItemList[0].routeID,favoriteItemList[0].startNodeID,
                    favoriteItemList[0].startNodenm)
                datamodelDB?.datamodelDao()?.insert(datamodellist)
            }
            alertDialog.dismiss()
            Toast.makeText(this@BookmarkMain, "승차 예약되었습니다. 승차 대기 화면으로 이동합니다.", Toast.LENGTH_SHORT).show()
            // 예약 후 화면 이동
            val intent = Intent(this, AfterReservation::class.java)
            startActivity(intent)
        }
        view.btn_no.setOnClickListener {
            alertDialog.dismiss()
        }
    }
}