package com.code_23.ta_eye_go.ui.bookbus

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.*
import com.code_23.ta_eye_go.R
import com.code_23.ta_eye_go.data.Message
import com.code_23.ta_eye_go.util.BotResponse
import com.code_23.ta_eye_go.util.Constants.RECEIVE_ID
import com.code_23.ta_eye_go.util.Constants.SEND_ID
import kotlinx.android.synthetic.main.activity_bookbus_typing.*
import android.content.Intent
import android.view.KeyEvent
import android.view.View
import android.widget.EditText

class Chatbot_typing : AppCompatActivity(){

    //You can ignore this messageList if you're coming from the tutorial,
    // it was used only for my personal debugging
    var messagesList = mutableListOf<Message>()
    private lateinit var adapter: MessagingAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bookbus_typing)

        recyclerView()

//        mic_btn.setOnClickListener {
//            val intent = Intent(this, ChatbotMain::class.java)
//            startActivity(intent)
//        }

        clickEvents()

        GlobalScope.launch {
            val greetingMessage = "안녕하세요, 타이고입니다. 임시로 마이크 버튼을 전송 버튼으로 사용하고 있습니다!"
            delay(1000)
            withContext(Dispatchers.Main) {
                messagesList.add(Message(greetingMessage, RECEIVE_ID))
                adapter.insertMessage(Message(greetingMessage, RECEIVE_ID))

                rv_messages.scrollToPosition(adapter.itemCount - 1)
            }
        }
    }

    private fun clickEvents() {

        mic_btn.setOnClickListener {
            sendMessage()
        }

        //Scroll back to correct position when user clicks on text view
        et_message.setOnClickListener {
            GlobalScope.launch {
                delay(100)

                withContext(Dispatchers.Main) {
                    rv_messages.scrollToPosition(adapter.itemCount - 1)

                }
            }
        }
    }

    private fun recyclerView() {
        adapter = MessagingAdapter()
        rv_messages.adapter = adapter
        rv_messages.layoutManager = LinearLayoutManager(applicationContext)

    }

    override fun onStart() {
        super.onStart()
        //In case there are messages, scroll to bottom when re-opening app
        GlobalScope.launch {
            delay(100)
            withContext(Dispatchers.Main) {
                rv_messages.scrollToPosition(adapter.itemCount - 1)
            }
        }
    }

    private fun sendMessage() {
        val message = et_message.text.toString()

        if (message.isNotEmpty()) {
            //Adds it to our local list
            messagesList.add(Message(message, SEND_ID))
            et_message.setText("")

            adapter.insertMessage(Message(message, SEND_ID))
            rv_messages.scrollToPosition(adapter.itemCount - 1)

            botResponse(message)
        }
    }

    private fun botResponse(message: String) {

        GlobalScope.launch {
            //Fake response delay
            delay(1000)

            withContext(Dispatchers.Main) {
                //Gets the response
                val response = BotResponse.basicResponses(message)

                //Adds it to our local list
                messagesList.add(Message(response, RECEIVE_ID))

                //Inserts our message into the adapter
                adapter.insertMessage(Message(response, RECEIVE_ID))

                //Scrolls us to the position of the latest message
                rv_messages.scrollToPosition(adapter.itemCount - 1)

            }
        }
    }
}
