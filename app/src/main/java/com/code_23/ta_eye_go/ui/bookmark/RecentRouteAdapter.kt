package com.code_23.ta_eye_go.ui.bookmark

import android.annotation.SuppressLint
import android.app.Activity
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.code_23.ta_eye_go.R
import com.code_23.ta_eye_go.data.ReservationData
import kotlinx.android.synthetic.main.activity_bookmark.*
import kotlinx.android.synthetic.main.bookmark_item.view.*

class RecentRouteAdapter(private var activity: Activity) : RecyclerView.Adapter<RecentRouteAdapter.ReservationViewHolder>() {

    private var recentRoutes = mutableListOf<ReservationData>()
    private lateinit var onItemClick: View.OnClickListener

    inner class ReservationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    fun setOnItemClickListener(l: View.OnClickListener) {
        onItemClick = l
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReservationViewHolder {
        val view = LayoutInflater.from(activity).inflate(R.layout.bookmark_item, parent, false)
        view.setOnClickListener(onItemClick)

        // 클릭 이벤트
        view.setOnTouchListener { v, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                v.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#D6D7D7"))
            } else if (motionEvent.action == MotionEvent.ACTION_CANCEL || motionEvent.action == MotionEvent.ACTION_UP) {
                v.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#474641"))
            }
            return@setOnTouchListener false
        }

        return ReservationViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ReservationViewHolder, position: Int) {
        val startSttnNm : String = recentRoutes[position].startSttnNm
        val destination : String = recentRoutes[position].destination
        val busNm : String = recentRoutes[position].busNm

        /* for server
        val startSttnID : String = favoriteItems[position].startSttnID
        val destinationID : String = favoriteItems[position].destinationID
        */

        apply {
            holder.itemView.bookmark_name.visibility = View.GONE
            holder.itemView.menu_btn.visibility = View.GONE
            holder.itemView.bus_end.text = destination
            holder.itemView.bus_start.text = startSttnNm
            holder.itemView.bus_number.text = busNm
        }
    }

    override fun getItemCount(): Int = recentRoutes.count()

    fun insertRecentRoute(reservationData: ReservationData) {
        this.recentRoutes.add(reservationData)
        notifyItemInserted(recentRoutes.size)
    }
}
