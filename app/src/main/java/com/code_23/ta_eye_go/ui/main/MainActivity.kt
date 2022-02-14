package com.code_23.ta_eye_go.ui.main

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.code_23.ta_eye_go.R
import com.code_23.ta_eye_go.databinding.ActivityMainBinding
import com.code_23.ta_eye_go.ui.bookbus.ChatbotMainActivity
import com.code_23.ta_eye_go.ui.bookmark.BookmarkList
import com.code_23.ta_eye_go.ui.settings.Settings
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.ktx.database
// import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.currentLocationText
import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import org.json.JSONObject
import org.json.JSONException
import java.io.IOException
import java.net.MalformedURLException
import kotlin.math.abs


class MainActivity : AppCompatActivity() {

    val binding by lazy { ActivityMainBinding.inflate(layoutInflater)}

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var lati: Double? = null
    private var long: Double? = null
    var currentStation: String? = ""

    // 정류장 id
    var sttnId: String? = null

    var sttnNo: String? = "새로고침을 눌러주세요"
    private val key =
        "NrOHnEMMNsLCDTuElcA01fuKwTdlJfGt95XWdtq771Ft34OvtB74iaRmUOCRc21wQPseZBRnw0bbvs%2B2Nbsedw%3D%3D"
    private val address =
        "http://openapi.tago.go.kr/openapi/service/BusSttnInfoInqireService/getCrdntPrxmtSttnList?serviceKey="

    // 뒤로가기
    private var mBackWait:Long = 0

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initVariables()
        fetchLocation()

        // 예약 창 이동
        bookBusBtn.setOnClickListener {
            val intent = Intent(this, ChatbotMainActivity::class.java)
            intent.putExtra("currentStation", currentStation)
            startActivity(intent)
        }
        // 설정 창 이동
        settingBtn.setOnClickListener {
            val intent = Intent(this, Settings::class.java)
            startActivity(intent)
        }
        // 즐겨찾기 창 이동
        bookmarkBtn.setOnClickListener {
            val intent = Intent(this, BookmarkList::class.java)
            startActivity(intent)
        }
        // 현 위치 새로고침
        refreshBtn.setOnClickListener {
            fetchLocation()
            fetchCurrentStation()
        }
    }

    // 뒤로 가기 버튼 종료 액션
    override fun onBackPressed() {
        if(System.currentTimeMillis() - mBackWait >= 2000 ) {
            mBackWait = System.currentTimeMillis()
            Toast.makeText(applicationContext, "뒤로가기 버튼을 한번 더 누르면 종료됩니다.", Toast.LENGTH_LONG).show()
        } else {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        CoroutineScope(Dispatchers.IO).launch {
            delay(1000)
            withContext(Dispatchers.Main) {
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
        currentLocationText.text = "${currentStation}\n (${sttnNo})"
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
