package com.code_23.ta_eye_go.ui.bookbus

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.code_23.ta_eye_go.R
import com.code_23.ta_eye_go.ui.main.MainActivity
import kotlinx.android.synthetic.main.activity_in_bus.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.MalformedURLException
import java.net.URL

class InBus : AppCompatActivity() {

    // 참고 : 경유 정류장을 지날 때는 버스 위치가 안뜸
    //        출발 정류장에 버스가 없는 경우 실행이 안될 수 있으니 테스트 시 유의 바람
    //        실시간 적용을 아직 안한 상태 (적용 예정) => 도착 후에 새로고침 한 번 더 눌러줘야 제대로 작동합니다!

    // 이전 단계에서 받아와야 하는 변수들(3개)
    private val startSttnID : String = "GGB229000509" // 출발 정류장 id
    private val endSttnID : String = "GGB229000585" // 도착 정류장 id
    private val routeId : String = "GGB229000042" // 탑승 버스의 노선 번호
    // private val startSttnNm : String? = "가람마을3.4.6단지"
    // private val destination : String? = "파주우체국"
    // private val busNm : String? = "92"

    private val citycode : Int = 31200 // 도시코드 (인천 : 23)
    private var vehicleNo : String? = null // 버스 번호
    private var nodeord : Int? = null // 현재 정류장의 순서 번호 -> 노선마다 정류장에 순서대로 번호를 붙임
    private var endNodeord : Int? = null // 도착 정류장의 순서 번호
    private var thisStation : String? = null // 현재 정류장 이름
    private var nextStation : String? = null // 도착 정류장 이름
    private var leftSttnCnt : Int = 0 // 남은 정류장

    // 도착 여부 확인용
    var arrive = false

    // private val key = "NrOHnEMMNsLCDTuElcA01fuKwTdlJfGt95XWdtq771Ft34OvtB74iaRmUOCRc21wQPseZBRnw0bbvs%2B2Nbsedw%3D%3D"
    private val key = "NbREnDA1nV3nLBWbv7EXWntBQT%2BoyKeMVAPC7dGVUYJu8zgIV%2FHzLylOStyuhH%2FjTSuC3Nj0pjTC6sCV9jkY%2Fg%3D%3D"
    private val address_myBusLc = "http://openapi.tago.go.kr/openapi/service/BusLcInfoInqireService/getRouteAcctoBusLcList?serviceKey=" //노선별버스위치목록조회
    private val address_getNodeord = "http://openapi.tago.go.kr/openapi/service/BusRouteInfoInqireService/getRouteAcctoThrghSttnList?serviceKey=" //노선별경유정류소목록조회

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_in_bus)

        CoroutineScope(Dispatchers.IO).launch {
            delay(1000)
            withContext(Main) {
                val thread = NetworkThread()
                thread.start()
                thread.join()
            }
        }

        refreshBtn.setOnClickListener {
            if (!arrive) {
                CoroutineScope(Dispatchers.IO).launch {
                    withContext(Main){
                        val thread = NetworkThread()
                        thread.start()
                        thread.join()
                    }
                }
                if (arrive) {
                    // TODO : 버스 도착 후 처리
                    Toast.makeText(applicationContext, "정류장에 도착했습니다.", Toast.LENGTH_LONG).show()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
            if (arrive) { // TODO : 버스 도착 후 처리2
                Toast.makeText(applicationContext, "정류장에 도착했습니다.", Toast.LENGTH_LONG).show()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        getoffBtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
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
        @SuppressLint("SetTextI18n")
        override fun run() {
            // 탑승한 차량의 위치 정보
            if (vehicleNo == null) { // 탑승한 버스 차량 번호를 모를 때 (처음 돌렸을 경우)
                val urlAddress = "${address_myBusLc}${key}&cityCode=${citycode}&routeId=${routeId}&_type=json"

                try {
                    val buf = parsing1(urlAddress)
                    val jsonObject = JSONObject(buf.toString())

                    // 결과가 하나 있을 때는 Array로 처리하면 오류가 나기 때문에 구분해주기
                    val totalCnt = jsonObject.getJSONObject("response").getJSONObject("body")
                        .getInt("totalCount")
                    // 결과가 하나일 경우
                    if (totalCnt == 1) {
                        val response = jsonObject.getJSONObject("response").getJSONObject("body")
                            .getJSONObject("items").getJSONObject("item")
                        vehicleNo = response.getString("vehicleno")
                        nodeord = response.getInt("nodeord")
                    } else {
                        val response = jsonObject.getJSONObject("response").getJSONObject("body")
                            .getJSONObject("items")
                        val item = response.getJSONArray("item")
                        for (i in 0 until item.length()) {
                            val iObject = item.getJSONObject(i)
                            if (iObject.getString("nodeid") == startSttnID) {
                                vehicleNo = iObject.getString("vehicleno")
                                nodeord = iObject.getInt("nodeord")
                                break
                            }
                        }
                    }

                } catch (e: MalformedURLException) {
                    e.printStackTrace()
                } catch (e: IOException) {
                    e.printStackTrace()
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            } else { // 이미 버스 차량번호를 알고 있을 때
                val urlAddress = "${address_myBusLc}${key}&cityCode=${citycode}&routeId=${routeId}&_type=json"
                try {
                    val buf = parsing1(urlAddress)

                    val jsonObject = JSONObject(buf.toString())
                    val response = jsonObject.getJSONObject("response").getJSONObject("body")
                        .getJSONObject("items")
                    val item = response.getJSONArray("item")
                    for (i in 0 until item.length()) {
                        val iObject = item.getJSONObject(i)
                        if (iObject.getString("vehicleno") == "$vehicleNo") {
                            nodeord = iObject.getInt("nodeord")
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

            // 현재 정류장의 다음 정류장 조회
            // nodeord는 현재 정류장, nodeord+1은 다음 정류장임을 이용한다.
            val urlAddress1 =
                "${address_getNodeord}${key}&numOfRows=1&pageNo=${nodeord}&cityCode=${citycode}&routeId=${routeId}&_type=json"
            val urlAddress2 =
                "${address_getNodeord}${key}&numOfRows=1&pageNo=${nodeord?.plus(1)}&cityCode=${citycode}&routeId=${routeId}&_type=json"

            try {
                val buf1 = parsing1(urlAddress1)
                val buf2 = parsing1(urlAddress2)

                val jsonObject1 = JSONObject(buf1.toString())
                val jsonObject2 = JSONObject(buf2.toString())

                val response1 = jsonObject1.getJSONObject("response").getJSONObject("body")
                    .getJSONObject("items").getJSONObject("item")
                val response2 = jsonObject2.getJSONObject("response").getJSONObject("body")
                    .getJSONObject("items").getJSONObject("item")

                thisStation = response1.getString("nodenm")
                nextStation = response2.getString("nodenm")

                currentStation_text.text = thisStation
                nextStation_text.text = nextStation

            } catch (e: MalformedURLException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            // 남은 정류장 수 계산하기
            // 1. 도착 정류장 nodeord 구하기
            if (endNodeord == null) {
                var pageNum = nodeord?.div(10)?.plus(1)

                while (true) {
                    val urlAddress =
                        "${address_getNodeord}${key}&numOfRows=10&pageNo=${pageNum}&cityCode=${citycode}&routeId=${routeId}&_type=json"
                    try {
                        val buf = parsing1(urlAddress)
                        val jsonObject = JSONObject(buf.toString())

                        // 결과가 하나 있을 때는 Array로 처리하면 오류가 나기 때문에 구분해주기
                        val totalCnt = jsonObject.getJSONObject("response").getJSONObject("body")
                            .getInt("totalCount")
                        // 결과가 하나일 경우
                        if (totalCnt == 1) {
                            val response = jsonObject.getJSONObject("response").getJSONObject("body")
                                .getJSONObject("items").getJSONObject("item")
                            if (response.getString("nodeid") == endSttnID) {
                                endNodeord = response.getInt("nodeord")
                                break
                            }
                        } else {
                            val response = jsonObject.getJSONObject("response").getJSONObject("body")
                                .getJSONObject("items")
                            val item = response.getJSONArray("item")
                            for (i in 0 until item.length()) {
                                val iObject = item.getJSONObject(i)
                                if (iObject.getString("nodeid") == endSttnID) {
                                    endNodeord = iObject.getInt("nodeord")
                                    break
                                }
                            }
                        }
                    } catch (e: MalformedURLException) {
                        e.printStackTrace()
                        break
                    } catch (e: IOException) {
                        e.printStackTrace()
                        break
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        break
                    }
                    if (pageNum != null) {
                        if (endNodeord != null) break
                        else pageNum += 1
                    }
                }
            }
            // 도착정류장의 nodeord가 있을 때
            if(endNodeord == null) numOfStations_text.text = "오류 발생"
            else {
                leftSttnCnt = endNodeord!! - nodeord!!
                if (leftSttnCnt <= 0) { // 남은 정류장이 0 이하가 되면
                    arrive = true
                }
                numOfStations_text.text = "$leftSttnCnt 정류장"
            }
        }
    }
}