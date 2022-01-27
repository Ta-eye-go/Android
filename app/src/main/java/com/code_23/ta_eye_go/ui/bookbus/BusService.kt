package com.code_23.ta_eye_go.ui.bookbus

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

class BusService {

    interface BusService {
        @Headers("Connection: close")
        @GET(BusStationAPI.GET_NEAR_STATION)
        fun getNearStation(@Query("serviceKey") serviceKey: String, @Query("gpsLati") latitude: Double, @Query("gpsLong") longitude: Double) : Call<Station>

        @Headers("Connection: close")
        @GET(BusStationAPI.ARRIVE_INFO)
        fun getArriveInfo(@Query("serviceKey") serviceKey: String, @Query("cityCode") cityCode: String, @Query("nodeId") nodeId: String) : Call<ArriveResponse>
    }
}