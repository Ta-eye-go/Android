package com.code_23.ta_eye_go.ui.bookmark

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.code_23.ta_eye_go.DB.*
import com.code_23.ta_eye_go.R
import com.code_23.ta_eye_go.data.Favorite
import com.code_23.ta_eye_go.ui.bookbus.AfterReservation
import com.code_23.ta_eye_go.ui.main.MainActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_after_reservation.*
import kotlinx.android.synthetic.main.activity_bookmark.*
import kotlinx.android.synthetic.main.activity_bookmark_edit_name.*
import kotlinx.android.synthetic.main.activity_driver_main.*
import kotlinx.android.synthetic.main.activity_driver_reservation.view.*
import kotlinx.android.synthetic.main.alertdialog_item.view.*
import kotlinx.android.synthetic.main.bookmark_item.*
import kotlinx.android.synthetic.main.menu_bar.*
import kotlinx.android.synthetic.main.menu_bar.view.*
import kotlinx.coroutines.*
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.MalformedURLException
import java.net.URL
import kotlin.math.abs


class BookmarkMain : AppCompatActivity(), View.OnClickListener, View.OnCreateContextMenuListener {

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var lati: Double? = null
    private var long: Double? = null
    private lateinit var bookmarkAdapter: BookmarkAdapter
    private var favoriteItems = mutableListOf<Favorite>()
    private lateinit var sttnId : String
    private var selectedView: View? = null
    private lateinit var favoriteItemNm : String

    private val key = com.code_23.ta_eye_go.BuildConfig.TAGO_API_KEY
    private val addressBusLc = "http://apis.data.go.kr/1613000/ArvlInfoInqireService/getSttnAcctoSpcifyRouteBusArvlPrearngeInfoList?serviceKey=" //정류소별특정노선버스도착예정정보목록조회
    private val addressCrntSttn = "http://apis.data.go.kr/1613000/BusSttnInfoInqireService/getCrdntPrxmtSttnList?serviceKey="
    private val citycode : Int = 23 // 도시코드 (인천 : 23)
    private var routeId : String? = null // 버스의 노선 번호 ID
    private var resultCnt : Int = 2

    // BookmarkDB
    private var bookmarkDB : BookmarkDB? = null
    private var datamodelDB : DataModelDB? = null
    private var userDB : UserDB? = null
    private var recordDB : RecordDB? = null

    // private  var kakaoemail : String = "0"

    override fun onClick(v: View?) { // 짧은 클릭 (예약 화면 이동)
        val favoriteItem = favoriteItems[rv_favorites.getChildAdapterPosition(v!!)]
        favoriteItemNm = favoriteItems[rv_favorites.getChildAdapterPosition(v)].favoriteNm

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
        if (sttnId == "null") {
            Toast.makeText(applicationContext, "정류장 탐색 중...", Toast.LENGTH_LONG).show()
            initVariables()
            fetchLocation()
            CoroutineScope(Dispatchers.IO).launch {
                delay(500)
                withContext(Dispatchers.Main) {
                    val thread2 = NetworkThread2()
                    thread2.start()
                }
            }
        }

        bookmarkDB = BookmarkDB.getInstance(this)
        datamodelDB = DataModelDB.getInstance(this)
        userDB = UserDB.getInstance(this)
        recordDB = RecordDB.getInstance(this)

        bookmarkAdapter = BookmarkAdapter(this)
        rv_favorites.adapter = bookmarkAdapter
        rv_favorites.layoutManager = LinearLayoutManager(applicationContext)
        bookmarkAdapter.setOnItemClickListener(this)
        bookmarkAdapter.setOnCreateContextMenuListener(this)

//        val bookmark = Bookmark("감811", "2차풍림아이원",
//            "ICB168000584", "마전지구버스차고지" , "ICB168001345" , "78")
//        bookmarkDB?.bookmarkDao()?.insert(bookmark)

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

    private fun initVariables() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
    }

    private fun fetchLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),101)
            return
        }

        fusedLocationProviderClient.lastLocation.addOnSuccessListener {
            lati = it.latitude
            long = abs(it.longitude)
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

            Log.d("즐찾 별칭 수정", favoriteItemNm)
            bookmarkDB?.bookmarkDao()?.updateBookmark(favoriteItemNm, bookmark_name.text.toString())

        }
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            //각각 선택했을때 할 작업 설정
            R.id.menu_edit_list -> { //목록편집
                startActivity(Intent(this, BookmarkEditList::class.java))
            }

            R.id.menu_edit_name -> { //별칭수정
                val selected = favoriteItems[rv_favorites.getChildAdapterPosition(selectedView!!)]
                val intent = Intent(this,BookmarkEditName::class.java)
                intent.putExtra("selected", selected)
                resultLauncher.launch(intent)
            }

            R.id.menu_delete_list -> { //북마크삭제
                showSettingPopup(selectedView)
            }
        }
        return super.onContextItemSelected(item)
    }

    private fun showSettingPopup(v: View?) {
        val layoutInflater = LayoutInflater.from(this)
        val view = layoutInflater.inflate(R.layout.alertdialog_item, null)

        val alertDialog = AlertDialog.Builder(this)
            .setView(view)
            .create()

        view.menu_name.text = ""
        view.menu_content.text = "삭제하시겠습니까?"

        //"예" 눌렀을때 팝업창 띄워주는 형식으로 일단 설정, 삭제되는 액션 안에 넣어주면 됨
        view.btn_yes.setOnClickListener{
            favoriteItemNm = favoriteItems[rv_favorites.getChildAdapterPosition(v!!)].favoriteNm
            Log.d("즐찾 삭제 별칭", favoriteItemNm)
            removeBookmarkList(rv_favorites.getChildAdapterPosition(v))
            bookmarkDB?.bookmarkDao()?.delete(favoriteItemNm)
            Toast.makeText(applicationContext, "삭제되었습니다", Toast.LENGTH_SHORT).show()
            alertDialog.dismiss()
        }

        //"아니오" 눌렀을때 -> 변화없음(dismiss)
        view.btn_no.setOnClickListener{
            alertDialog.dismiss()
        }

        // alertDialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)) //팝업창 모양설정
        // alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE) //팝업창 타이틀바 제거
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

        // 서버
        val database = Firebase.database
        val email = Firebase.auth.currentUser?.email.toString()
        val favoriteItemList = bookmarkDB?.bookmarkDao()?.bookmarkdata(favoriteItemNm)
        val dog = userDB?.userDao()?.userdata(email)

        if (favoriteItemList != null) {
            routeId = recordDB?.recordDao()?.search(favoriteItemList[0].routeID)
            Log.d("노선id", routeId.toString())
            // 운행 미운행 확인
            CoroutineScope(Dispatchers.IO).launch {
                withContext(Dispatchers.Main) {
                    val thread = NetworkThread()
                    thread.start()
                }
            }
        }

        view.menu_name.text = ""
        view.menu_content.text = "예약하시겠습니까?"

        alertDialog.show()

        view.btn_yes.setOnClickListener {
            if (favoriteItemList != null) {
                if (favoriteItemList != null) {
                    routeId = recordDB?.recordDao()?.search(favoriteItemList[0].routeID)
                    val datamodellist = DataModel(favoriteItemList[0].endNodeID, favoriteItemList[0].endNodenm, routeId, favoriteItemList[0].routeID, favoriteItemList[0].startNodeID, favoriteItemList[0].startNodenm)
                    datamodelDB?.datamodelDao()?.insert(datamodellist)
                }
                Log.d("운행 미운행", resultCnt.toString())
                if (resultCnt == 1) {
                    // 기사용 서버에 데이터 전송
                    val driverdata = database.getReference("Driver").child(favoriteItemList[0].routeID)
                    val Todriver = booklist(Firebase.auth.currentUser!!.uid, favoriteItemList[0].startNodenm, favoriteItemList[0].endNodenm, dog)    // 현재정류장, 도착정류장, 안내견유무
                    driverdata.setValue(Todriver)
                    alertDialog.dismiss()
                    Toast.makeText(this@BookmarkMain, "승차 예약되었습니다. 승차 대기 화면으로 이동합니다.", Toast.LENGTH_SHORT).show()
                    // 예약 후 화면으로 이동
                    val intent = Intent(this, AfterReservation::class.java)
                    startActivity(intent)
                } else{
                    alertDialog.dismiss()
                    Toast.makeText(this@BookmarkMain, "현재 운행하고 있는 버스가 없습니다. 다른 버스를 예약해주세요.", Toast.LENGTH_SHORT).show()
                }
            }
        }
        view.btn_no.setOnClickListener {
            alertDialog.dismiss()
        }
    }

    private fun parsing1(urlAddress: String?) : StringBuffer {
        val url = URL(urlAddress)
        val conn = url.openConnection()
        val input = conn.getInputStream()
        val isr = InputStreamReader(input)
        val br = BufferedReader(isr)

        var str: String?
        val buf = StringBuffer()

        do {
            str = br.readLine()

            if (str != null) {
                buf.append(str)
            }
        } while (str != null)

        return buf
    }

    inner class NetworkThread2 : Thread() {
        @SuppressLint("SetTextI18n")
        override fun run() {
            val urlAddress =
                "${addressCrntSttn}${key}&gpsLati=${lati}&gpsLong=${long}&_type=json"
            Log.d("url", urlAddress)

            try {
                val buf = parsing1(urlAddress)
                val jsonObject = JSONObject(buf.toString())
                val response = jsonObject.getJSONObject("response").getJSONObject("body")
                    .getJSONObject("items")
                val item = response.getJSONArray("item")
                val iObject = item.getJSONObject(0)
                sttnId = iObject.getString("nodeid")

            } catch (e: MalformedURLException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
    }

    // json 파싱...
    inner class NetworkThread : Thread() {
        @SuppressLint("SetTextI18n")
        override fun run() {
            // 버스 정보 받아오기
            val urlAddress2 =
                "${addressBusLc}${key}&cityCode=${citycode}&nodeId=${sttnId}&routeId=${routeId}&_type=json"
            Log.d("즐겨찾기 미운행url", urlAddress2)

            try {
                val buf = parsing1(urlAddress2)
                val jsonObject = JSONObject(buf.toString())

                val totalCnt = jsonObject.getJSONObject("response").getJSONObject("body")
                    .getInt("totalCount")
                Log.d("운행 미운행2", totalCnt.toString())

                if (totalCnt == 0) {
                    resultCnt = 0
                    Log.d("미운행", resultCnt.toString())
                } else {
                    resultCnt = 1
                    Log.d("운행", resultCnt.toString())
                }
            } catch (e: MalformedURLException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
    }
}