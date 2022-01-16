package com.code_23.ta_eye_go.ui.bookbus

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.code_23.ta_eye_go.R
import com.code_23.ta_eye_go.data.Message
import com.code_23.ta_eye_go.util.BotResponse
import com.code_23.ta_eye_go.util.Constants
import kotlinx.android.synthetic.main.activity_bookbus.*
import kotlinx.android.synthetic.main.activity_bookbus.rv_messages
import kotlinx.coroutines.*
import java.util.*

class ChatbotMain : AppCompatActivity(){
    private val RQ_SPEECH_REC = 102

    var messagesList = mutableListOf<Message>()
    private lateinit var adapter: MessagingAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bookbus)

        recyclerView()

        // 타이핑과 음성이 같은 액티비티 내에서 작업될 수 있도록 연구 중입니다. ㅠㅠ
        // 타이핑으로 예약하기
        text_chat_btn.setOnClickListener {
            val intent = Intent(this, Chatbot_typing::class.java)
            startActivity(intent)
        }

        // 음성으로 예약하기
       mic_btn.setOnClickListener{
           askSpeechInput()
        }

        GlobalScope.launch {
            val greetingMessage = "안녕하세요, 타이고입니다. 어흥! 현재 위치가 어디야? 라고 말해보세요!"
            delay(1000)
            withContext(Dispatchers.Main) {
                messagesList.add(Message(greetingMessage, Constants.RECEIVE_ID))
                adapter.insertMessage(Message(greetingMessage, Constants.RECEIVE_ID))

                rv_messages.scrollToPosition(adapter.itemCount - 1)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            RQ_SPEECH_REC -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    result?.let {
                        val recognizedText = it[0]
                        sendMessage(recognizedText)
                    }
                }
            }
        }
    }

    private fun recyclerView() {
        adapter = MessagingAdapter()
        rv_messages.adapter = adapter
        rv_messages.layoutManager = LinearLayoutManager(applicationContext)

    }

    private fun sendMessage(message : String) {

        if (message.isNotEmpty()) {
            messagesList.add(Message(message, Constants.SEND_ID))

            adapter.insertMessage(Message(message, Constants.SEND_ID))

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
                messagesList.add(Message(response, Constants.RECEIVE_ID))

                //Inserts our message into the adapter
                adapter.insertMessage(Message(response, Constants.RECEIVE_ID))

                //Scrolls us to the position of the latest message
                rv_messages.scrollToPosition(adapter.itemCount - 1)

            }
        }
    }

    private fun askSpeechInput() {
        if (!SpeechRecognizer.isRecognitionAvailable(this)) {
            Toast.makeText(this, "음성 인식이 가능하도록 권한을 켜주세요!", Toast.LENGTH_SHORT).show()
        }
        else {
            val i = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            i.putExtra(RecognizerIntent.EXTRA_PROMPT, "타이고에게 말해보세요")
            startActivityForResult(i, RQ_SPEECH_REC)
        }
    }
}
