package com.code_23.ta_eye_go.ui.driver

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.code_23.ta_eye_go.R
import com.code_23.ta_eye_go.data.BookerData
import kotlinx.android.synthetic.main.activity_driver_main.*

class DriverMain : AppCompatActivity() {

    private lateinit var bookerAdapter: BookerAdapter
    private var reservations = mutableListOf<BookerData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_driver_main)

        bookerAdapter = BookerAdapter(this)
        rv_bookers.adapter = bookerAdapter
        rv_bookers.layoutManager = LinearLayoutManager(applicationContext)

        // 예시 예약
        addBookerToList("인천대입구","인천대정문",true)
        addBookerToList("송도더샾마스터뷰23단지","해양경찰청",false)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun addBookerToList(startSttnNm : String, endSttnNm : String, guideDog: Boolean) {
        reservations.add(BookerData(startSttnNm, endSttnNm, guideDog))
        bookerAdapter.insertBooker(BookerData(startSttnNm, endSttnNm, guideDog))
        bookerAdapter.notifyDataSetChanged()
        rv_bookers.scrollToPosition(0)
    }
}