package com.code_23.ta_eye_go.ui.main

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import com.code_23.ta_eye_go.R
import com.code_23.ta_eye_go.databinding.ActivityMainBinding
import com.code_23.ta_eye_go.ui.bookbus.ChatbotMainActivity
import com.code_23.ta_eye_go.ui.bookmark.BookmarkList
import com.code_23.ta_eye_go.ui.settings.Settings
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import org.json.JSONObject
import org.json.JSONException
import java.io.IOException
import java.net.MalformedURLException


class MainActivity : AppCompatActivity() {

    val binding by lazy { ActivityMainBinding.inflate(layoutInflater)}

    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var lati: Double? = null
    private var long: Double? = null
    var currentStation: String? = "정류장 불러오기 오류"
    var sttnNo: String? = "버스 번호 오류"
    private val key =
        "NrOHnEMMNsLCDTuElcA01fuKwTdlJfGt95XWdtq771Ft34OvtB74iaRmUOCRc21wQPseZBRnw0bbvs%2B2Nbsedw%3D%3D"
    private val address =
        "http://openapi.tago.go.kr/openapi/service/BusSttnInfoInqireService/getCrdntPrxmtSttnList?serviceKey="

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /*// Write a message to the database
        val database = Firebase.database
        val myRef = database.getReference("message")

        myRef.setValue("Hello, World!")

        // Read from the database
        myRef.addValueEventListener(object: ValueEventListener() {
            override fun onDataChange(snapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                val value = snapshot.getValue<String>()
                Log.d(TAG, "Value is: " + value)
            }
            override fun onCancelled(error: DatabaseError) {
                Log.w(TAG, "Failed to read value.", error.toException())
            }
        })*/


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
            initVariables()
            fetchLocation()
            fetchCurrentStaion()
        }
    }

    override fun onResume() {
        super.onResume()
        val thread = NetworkThread()
        thread.start()
        thread.join()
    }

    private fun initVariables() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
    }

    private fun fetchCurrentStaion() {
        val thread = NetworkThread()
        thread.start()
        thread.join()
    }

    private fun fetchLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }

        fusedLocationProviderClient.lastLocation.addOnSuccessListener {
            lati = it.latitude
            long = Math.abs(it.longitude)
            // Toast.makeText(applicationContext, "${Lati}, ${Long}", Toast.LENGTH_LONG).show()
        }
    }

    @SuppressLint("SetTextI18n")
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

                currentLocationText.text = "현재 정류장\n ${currentStation}\n (${sttnNo})"
            } catch (e: MalformedURLException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: JSONException) {
                e.printStackTrace()
                currentLocationText.text = "현재 정류장\n 새로고침 오류"
            }
        }
    }
}