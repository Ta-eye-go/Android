package com.code_23.ta_eye_go.ui.bookbus

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.code_23.ta_eye_go.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BusStationAdapter() : RecyclerView.Adapter<BusStationAdapter.BusStationListHolder>() {

    constructor(busList: Array<StationDTO>) : this() {
        this.busList = busList
    }

    private var busList: Array<StationDTO>? = null
    private lateinit var context: Context
    // private lateinit var arriveInfo: ArriveDTO
    private var retryCount: Int = 0
    class BusStationListHolder(view: View): RecyclerView.ViewHolder(view) {
        // val tvStationName: TextView = view.findViewById(R.id.tv_bus_station_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BusStationListHolder {
        TODO("Not yet implemented")
    }

//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BusStationListHolder {
//        context = parent.context
//        val view = LayoutInflater.from(parent.context).inflate(R.layout.bus_station_list_item, parent, false)
//        return BusStationListHolder(view)
//    }

    override fun onBindViewHolder(holder: BusStationListHolder, position: Int) {
    }

    private fun initArriveList(station: StationDTO) {
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }
}