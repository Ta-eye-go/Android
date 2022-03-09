package com.code_23.ta_eye_go.ui.bookmark

import android.annotation.SuppressLint
import android.app.Activity
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.*
import androidx.recyclerview.widget.RecyclerView
import com.code_23.ta_eye_go.R
import com.code_23.ta_eye_go.data.Favorite
import kotlinx.android.synthetic.main.bookmark_item.view.*

class BookmarkAdapter(private var activity: Activity) : RecyclerView.Adapter<BookmarkAdapter.FavoriteViewHolder>(){

    private var favoriteItems = mutableListOf<Favorite>()
    private lateinit var onItemClick: View.OnClickListener
    private lateinit var onCreateContextMenu: View.OnCreateContextMenuListener

    inner class FavoriteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    fun setOnItemClickListener(l: View.OnClickListener){
        onItemClick = l
    }

    fun setOnCreateContextMenuListener(l : View.OnCreateContextMenuListener){
        onCreateContextMenu = l
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val view = LayoutInflater.from(activity).inflate(R.layout.bookmark_item, parent, false)
        view.setOnClickListener(onItemClick)
        view.setOnCreateContextMenuListener(onCreateContextMenu)

        // 클릭 이벤트
        view.setOnTouchListener { view, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                view.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#D6D7D7"))
            } else if (motionEvent.action == MotionEvent.ACTION_CANCEL || motionEvent.action == MotionEvent.ACTION_UP) {
                view.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#474641"))
            }
            return@setOnTouchListener false
        }
        return FavoriteViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        val favoriteNm : String = favoriteItems[position].favoriteNm// 즐겨찾기 이름
        val startSttnNm : String = favoriteItems[position].startSttnNm
        val destination : String = favoriteItems[position].destination
        val busNm : String = favoriteItems[position].busNm

        /* for server
        val startSttnID : String = favoriteItems[position].startSttnID
        val destinationID : String = favoriteItems[position].destinationID
        */

        apply {
            // 너무 긴 텍스트 처리
            if (favoriteNm.length > 15) holder.itemView.bookmark_name.text = favoriteNm.substring(0,13) + "..."
            else holder.itemView.bookmark_name.text = favoriteNm
            holder.itemView.menu_btn.visibility = View.GONE
            holder.itemView.bus_end.text = destination
            holder.itemView.bus_start.text = startSttnNm
            holder.itemView.bus_number.text = busNm
        }
    }

    override fun getItemCount(): Int = favoriteItems.count()

    fun insertFavorite(favorite: Favorite) {
        this.favoriteItems.add(favorite)
        notifyItemInserted(favoriteItems.size)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun removeBookmark(position: Int) {
        favoriteItems.remove(favoriteItems[position])
        notifyItemRemoved(position)
    }
}