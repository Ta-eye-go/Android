package com.code_23.ta_eye_go.ui.main

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.code_23.ta_eye_go.BuildConfig
import com.code_23.ta_eye_go.DB.*
import com.code_23.ta_eye_go.R
import com.code_23.ta_eye_go.ui.bookbus.ChatbotMainActivity
import com.code_23.ta_eye_go.ui.bookmark.BookmarkMain
import com.code_23.ta_eye_go.ui.settings.Settings
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.kakao.sdk.user.UserApiClient
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.alertdialog_item.view.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import org.json.JSONObject
import org.json.JSONException
import java.io.IOException
import java.net.MalformedURLException
import kotlin.math.abs


class MainActivity : AppCompatActivity() {

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var lati: Double? = null
    private var long: Double? = null
    private var citycode: String = "23" // 도시코드
    var currentStation: String? = null  // 현재정류장
    var sttnId: String? = null  // 현재정류장 id

    var sttnNo: String? = "새로고침을 눌러주세요"
    private val key = BuildConfig.TAGO_API_KEY
    private val address = "http://apis.data.go.kr/1613000/BusSttnInfoInqireService/getCrdntPrxmtSttnList?serviceKey="

    // firebase DB
    val database = Firebase.database
    // Room DB
    private var userDB : UserDB? = null
    private var recordDB : RecordDB? = null
    private var datamodelDB : DataModelDB? = null
    private var bookmarkDB : BookmarkDB? = null
    private var driverDB : DriverDB? = null

    private  var kakaoemail : String = "0"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Room DB
        userDB = UserDB.getInstance(this)
        recordDB = RecordDB.getInstance(this)
        datamodelDB = DataModelDB.getInstance(this)
        bookmarkDB = BookmarkDB.getInstance(this)
        driverDB = DriverDB.getInstance(this)

        // 수정이가 넣으라고 한거 (한번 사용했으면 꺼두기, datamodelDB는 건들지말기)
        userDB?.userDao()?.deleteAll()    // 유저 DB 초기화
        datamodelDB?.datamodelDao()?.deleteAll()    // 예약리스트(일회용) DB 초기화
        //recordDB?.recordDao()?.deleteAll()    // 최근경로 DB 초기화
        //bookmarkDB?.bookmarkDao()?.deleteAll()    // 즐겨찾기 DB 초기화
        driverDB?.driverDao()?.deleteAll()    // 기사용 DB 초기화

        initVariables()
        fetchLocation()

        // 예약 창 이동
        bookBusBtn.setOnClickListener {
            // 예약 초기 데이터 리스트 realtime DB로 전송
            datamodelDB?.datamodelDao()?.deleteAll()
            val email = Firebase.auth.currentUser?.email.toString()
            // 카카오 유저
            if (kakaoemail != "0") {
                val userdata = userDB?.userDao()?.userdata(kakaoemail)!!
                val bookdata = database.getReference("data").child("Kakao")
                val currentLoc = ListForm(citycode,"","", userdata,kakaoemail,"","",sttnId,currentStation)
                bookdata.setValue(currentLoc)
            } else {    // 구글 유저
                val userdata = userDB?.userDao()?.userdata(email)!!
                val bookdata = database.getReference("data").child(Firebase.auth.currentUser!!.uid)
                val uid = database.getReference("uid")
                uid.setValue(Firebase.auth.currentUser!!.uid)
                val currentLoc = ListForm(citycode,"","", userdata,email,"","",sttnId,currentStation)
                bookdata.setValue(currentLoc)
            }
//            val uid = database.getReference("uid")
//            uid.setValue(Firebase.auth.currentUser!!.uid)
//            //val currentLoc = ListForm(citycode,"ICB168000377","독정역(서구동구예비군훈련장)", userdata,email,"ICB165000055","87",sttnId,currentStation)
//            val currentLoc = ListForm(citycode,"","", userdata,email,"","",sttnId,currentStation)
//            //database.child("users").child(email).setValue(currentLoc)
//            bookdata.setValue(currentLoc)
            val intent = Intent(this, ChatbotMainActivity::class.java)
            intent.putExtra("currentStation", currentStation)
            intent.putExtra("sttnId", sttnId)
            startActivity(intent)
            finish()
        }
        // 설정 창 이동
        settingBtn.setOnClickListener {
            val intent = Intent(this, Settings::class.java)
            startActivity(intent)
            finish()
        }
        // 즐겨찾기 창 이동
        bookmarkBtn.setOnClickListener {
            val intent = Intent(this, BookmarkMain::class.java)
            intent.putExtra("sttnId", sttnId)
            startActivity(intent)
            finish()
        }
        // 현 위치 새로고침
        refreshBtn.setOnClickListener {
            fetchLocation()
            fetchCurrentStation()
        }
        //사용자 정보 가져오기 (Logcat에서 확인가능, 한번 동의하면 그 뒤로 동의메시지 안뜸)
        UserApiClient.instance.me { user, error ->
            if (error != null) {
                Log.e(ContentValues.TAG, "사용자 정보 요청 실패", error)
                Log.d("kakao_email2", user?.kakaoAccount?.email.toString())
            }
            else if (user != null) {
                Log.d("kakao_email", user.kakaoAccount?.email.toString())
                kakaoemail = user.kakaoAccount?.email.toString()
                val users = User(user.kakaoAccount?.email.toString(), false)
                userDB?.userDao()?.insert(users)
            }
        }
        // 로그인한 유저 DB등록
        val r = Runnable {
            try {
                val users = User(Firebase.auth.currentUser?.email.toString(), false)
                userDB?.userDao()?.insert(users)
            } catch (e: Exception) {
                Log.d("tag", "Error - $e")
            }
        }
        val thread = Thread(r)
        thread.start()
    }

    override fun onDestroy() {
        UserDB.destroyInstance()
        userDB = null
        super.onDestroy()
    }

    // 뒤로 가기 버튼 종료 액션
    override fun onBackPressed() {
        val layoutInflater = LayoutInflater.from(this)
        val view = layoutInflater.inflate(R.layout.alertdialog_item, null)

        val alertDialog = AlertDialog.Builder(this)
            .setView(view)
            .create()

        view.menu_name.text = ""
        view.menu_content.text = "앱을 종료하시겠습니까?"

        alertDialog.show()

        view.btn_yes.setOnClickListener {
            alertDialog.dismiss()
            finishAffinity()
        }
        view.btn_no.setOnClickListener {
            alertDialog.dismiss()
        }
    }

    override fun onResume() {
        super.onResume()
        CoroutineScope(Dispatchers.IO).launch {
            delay(500)
            withContext(Main) {
                fetchCurrentStation()
            }
        }
    }

    private fun initVariables() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
    }

    @SuppressLint("SetTextI18n")
    private fun fetchCurrentStation() {
        if (lati != null && long != null) {
            val thread = NetworkThread()
            thread.start()
            thread.join()
        }
        else {
            fetchLocation()
            currentStation = "정류장 불러오기 오류"
        }

        // api 정류소별경유노선 목록조회에 오류가 있어 임시로 정류장 번호 표시
        currentLocationText.text = "${currentStation}\n (${sttnNo})"

        // Talkback 사용자를 위해 toast를 띄워 현재 정류장을 읽도록 함
        if(currentStation == "정류장 불러오기 오류") {
            Toast.makeText(this,"정류장을 불러오지 못했습니다. 새로고침을 눌러주세요", Toast.LENGTH_SHORT).show()
        }
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
            // Toast.makeText(applicationContext, "${Lati}, ${Long}", Toast.LENGTH_LONG).show()
        }
    }

    inner class NetworkThread : Thread() {
        override fun run() {
            // 현재 정류장
            val urlAddress = "${address}${key}&gpsLati=${lati}&gpsLong=${long}&_type=json"

            try {
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

                val jsonObject = JSONObject(buf.toString())
                val response = jsonObject.getJSONObject("response").getJSONObject("body")
                    .getJSONObject("items")
                val item = response.getJSONArray("item")
                val iObject = item.getJSONObject(0)
                currentStation = iObject.getString("nodenm")
                sttnNo = iObject.getString("nodeno")
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
}
