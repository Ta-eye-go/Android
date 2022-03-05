package com.code_23.ta_eye_go.ui.bookmark

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.code_23.ta_eye_go.R
import com.code_23.ta_eye_go.data.Favorite
import com.code_23.ta_eye_go.data.ReservationData
import kotlinx.android.synthetic.main.activity_bookmark.*
import kotlinx.android.synthetic.main.activity_bookmark_new.*
import kotlinx.android.synthetic.main.activity_bookmark_new.bookmark_menu
import kotlinx.android.synthetic.main.alertdialog_item.view.*
import kotlinx.android.synthetic.main.menu_bar.*
import kotlinx.android.synthetic.main.menu_bar.view.*

class BookmarkNew : AppCompatActivity(), View.OnClickListener {
    private lateinit var recentRouteAdapter: RecentRouteAdapter
    private var recentRoutes = mutableListOf<ReservationData>()

    override fun onClick(v: View?) {
        // Favorite 이름 default : " "
        val recentRoute = recentRoutes[rv_recentRoutes.getChildAdapterPosition(v!!)]
        val newFavorite = Favorite(" ", recentRoute.startSttnNm, recentRoute.startSttnID, recentRoute.destination, recentRoute.destinationID, recentRoute.busNm)
        confirmDialog(newFavorite)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bookmark_new)
        bookmark_menu.menu_text.text = "즐겨찾기"

        recentRouteAdapter = RecentRouteAdapter(this)
        rv_recentRoutes.adapter = recentRouteAdapter
        rv_recentRoutes.layoutManager = LinearLayoutManager(applicationContext)
        recentRouteAdapter.setOnItemClickListener(this)

        //신규추가 버튼 눌렀을때, 신규추가 챗봇 이동
        Newline_Btn.setOnClickListener {
            val intent = Intent(this, BookmarkAdd::class.java)
            startActivity(intent)
        }

        back_btn.setOnClickListener {
            val intent = Intent(this, BookmarkMain::class.java)
            startActivity(intent)
            finish()
        }

        addRecentRouteToList("당하대주파크빌", "ICB168000392",
            "인천대입구", "ICB164000396", "8")
        addRecentRouteToList("산내마을3단지", "12345",
            "인천대학교공과대학", "12345", "16")
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun addRecentRouteToList(startSttnNm : String, startSttnID : String,
                                  destination : String, destinationID : String, busNm : String) {

        recentRoutes.add(ReservationData(startSttnNm, startSttnID, destination, destinationID, busNm))
        recentRouteAdapter.insertRecentRoute(ReservationData(startSttnNm, startSttnID, destination, destinationID, busNm))
        recentRouteAdapter.notifyDataSetChanged()
        rv_recentRoutes.scrollToPosition(0)
    }

    private fun confirmDialog(newFavorite: Favorite) {
        val layoutInflater = LayoutInflater.from(this)
        val view = layoutInflater.inflate(R.layout.alertdialog_item, null)

        val alertDialog = AlertDialog.Builder(this)
            .setView(view)
            .create()

        view.menu_name.text = "<즐겨찾기 확인>"
        view.menu_content.text = "추가하시겠습니까?"

        alertDialog.show()

        view.btn_yes.setOnClickListener {
            alertDialog.dismiss()
            val intent = Intent(this, AddName::class.java)
            intent.putExtra("newFavorite", newFavorite)
            startActivity(intent)
            finish()
        }
        view.btn_no.setOnClickListener {
            alertDialog.dismiss()
        }
    }
}