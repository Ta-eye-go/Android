package com.code_23.ta_eye_go.ui.bookbus

class BusStationItem(BusStationName: String) {
    private var BusStationName: String? = BusStationName

    fun setBusStationName(BusStationName: String) {
        this.BusStationName = BusStationName
    }

    fun getBusStationName(): String? {
        return this.BusStationName
    }

}