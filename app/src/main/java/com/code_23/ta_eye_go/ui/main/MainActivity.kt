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
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var lati: Double? = null
    private var long: Double? = null
    private var citycode: String? = null // 도시코드
    var currentStation: String? = null  // 현재정류장
    var sttnId: String? = null  // 현재정류장 id
    var routeID: String? = null
    var nextSttnNm: String? = null

    var sttnNo: String? = null
    private val key = BuildConfig.TAGO_API_KEY
    private val addressCrntSttn = "http://apis.data.go.kr/1613000/BusSttnInfoInqireService/getCrdntPrxmtSttnList?serviceKey="
    private val addressBusList = "http://apis.data.go.kr/1613000/BusSttnInfoInqireService/getSttnThrghRouteList?serviceKey="
    private val addressStopList = "http://apis.data.go.kr/1613000/BusRouteInfoInqireService/getRouteAcctoThrghSttnList?serviceKey="

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
                val uid = database.getReference("uid")
                uid.setValue("Kakao")
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

        // 로그인한 유저 DB등록
        val r = Runnable {
            try {
                //사용자 정보 가져오기 (Logcat에서 확인가능, 한번 동의하면 그 뒤로 동의메시지 안뜸)
                UserApiClient.instance.me { user, error ->
                    when {
                        error != null -> {
                            Log.e(ContentValues.TAG, "사용자 정보 요청 실패", error)
                            Log.d("kakao_email2", user?.kakaoAccount?.email.toString())
                        }
                        user != null -> {
                            Log.d("kakao_email", user.kakaoAccount?.email.toString())
                            kakaoemail = user.kakaoAccount?.email.toString()
                            val users = User(user.kakaoAccount?.email.toString(), false)
                            userDB?.userDao()?.insert(users)
                        }
                        else -> {
                            val users = User(Firebase.auth.currentUser?.email.toString(), false)
                            userDB?.userDao()?.insert(users)
                        }
                    }
                }
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
            exitProcess(0)
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
            currentStation = "위치 불러오기 오류"
        }

        when (currentStation) {
            "위치 불러오기 오류" -> {
                currentLocationText.text = "위치 불러오기 오류"
                nextStationText.text = "(새로고침을 눌러주세요)"
                Toast.makeText(this,"위치를 불러오지 못했습니다. 새로고침을 눌러주세요.", Toast.LENGTH_SHORT).show()
            }
            null -> {
                currentLocationText.text = "해당 위치 정류장을"
                nextStationText.text = "찾을 수 없습니다."
                Toast.makeText(this,"해당 위치에서 정류장을 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
            else -> {
                // 다음 정류장 (방면) 표시
                if (nextSttnNm == null) { // api 정류소별경유노선에 잦은 오류: 오류시 정류장 번호로 대체
                    currentLocationText.text = currentStation
                    if (sttnNo == null) nextStationText.text = "(${sttnId})"
                    else nextStationText.text = "(${sttnNo})"
                } else {
                    currentLocationText.text = currentStation
                    nextStationText.text = "(${nextSttnNm} 방면)"
                }
                Toast.makeText(applicationContext, "현재 정류장은 ${currentStation}입니다.", Toast.LENGTH_LONG).show()
            }
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

    inner class NetworkThread : Thread() {
        override fun run() {
            // 현재 정류장
            val urlAddress = "${addressCrntSttn}${key}&gpsLati=${lati}&gpsLong=${long}&_type=json"
            Log.d("url", urlAddress)

            try {
                val buf = parsing1(urlAddress)
                val jsonObject = JSONObject(buf.toString())
                val response = jsonObject.getJSONObject("response").getJSONObject("body")
                    .getJSONObject("items")
                val item = response.getJSONArray("item")
                val iObject = item.getJSONObject(0)
                currentStation = iObject.getString("nodenm")
                sttnId = iObject.getString("nodeid")
                citycode = iObject.getString("citycode")
                sttnNo = iObject.getString("nodeno")

            } catch (e: MalformedURLException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            var urlAddress2 = "${addressBusList}${key}&numOfRows=1000&cityCode=${citycode}&nodeid=${sttnId}&_type=json"
            Log.d("url", urlAddress2)

            try {
                var buf = parsing1(urlAddress2)

                var jsonObject = JSONObject(buf.toString())
                val totalCnt = jsonObject.getJSONObject("response").getJSONObject("body")
                    .getInt("totalCount")

                routeID = if (totalCnt == 1) { // 결과가 하나일 경우
                    val response = jsonObject.getJSONObject("response").getJSONObject("body")
                        .getJSONObject("items").getJSONObject("item")
                    response.getString("routeid")
                } else { // 결과가 두 개 이상 (array)
                    val response = jsonObject.getJSONObject("response").getJSONObject("body")
                        .getJSONObject("items")
                    val item = response.getJSONArray("item")
                    val iObject = item.getJSONObject(totalCnt-1)
                    iObject.getString("routeid")
                }

                urlAddress2 = "${addressStopList}${key}&pageNo=1&numOfRows=1000&cityCode=${citycode}&routeId=${routeID}&_type=json"
                buf = parsing1(urlAddress2)
                jsonObject = JSONObject(buf.toString())

                val response = jsonObject.getJSONObject("response").getJSONObject("body")
                    .getJSONObject("items")
                val item = response.getJSONArray("item")
                for (i in 0 until item.length()) {
                    val iObject = item.getJSONObject(i)
                    if (iObject.getString("nodeid") == sttnId) {
                        nextSttnNm = item.getJSONObject(i+1).getString("nodenm")
                        break
                    }
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
