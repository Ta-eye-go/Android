package com.code_23.ta_eye_go.ui.bookbus


import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.code_23.ta_eye_go.R
import com.code_23.ta_eye_go.data.Message
import com.code_23.ta_eye_go.util.Constants.RECEIVE_ID
import com.code_23.ta_eye_go.util.Constants.SEND_ID
import kotlinx.android.synthetic.main.message_item.view.*


class MessagingAdapter: RecyclerView.Adapter<MessagingAdapter.MessageViewHolder>() {

    var messagesList = mutableListOf<Message>()

    inner class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        // .context : 액티비티에서 담고 있는 모든 정보, parent.context : 어댑터랑 연결될 액티비의 activity를 가져옴
        // .inflate : 붙이다, parent : 두번째 속성
        // view는 message_item에 대한것을 끌고와서 어댑터에 붙여주는 역할을 함
        return MessageViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.message_item, parent, false))
    }

    override fun getItemCount(): Int {
        return messagesList.size
    }

    @SuppressLint("SetTextI18n")
    // onCreateViewHolder로 만들어진 view를 가져다가 Bind(연결해줌)
    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val currentMessage = messagesList[position]

        when (currentMessage.id) {
            SEND_ID -> {    // 유저가 메시지를 보내면
                holder.itemView.usr_message.apply {
                    text = currentMessage.message   // 유저 메시지 저장하고
                    visibility = View.VISIBLE   // 보여줌
                }
                // bot 창은 안보이게
                holder.itemView.bot_message.visibility = View.GONE
                holder.itemView.bot_profile.visibility = View.GONE
                holder.itemView.bot_name.visibility = View.GONE
            }
            RECEIVE_ID -> { // bot이 응답할때
                holder.itemView.bot_message.apply {
                    text = currentMessage.message   // bot 응답 메시지를 저장하고
                    visibility = View.VISIBLE
                }
                // user 창은 안보이게
                holder.itemView.usr_message.visibility = View.GONE
            }
        }
    }

    fun insertMessage(message: Message) {
        this.messagesList.add(message)
        notifyItemInserted(messagesList.size)
    }

}
