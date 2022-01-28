package com.code_23.ta_eye_go.ui.bookbus

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.code_23.ta_eye_go.R
import com.code_23.ta_eye_go.ui.main.MainActivity
import kotlinx.android.synthetic.main.activity_after_reservation.*
import kotlinx.android.synthetic.main.activity_after_reservation.currentLocationText
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.MalformedURLException
import java.net.URL

class AfterReservation : AppCompatActivity(){

    // 변수들은 임시로 값을 넣어둠
    private var currentSttnNm : String? = "인천대정문"
    private var currentSttnID : String? = "ICB164000385"
    private var destination : String? = "인천대입구"
    private var busNm : String? = "8"
    private var citycode : Int? = 23
    private var routeId : String? = ""
    private var prevSttnCnt : Int? = 0
    private var arrTime : Int? = 0

    private val key = "NrOHnEMMNsLCDTuElcA01fuKwTdlJfGt95XWdtq771Ft34OvtB74iaRmUOCRc21wQPseZBRnw0bbvs%2B2Nbsedw%3D%3D"
    private val address_getRoute = "http://openapi.tago.go.kr/openapi/service/BusRouteInfoInqireService/getRouteNoList?serviceKey="
    private val address_busLc = "http://openapi.tago.go.kr/openapi/service/BusLcInfoInqireService/getRouteAcctoSpcifySttnAccesBusLcInfo?serviceKey="

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_after_reservation)

        reservationStatus()
        val thread = NetworkThread()
        thread.start()
        thread.join()

        cancel_button.setOnClickListener {
            // 화면 이동만 일단 해둠...
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun reservationStatus() {
        currentStation_text.text = "$currentSttnNm"
        busNum_text.text = "$busNm"
        destination_text.text = "$destination"
    }

    inner class NetworkThread : Thread() {
        override fun run() {

            // 타고자 하는 버스의 노선 id 받아오기
            val urlAddress : String? = "${address_getRoute}${key}&cityCode=${citycode}&routeNo=${busNm}&_type=json"

            try {
                val url = URL(urlAddress)
                val conn = url.openConnection()
                val input = conn.getInputStream()
                val isr = InputStreamReader(input)
                val br = BufferedReader(isr)

                var str: String? = null
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
                routeId = iObject.getString("routeid")

            } catch (e: MalformedURLException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            // 버스 정보 받아오기
            val urlAddress2 : String? = "${address_busLc}${key}&routeId=${routeId}&nodeId=${currentSttnID}&cityCode=${citycode}&_type=json"

            try {
                val url = URL(urlAddress2)
                val conn = url.openConnection()
                val input = conn.getInputStream()
                val isr = InputStreamReader(input)
                val br = BufferedReader(isr)

                var str: String? = null
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

                prevSttnCnt = iObject.getInt("arrprevstationcnt")
                arrTime = iObject.getInt("arrtime")

                currentLocationText.text = "${prevSttnCnt} 정거장 전\n ${arrTime} 분 후 도착 예정입니다."

            } catch (e: MalformedURLException) {
                e.printStackTrace()
                currentLocationText.text = "버스 정보 불러오기 오류"
            } catch (e: IOException) {
                e.printStackTrace()
                currentLocationText.text = "버스 정보 불러오기 오류"
            } catch (e: JSONException) {
                e.printStackTrace()
                currentLocationText.text = "현재 버스가 운행되지 않습니다."
            }
        }
    }
}

