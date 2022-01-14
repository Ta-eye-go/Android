package com.code_23.ta_eye_go.ui.bookbus

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.code_23.ta_eye_go.R
import kotlinx.android.synthetic.main.activity_bookbus.*
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class ChatbotMain : AppCompatActivity(){
    private val RQ_SPEECH_REC = 102

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bookbus)

        // 타이핑으로 예약하기
        text_chat_btn.setOnClickListener {
            val intent = Intent(this, Chatbot_typing::class.java)
            startActivity(intent)
        }

        // 음성으로 예약하기기
       mic_btn.setOnClickListener{
            askSpeechInput()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RQ_SPEECH_REC && resultCode == Activity.RESULT_OK){
            val result = data?.getStringArrayExtra((RecognizerIntent.EXTRA_RESULTS))
            // tv_text.text = result?.get(0).toString()
        }
    }
    private fun askSpeechInput() {
        if (!SpeechRecognizer.isRecognitionAvailable(this)) {
            Toast.makeText(this, "음성 인식이 가능하도록 권한을 켜주세요!", Toast.LENGTH_SHORT).show()
        }
        else {
            val i = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR")
            i.putExtra(RecognizerIntent.EXTRA_PROMPT, "타이고에게 말해보세요")
            startActivityForResult(i, RQ_SPEECH_REC)
        }
    }
}
