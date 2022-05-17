package com.code_23.ta_eye_go.ui.driver

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.graphics.Color
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.code_23.ta_eye_go.DB.*
import com.code_23.ta_eye_go.R
import com.code_23.ta_eye_go.data.BookerData
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_driver_getoff.view.*
import kotlinx.android.synthetic.main.activity_driver_main.*
import kotlinx.android.synthetic.main.activity_driver_reservation.view.*
import kotlinx.android.synthetic.main.activity_driver_reservation.view.end_station
import kotlinx.android.synthetic.main.bookers_item.view.*
import kotlinx.coroutines.*

class DriverMain : AppCompatActivity() {

    private var passengerIn : Int = 0
    private var passengerWaiting : Int = 0

    private lateinit var bookerAdapter: BookerAdapter
    private var reservations = mutableListOf<BookerData>()

    // 서버
    private lateinit var database: DatabaseReference
    // DriverDB
    private var driverDB : DriverDB? = null

    // 기사 버스 이름
    lateinit var driverNo: String

    // 알림 관련 (예약, 하차 등)
    private lateinit var view : View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_driver_main)

        // 서버
        database = Firebase.database.reference
        // DB
        driverDB = DriverDB.getInstance(this)
        driverDB?.driverDao()?.deleteAll()    // 기사용 DB 초기화
        val database1 = Firebase.database

        waitingNum.text = "$passengerWaiting"
        on_boardNum.text = "$passengerIn"
        driverNo = intent.getStringExtra("busNm").toString()

        bookerAdapter = BookerAdapter(this)
        rv_bookers.adapter = bookerAdapter
        rv_bookers.layoutManager = LinearLayoutManager(applicationContext)

        val driver1 = database1.getReference("Driver")
        driver1.removeValue()
        val driver = database1.getReference("Driver").child(driverNo)
        val boarding = database1.getReference("Driver").child("boarding")
        val onboard = database1.getReference("Driver").child("on board")
        val getoffi = database1.getReference("Driver").child("get off i")
        val getoff = database1.getReference("Driver").child("get off")
        val todriver = driverlist("","","","")
        val startNm =  bordinglist("")
        val endNm =  getofflist("")
        driver.setValue(todriver)
        boarding.setValue(startNm)
        onboard.setValue(startNm)
        getoffi.setValue(endNm)
        getoff.setValue(endNm)

        // 기사용 서버 실시간 데이터 확인
        val database = Firebase.database
        val driverdata = database.getReference("Driver")
        driverdata.addChildEventListener(object : ChildEventListener {
            override fun onChildRemoved(p0: DataSnapshot) {
            }
            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                Log.d("추가", driverNo)
                val tmpbooklist = driverlist()
                if (driverNo == p0.key.toString()) {
                    for (snapshot in p0.children) {
                        when (snapshot.key) {
                            "startNodenm" -> tmpbooklist.startNodenm = snapshot.value.toString()
                            "endNodenm" -> tmpbooklist.endNodenm = snapshot.value.toString()
                            "guide_dog" -> tmpbooklist.guide_dog = snapshot.value.toString()
                            "id" -> tmpbooklist.id = snapshot.value.toString()
                        }
                    }
                    Log.d("기사용_추가", tmpbooklist.toString())
                    Log.d("기사용_추가", tmpbooklist.id.toString())
                    Log.d("기사용_추가", tmpbooklist.startNodenm.toString())
                    Log.d("기사용_추가", tmpbooklist.endNodenm.toString())
                    Log.d("기사용_추가", tmpbooklist.guide_dog)

                    val Userbook = Driver(tmpbooklist.id.toString(),tmpbooklist.startNodenm.toString(),
                        tmpbooklist.endNodenm.toString(),tmpbooklist.guide_dog.toBoolean())
                    driverDB?.driverDao()?.insert(Userbook)
                    addBookerToList(tmpbooklist.startNodenm.toString(), tmpbooklist.endNodenm.toString(), tmpbooklist.guide_dog.toBoolean())
                    if (tmpbooklist.startNodenm != ""){
                        newNoti()
                    }
                }
                // 탑승하기 1정거장 전
                else if (p0.key.toString() == "boarding"){
                    for (snapshot in p0.children) {
                        if (snapshot.key == "startNodenm") {
                            val boardingnm = snapshot.value.toString()
                            val driverlist3 = driverDB?.driverDao()?.getAll()
                            if (driverlist3 != null){
                                for (index in driverlist3.indices){
                                    if (boardingnm == driverlist3[index].startNodenm) {
                                        oneSttnLeft(index)
                                        Log.d("기사용_탑승임박", index.toString())
                                    }
                                }
                            }
                        }
                    }
                }
                // 탑승완료
                else if (p0.key.toString() == "on board") {
                    Log.d("기사용_탑승완료1", "ㅇㅇ")
                    for (snapshot in p0.children){
                        if (snapshot.key == "startNodenm") {
                            val boardnm = snapshot.value.toString()
                            val driverlist4 = driverDB?.driverDao()?.getAll()
                            if (driverlist4 != null){
                                for (index in driverlist4.indices){
                                    Log.d("기사용_탑승완료2", index.toString())
                                    if (boardnm == driverlist4[index].startNodenm) {
                                        Log.d("기사용_탑승완료4", index.toString())
                                        CoroutineScope(Dispatchers.IO).launch {
                                            delay(1000)
                                        }
                                        if (index == 0){removeBookerInList(0, true)}
                                        else {removeBookerInList(index, true)
                                            Log.d("기사용_탑승완료3", index.toString())}
                                    }
                                    else {
                                        Log.d("기사용_탑승완료5", index.toString())
                                    }
                                }
                            }
                        }
                    }
                }
                // 하차 예정
                else if (p0.key.toString() == "get off i") {
                    for (snapshot in p0.children){
                        if (snapshot.key == "endNodenm") {
                            val boardnm = snapshot.value.toString()
                            val driverlist5 = driverDB?.driverDao()?.getAll()
                            if (driverlist5 != null){
                                for (index in driverlist5.indices){
                                    if (boardnm == driverlist5[index].endNodenm) {
                                        getOffNoti(boardnm, 1)
                                        Log.d("기사용_하차알림", index.toString())
                                    }
                                }
                            }
                        }
                    }
                }
                // 하차
                else if (p0.key.toString() == "get off") {
                    for (snapshot in p0.children){
                        if (snapshot.key == "endNodenm") {
                            val boardendNodenm = snapshot.value.toString()
                            getOff()
                            driverDB?.driverDao()?.delete(boardendNodenm)
                            Log.d("기사용_하차", boardendNodenm)
                        }
                    }
                }
                // 예약취소
//                else if (p0.key.toString() == "cancel") {
//                    for (snapshot in p0.children){
//                        if (snapshot.key == "startNodenm") {
//                            val boardendNodenm = snapshot.value.toString()
//                            driverDB?.driverDao()?.delete(boardendNodenm)
//                            getOff()
//                            Log.d("기사용_예약취소", boardendNodenm)
//                        }
//                    }
//                }
            }
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                Log.d("신규", driverNo)
            }
            override fun onCancelled(p0: DatabaseError) {
            }
            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
            }
        })
        val driverlist = driverDB?.driverDao()?.getAll()
        if (driverlist != null){
            for (index in driverlist.indices){
                BookerToList(driverlist[index].startNodenm, driverlist[index].endNodenm,driverlist[index].guide_dog)
            }
        }
    }


    /*override fun onStart() {
        super.onStart()
         //테스트 용
        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.Main) {

                delay(10000)
                // 신규 예약
                addBookerToList("인천대입구","인천대정문",true)
                delay(6000)
                addBookerToList("송도더샾마스터뷰23단지","해양경찰청",false)

                // 1 정거장 전 => 글씨 빨강색으로 변화
                delay(10000)
                oneSttnLeft(0)

                // 예약을 취소한 경우
                delay(9000)
                removeBookerInList(1, false)

                // 버스에 탑승한 경우
                delay(13000)
                removeBookerInList(0, true)

                // 세 정거장 전(하차 알림)
                delay(10000)
                getOffNoti("인천대입구", 1) // 승객 탑승 후에는 항목이 없어지기 때문에 서버에 저장된 값을 받아와야 함

                // 하차 (도중 하차, 정상 하차 모두)
                delay(13000)
                getOff()
            }
        }
    }*/

    @SuppressLint("NotifyDataSetChanged")
    private fun BookerToList(startSttnNm : String, endSttnNm : String, guideDog: Boolean) {
        reservations.add(BookerData(startSttnNm, endSttnNm, guideDog))
        bookerAdapter.insertBooker(BookerData(startSttnNm, endSttnNm, guideDog))
        bookerAdapter.notifyDataSetChanged()

        passengerWaiting += 1
        waitingNum.text = "$passengerWaiting"
        rv_bookers.scrollToPosition(0)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun addBookerToList(startSttnNm : String, endSttnNm : String, guideDog: Boolean) {
        reservations.add(BookerData(startSttnNm, endSttnNm, guideDog))
        bookerAdapter.insertBooker(BookerData(startSttnNm, endSttnNm, guideDog))
        bookerAdapter.notifyDataSetChanged()

        passengerWaiting += 1
        waitingNum.text = "$passengerWaiting"
        rv_bookers.scrollToPosition(0)

        newNoti()
    }

    private fun removeBookerInList(position : Int, onBoard : Boolean) {
        // 예약 취소
        if(!onBoard) { cancelNoti(position) }

        bookerAdapter.removeBooker(position)
        reservations.removeAt(position)

        passengerWaiting -= 1
        waitingNum.text = "$passengerWaiting"

        // 버스 탑승
        if (onBoard) {
            passengerIn += 1
            on_boardNum.text = "$passengerIn"
        }
    }

    private fun oneSttnLeft(position: Int) {
        val v : View? = rv_bookers.findViewHolderForAdapterPosition(position)?.itemView
        v?.booker_station?.setTextColor(Color.parseColor("#FFFE0000"))
    }

    private fun getOff() {
        passengerIn -= 1
        on_boardNum.text = "$passengerIn"
    }

    private fun ringtonePlay() {
//        val uriNotification: Uri = Uri.parse("android.resource://")
//        val ringtone: Ringtone = RingtoneManager.getRingtone(applicationContext, uriNotification)
//        ringtone.play()
        val ringtone : MediaPlayer = MediaPlayer.create(this, R.raw.notification_ringtone)
        ringtone.start()
    }

    private fun newNoti() {
        val layoutInflater = LayoutInflater.from(this)
        view = layoutInflater.inflate(R.layout.activity_driver_reservation, null)

        val alertDialog = AlertDialog.Builder(this)
            .setView(view)
            .create()

        view.start_station.text = reservations[reservations.size-1].startSttn
        view.end_station.text = reservations[reservations.size-1].endSttn

        if(reservations[reservations.size-1].guideDog) {
            view.with_guide_dog.text = "안내견 있음"
        } else view.with_guide_dog.text = " "

        alertDialog.show()
        ringtonePlay()

        CoroutineScope(Dispatchers.IO).launch {
            delay(2000)
            alertDialog.dismiss()
        }
    }

    private fun cancelNoti(position: Int) {
        val layoutInflater2 = LayoutInflater.from(this)
        view = layoutInflater2.inflate(R.layout.activity_driver_canceled, null)

        val alertDialog = AlertDialog.Builder(this)
            .setView(view)
            .create()

        view.start_station.text = reservations[position].startSttn

        alertDialog.show()
        ringtonePlay()

        CoroutineScope(Dispatchers.IO).launch {
            delay(7000)
            alertDialog.dismiss()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun getOffNoti(destination : String, num : Int) {
        val layoutInflater3 = LayoutInflater.from(this)
        view = layoutInflater3.inflate(R.layout.activity_driver_getoff, null)

        val alertDialog = AlertDialog.Builder(this)
            .setView(view)
            .create()

        view.end_station.text = destination
        if(num > 1) {
            view.more_than_one.text = "${num}명"
        } else view.more_than_one.text = " "

        alertDialog.show()
        ringtonePlay()

        CoroutineScope(Dispatchers.IO).launch {
            delay(10000)
            alertDialog.dismiss()
        }
    }
}