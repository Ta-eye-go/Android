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
    private val RQ_SPEECH_REC = 102 // 음성 디코드?

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

        // 이것만 사용!
        // 음성으로 예약하기
        mic_btn.setOnClickListener{
            askSpeechInput()
        }

        GlobalScope.launch {
            val greetingMessage = "어흥! 승차 예약을 원하시면 현재 위치가 어디야? 라고 말해보세요!"
            delay(1000)
            withContext(Dispatchers.Main) {
                messagesList.add(Message(greetingMessage, Constants.RECEIVE_ID))
                adapter.insertMessage(Message(greetingMessage, Constants.RECEIVE_ID))
                rv_messages.scrollToPosition(adapter.itemCount - 1)
            }
        }
    }

    // STT, SPEECH_INPUT
    // main액티비티에서 sub액티비티를 호출하여 넘어갔다가, 다시 main 액티비티로 돌아올때 사용되는 기본 메소드
    // sub액티비티에서 뒤로가기버튼을 만들던 핸드폰 내에있는 뒤로가기 버튼을 누르던 onActivityResult() 메소드는 실행
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            RQ_SPEECH_REC -> {  // 음성 데이터가 입력되면
                if (resultCode == Activity.RESULT_OK && data != null) {
                    val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)   // 음성 데이터를 받아옴
                    // result? : result가 null이든 null이 아니든 실행
                    result?.let {
                        val recognizedText = it[0]  // 정확도가 높은 텍스트를 recognizedText 넣음
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
            messagesList.add(Message(message, Constants.SEND_ID))   // user가 보낸 메시지와 챗팅창을 messagesList에 넣어줌
            adapter.insertMessage(Message(message, Constants.SEND_ID))  // user가 보낸 메시지와 챗팅창을 MessagingAdapter에 넣어줌
            rv_messages.scrollToPosition(adapter.itemCount - 1)
            botResponse(message)    // 처음 현재 정류장 확인할때만 필요
        }
    }

    private fun botResponse(message: String) {

        GlobalScope.launch {
            //Fake response delay
            delay(1000)
            // 1000ms 후에 다음을 실행
            withContext(Dispatchers.Main) {
                //Gets the response
                val response = BotResponse.basicResponses(message)  // "현재 정류장은 $result 입니다!" 받아옴
                //Adds it to our local list
                messagesList.add(Message(response, Constants.RECEIVE_ID))   // bot이 보낸 메시지와 챗팅창을 messagesList에 넣어줌
                //Inserts our message into the adapter
                adapter.insertMessage(Message(response, Constants.RECEIVE_ID))  // bot이 보낸 메시지와 챗팅창을 MessagingAdapter에 넣어줌
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
            val i = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)    // 자연어처리 비슷한 뭔가
            i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            i.putExtra(RecognizerIntent.EXTRA_PROMPT, "타이고에게 말해보세요")
            startActivityForResult(i, RQ_SPEECH_REC)
        }
    }
}
