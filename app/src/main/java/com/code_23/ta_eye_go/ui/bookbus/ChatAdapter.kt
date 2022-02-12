package com.code_23.ta_eye_go.ui.bookbus

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.code_23.ta_eye_go.R
import com.code_23.ta_eye_go.data.ChatMessage
import com.code_23.ta_eye_go.ui.bookbus.ChatAdapter.ChatViewHolder
import kotlinx.android.synthetic.main.message_item.view.*

// class ChatAdapter(private var activity: Activity, private var messageList: List<ChatMessage>) : RecyclerView.Adapter<ChatViewHolder>() {
class ChatAdapter(private var activity: Activity) : RecyclerView.Adapter<ChatViewHolder>() {

    private var messageList = mutableListOf<ChatMessage>()

    inner class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

//    class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//         var messageReceive: TextView = itemView.findViewById(R.id.bot_message)  // bot, .findViewById : 특정 xml에서 id값들을 찾아올 수 있음
//         var messageSend: TextView = itemView.findViewById(R.id.usr_message)    // user
//    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        // .context : 액티비티에서 담고 있는 모든 정보, parent.context : 어댑터랑 연결될 액티비의 activity를 가져옴
        // .inflate : 붙이다, parent : 두번째 속성
        // view는 message_item에 대한것을 끌고와서 어댑터에 붙여주는 역할을 함
        val view = LayoutInflater.from(activity).inflate(R.layout.message_item, parent, false)
        return ChatViewHolder(view)
    }

    override fun getItemCount(): Int {
        return messageList.count()
    }

    // onCreateViewHolder로 만들어진 view를 가져다가 Bind(연결해줌)
    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val message: String = messageList[position].message
        val isReceived: Boolean = messageList[position].isReceived
        if (isReceived) {   // bot 메시지 보이기
            holder.itemView.bot_message.apply {
                text = message   // bot 응답 메시지를 저장하고
                visibility = View.VISIBLE
            }
            holder.itemView.usr_message.visibility = View.GONE
            holder.itemView.bot_profile.visibility = View.VISIBLE
            holder.itemView.bot_name.visibility = View.VISIBLE
        }
        else {  // user 메시지 보이기
            holder.itemView.usr_message.apply {
                text = message   // 유저 메시지 저장하고
                visibility = View.VISIBLE   // 보여줌
            }
            holder.itemView.bot_message.visibility = View.GONE
            holder.itemView.bot_profile.visibility = View.GONE
            holder.itemView.bot_name.visibility = View.GONE
        }
    }

    fun insertMessage(message: ChatMessage) {
        this.messageList.add(message)
        notifyItemInserted(messageList.size)
    }

}