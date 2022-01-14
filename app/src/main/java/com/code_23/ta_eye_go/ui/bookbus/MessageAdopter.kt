package com.code_23.ta_eye_go.ui.bookbus

//import android.annotation.SuppressLint
//import android.content.Context
//import android.util.Log
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.core.view.LayoutInflaterCompat
//import androidx.recyclerview.widget.RecyclerView
//import androidx.core.view.LayoutInflaterFactory
//import com.code_23.ta_eye_go.R
//import com.code_23.ta_eye_go.data.Message
//import kotlinx.android.synthetic.main.message_item.view.*
//
//class MessageAdopter(val viewModel:ChatViewModel):RecyclerView.Adapter<RecyclerView.ViewHolder>() {
//
//    var messagesList = mutableListOf<Message>()
//
//    inner class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
//        init {
//            itemView.setOnClickListener {
//                messagesList.removeAt(adapterPosition)
//                notifyItemRemoved(adapterPosition)
//            }
//        }
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
//        return MessageViewHolder(LayoutInflaterCompat.from(perent.context).inflate(R.layout.message_item, perent, false))
//    }
//
//    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
//        val currentMessage = messagesList[position]
//
//        when (currentMessage.id) {
//            holder.itemView.usr_message.apply {
//                text = currentMessage.message
//                visibility = View.VISIBLE
//            }
//
//        }
//    }
//
//    override fun getItemCount(): Int {
//        return messagesList.size
//    }
//}