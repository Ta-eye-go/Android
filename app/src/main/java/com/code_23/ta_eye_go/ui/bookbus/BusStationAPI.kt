package com.code_23.ta_eye_go.ui.bookbus

import java.net.URLDecoder

object BusStationAPI {
    const val BASE_URL = "http://openapi.tago.go.kr/openapi/service/"
    const val SELECT_CITY = "BusSttnInfoInqireService/getCtyCodeList"
    const val GET_NEAR_STATION = "BusSttnInfoInqireService/getCrdntPrxmtSttnList"
    const val ARRIVE_INFO = "ArvlInfoInqireService/getSttnAcctoArvlPrearngeInfoList"

    val BUS_STATION_SERVICE_KEY = URLDecoder.decode("NrOHnEMMNsLCDTuElcA01fuKwTdlJfGt95XWdtq771Ft34OvtB74iaRmUOCRc21wQPseZBRnw0bbvs%2B2Nbsedw%3D%3D", "UTF-8")
}