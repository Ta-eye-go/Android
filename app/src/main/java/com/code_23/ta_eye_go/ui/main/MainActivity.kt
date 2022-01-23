package com.code_23.ta_eye_go.ui.main

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import com.code_23.ta_eye_go.R
import com.code_23.ta_eye_go.ui.bookbus.ChatbotMain
import com.code_23.ta_eye_go.ui.bookmark.BookmarkList
import com.code_23.ta_eye_go.ui.login.LoginEmail
import com.code_23.ta_eye_go.ui.settings.Settings
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        fetchLocation()

        // 예약 창 이동
        bookBusBtn.setOnClickListener {
            val intent = Intent(this, ChatbotMain::class.java)
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
            // getCurrentStation()
        }
    }

    private fun fetchLocation(){
        val task = fusedLocationProviderClient.lastLocation

        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),101)
            return
        }
        task.addOnSuccessListener {
            if(it != null){
                currentLocationText.text = "현재 정류장(임시로 좌표 표시)\n ${it.latitude}\n ${it.longitude}"
            }
        }
    }

    private fun getCurrentStation(){

    }

}
