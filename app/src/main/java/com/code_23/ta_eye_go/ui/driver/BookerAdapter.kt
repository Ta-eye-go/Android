package com.code_23.ta_eye_go.ui.driver

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.code_23.ta_eye_go.R
import com.code_23.ta_eye_go.data.BookerData
import kotlinx.android.synthetic.main.bookers_item.view.*
import kotlinx.coroutines.delay

class BookerAdapter (private var activity: Activity) : RecyclerView.Adapter<BookerAdapter.BookerHolder>() {

    private var reservations = mutableListOf<BookerData>()

    inner class BookerHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookerHolder {
        val view = LayoutInflater.from(activity).inflate(R.layout.bookers_item, parent, false)
        return BookerHolder(view)
    }

    override fun onBindViewHolder(holder: BookerHolder, position: Int) {
        val startSttn : String = reservations[position].startSttn
        // var endSttn: String = reservations[position].endSttn
        val guideDog: Boolean = reservations[position].guideDog

        if (guideDog) {
            holder.itemView.booker_station.isSelected = true
            holder.itemView.booker_station.text = startSttn
            holder.itemView.guide_dog.visibility = View.VISIBLE
        }
        else {
            holder.itemView.booker_station.isSelected = true
            holder.itemView.booker_station.text = startSttn
            holder.itemView.guide_dog.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int = reservations.count()

    fun insertBooker(bookerData: BookerData) {
        this.reservations.add(bookerData)
        notifyItemInserted(reservations.size)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun removeBooker(position: Int) {
        reservations.remove(reservations[position])
        notifyItemRemoved(position)
    }

}