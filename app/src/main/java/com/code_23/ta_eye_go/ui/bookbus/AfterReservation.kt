package com.code_23.ta_eye_go.ui.bookbus

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.code_23.ta_eye_go.R
import com.code_23.ta_eye_go.ui.main.MainActivity
import kotlinx.android.synthetic.main.activity_after_reservation.*
import kotlinx.android.synthetic.main.activity_after_reservation.currentLocationText
import kotlinx.android.synthetic.main.alertdialog_item.view.*
import kotlinx.android.synthetic.main.menu_bar.*
import kotlinx.android.synthetic.main.menu_bar.view.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.MalformedURLException
import java.net.URL

class AfterReservation : AppCompatActivity() {

    // ** 변수들은 일단 임시로 값을 넣어둠
    // 예약 시 받아와야 하는 변수들(4개)
    private val startSttnNm : String = "당하대주파크빌" // 출발(현재) 정류장 이름
    private val startSttnID : String = "ICB168000392" // 출발 정류장 id
    private val destination : String = "검암역입구" // 도착 정류장 이름
    private val busNm : String = "1" // 탈 버스 번호

    private val citycode : Int = 23 // 도시코드 (인천 : 23)
    private var routeId : String? = null // 버스의 노선 번호
    private var prevSttnCnt : Int? = 0 // 남은 정류장 수
    private var arrTime : Int? = 0 // 도착 예정 시간

    // 실시간 여부 확인용
    // var j = 0

    // 도착 여부 확인용
    var arrive = false

    private val key = "NrOHnEMMNsLCDTuElcA01fuKwTdlJfGt95XWdtq771Ft34OvtB74iaRmUOCRc21wQPseZBRnw0bbvs%2B2Nbsedw%3D%3D"
    private val address_getRoute = "http://openapi.tago.go.kr/openapi/service/BusRouteInfoInqireService/getRouteNoList?serviceKey=" //노선정보항목조회
    private val address_busLc = "http://openapi.tago.go.kr/openapi/service/ArvlInfoInqireService/getSttnAcctoSpcifyRouteBusArvlPrearngeInfoList?serviceKey=" //정류소별특정노선버스도착예정정보목록조회

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_after_reservation)
        menu.menu_text.text = "버스 탑승 예정"

        cancel_button.setOnClickListener {
            // 화면 이동만 일단 해둠...
            confirmDialog()
        }

        back_btn.setOnClickListener {
            // TODO : 다시 이 화면으로 돌아와도 지장 없도록 해야 함...(안드)
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        // coroutines을 이용한 실시간 업데이트 처리
        // 참고 : delay(1000) => 1초 지연
        // 주의 : delay 안에 수를 너무 작게하면 api 일일 접근 횟수(1000회)를 초과하니 주의! 10000 이하로는 추천하지 않음
        CoroutineScope(Dispatchers.IO).launch {
            // 서버에서 값을 받아오는 시간을 벌기 위해 의도적으로 딜레이 추가
            delay(1000)
            withContext(Main) {
                reservationStatus()
                val thread = NetworkThread()
                thread.start()
                thread.join()
            }
            for(i in 0..100) {
                if (!arrive) {
                    withContext(Main) {
                        val thread = NetworkThread()
                        thread.start()
                        thread.join()
                    }
                    if (arrive) {
                        // TODO : 탑승 완료 후 처리 1
                        currentLocationText.text = "탑승이 완료되었습니다."
                        break
                    }
                    delay(20000) // 20초 기다리기
                }
                if (arrive) {
                    // TODO : 탑승 완료 후 처리 2
                    currentLocationText.text = "탑승이 완료되었습니다."
                    break
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        Log.d("life_cycle","onStop")
    }

    private fun reservationStatus() {
        currentStation_text.text = startSttnNm
        busNum_text.text = busNm
        destination_text.text = destination
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

    private fun confirmDialog() {
        val layoutInflater = LayoutInflater.from(this)
        val view = layoutInflater.inflate(R.layout.alertdialog_item, null)

        val alertDialog = AlertDialog.Builder(this)
            .setView(view)
            .create()

        view.menu_name.text = "<취소 확인>"
        view.menu_content.text = "취소하시겠습니까?"

        alertDialog.show()

        view.btn_yes.setOnClickListener {
            alertDialog.dismiss()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        view.btn_no.setOnClickListener {
            alertDialog.dismiss()
        }
    }

    // json 파싱...
    inner class NetworkThread : Thread() {
        @SuppressLint("SetTextI18n")
        override fun run() {
            // 타고자 하는 버스의 노선 id 받아오기 (노선은 한 번만 받아오도록 처리)
            if (routeId == null) {
                var urlAddress =
                    "${address_getRoute}${key}&cityCode=${citycode}&routeNo=${busNm}&_type=json"

                try {
                    var buf = parsing1(urlAddress)
                    var jsonObject = JSONObject(buf.toString())

                    // 결과가 하나 있을 때는 Array로 처리하면 오류가 나기 때문에 구분해주기
                    val totalCnt = jsonObject.getJSONObject("response").getJSONObject("body")
                        .getInt("totalCount")
                    // 결과가 하나일 경우
                    if (totalCnt == 1) {
                        val response = jsonObject.getJSONObject("response").getJSONObject("body")
                            .getJSONObject("items").getJSONObject("item")
                        routeId = response.getString("routeid")
                    }
                    else if (totalCnt > 10) { // 결과가 10개 이상 (2 페이지 이상)
                        urlAddress = "${address_getRoute}${key}&numOfRows=${totalCnt}&cityCode=${citycode}&routeNo=${busNm}&_type=json"
                        buf = parsing1(urlAddress)
                        jsonObject = JSONObject(buf.toString())
                        val response = jsonObject.getJSONObject("response").getJSONObject("body")
                            .getJSONObject("items")
                        val item = response.getJSONArray("item")
                        for (i in 0 until item.length()) {
                            val iObject = item.getJSONObject(i)
                            if (iObject.getString("routeno") == busNm) {
                                routeId = iObject.getString("routeid")
                                break
                            }
                        }
                    }
                    // 버스 정보의 경우, 검색한 문자열을 포함하는 버스가 모두 검색된다. ex) 10번 검색시 : 10번, 9710번, 108번이 모두 검색됨
                    // 따라서 여러 가지 결과가 나올 경우, 우리가 검색한 번호와 일치하는 버스만 선택하여 골라준다.
                    else {
                        val response = jsonObject.getJSONObject("response").getJSONObject("body")
                            .getJSONObject("items")
                        val item = response.getJSONArray("item")
                        for (i in 0 until item.length()) {
                            val iObject = item.getJSONObject(i)
                            if (iObject.getString("routeno") == busNm) {
                                routeId = iObject.getString("routeid")
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
            }

            // 버스 정보 받아오기
            val urlAddress2 =
                "${address_busLc}${key}&cityCode=${citycode}&nodeId=${startSttnID}&routeId=${routeId}&_type=json"

            try {
                val buf = parsing1(urlAddress2)
                val jsonObject = JSONObject(buf.toString())

                // 결과가 하나 있을 때는 Array로 처리하면 오류가 나기 때문에 구분해주기
                val totalCnt = jsonObject.getJSONObject("response").getJSONObject("body")
                    .getInt("totalCount")
                // 결과가 하나인 경우
                if (totalCnt == 1) {
                    val response = jsonObject.getJSONObject("response").getJSONObject("body")
                        .getJSONObject("items").getJSONObject("item")
                    if (prevSttnCnt == 1 && response.getInt("arrprevstationcnt") > 1) {
                        // 남은 정류장 수가 1이었는데 1보다 커진 경우 -> 버스에 탔다고 처리
                        arrive = true
                    }
                    prevSttnCnt = response.getInt("arrprevstationcnt")
                    arrTime = response.getInt("arrtime")
                }
                // 결과가 두 개 이상인 경우 (제일 가까운 버스로 안내)
                else {
                    val response = jsonObject.getJSONObject("response").getJSONObject("body")
                        .getJSONObject("items")
                    val item = response.getJSONArray("item")
                    // 도착 예정 시간이 더 적은 버스 고르기
                    var tmp = 999
                    for (i in 0 until item.length()) {
                        val iObject = item.getJSONObject(i)
                        if (tmp > iObject.getInt("arrprevstationcnt")) {
                            tmp = iObject.getInt("arrprevstationcnt")
                            if (prevSttnCnt == 1 && iObject.getInt("arrprevstationcnt") > 1) {
                                // 남은 정류장 수가 1이었는데 1보다 커진 경우(뒤 버스가 있는 경우) -> 버스에 탔다고 처리
                                arrive = true
                            }
                            prevSttnCnt = iObject.getInt("arrprevstationcnt")
                            arrTime = iObject.getInt("arrtime")
                        }
                    }
                }
                if (arrTime!! < 60) { // 도착 예정 시간 1분 미만 시
                    currentLocationText.text = "$prevSttnCnt 정거장 전\n 잠시 후 도착 예정입니다."
                }
                else {
                    currentLocationText.text = "$prevSttnCnt 정거장 전\n ${arrTime?.div(60)} 분 후 도착 예정입니다."
                }

            } catch (e: MalformedURLException) {
                e.printStackTrace()
                currentLocationText.text = "버스 정보 불러오기 오류"
            } catch (e: IOException) {
                e.printStackTrace()
                currentLocationText.text = "버스 정보 불러오기 오류"
            } catch (e: JSONException) {
                e.printStackTrace()
                if (prevSttnCnt == 1) {
                    // 남은 정류장이 1이다가 목록이 없을 때(뒤 버스가 없는 경우) -> 버스에 탔다고 처리
                    arrive = true
                }
                currentLocationText.text = "현재 버스가 운행되지 않습니다."
            }
        }
    }
}

