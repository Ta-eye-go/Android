package com.code_23.ta_eye_go.ui.bookbus

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.code_23.ta_eye_go.R
import com.code_23.ta_eye_go.data.ChatMessage
import com.google.api.gax.core.FixedCredentialsProvider
import com.google.auth.oauth2.GoogleCredentials
import com.google.auth.oauth2.ServiceAccountCredentials
import com.google.cloud.dialogflow.v2.*
import kotlinx.android.synthetic.main.activity_bookbus.*
import kotlinx.coroutines.*
import java.util.*

class ChatbotMainActivity : AppCompatActivity(), TextToSpeech.OnInitListener {
    private val RQ_SPEECH_REC = 102 // 음성 디코드?

    private var tts: TextToSpeech? = null    // Variable for TextToSpeech
    var messageList = mutableListOf<ChatMessage>()
    // private var messageList: ArrayList<ChatMessage> = ArrayList()

    //dialogFlow
    private var sessionsClient: SessionsClient? = null
    private var sessionName: SessionName? = null
    private val uuid = UUID.randomUUID().toString()
    private val TAG = "mainactivity"
    private lateinit var chatAdapter: ChatAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bookbus)

        //setting adapter to recyclerview
        // chatAdapter = ChatAdapter(this, messageList)
        chatAdapter = ChatAdapter(this)
        rv_messages.adapter = chatAdapter
        rv_messages.layoutManager = LinearLayoutManager(applicationContext)

        //onclick listener to update the list and call dialogflow
        // 음성데이터 입력 (이것만 실행!)
        mic_btn.setOnClickListener {
            askSpeechInput()
        }

        // Initialize the Text To Speech
        tts = TextToSpeech(this, this)

        // 현재 정류장 확인하기
        val currentStation = intent.getStringExtra("currentStation")
        GlobalScope.launch {
            val currentSttnChecker = "현재 정류장은 ${currentStation} 입니다."
            delay(1000)
            withContext(Dispatchers.Main) {
                addMessageToList(currentSttnChecker, true)
                speakOut(currentSttnChecker)
            }
        }

        /*// 키보드 입력
        btnSend.setOnClickListener {
            val message: String = editMessage.text.toString()   // message : 입력한 텍스트 테이터
            if (message.isNotEmpty()) { // 데이터를 입력했으면
                addMessageToList(message, false)
                sendMessageToBot(message)
            } else {
                Toast.makeText(this@ChatbotMainActivity, "Please enter text!", Toast.LENGTH_SHORT).show()
            }
        }*/

        //initialize bot config
        setUpBot()
    }


    // 음성 입력 후 자연어처리
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
                        if (recognizedText.isNotEmpty()) { // 데이터를 입력했으면
                            addMessageToList(recognizedText, false)
                            sendMessageToBot(recognizedText)
                        } else {
                            Toast.makeText(this@ChatbotMainActivity, "다시 말씀해주세요!", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    // handles UI changes
    private fun addMessageToList(message: String, isReceived: Boolean) {
        messageList.add(ChatMessage(message, isReceived))   // messageList에 추가해주고
        chatAdapter.insertMessage(ChatMessage(message, isReceived))
        chatAdapter.notifyDataSetChanged()  // chatAdapter의 리스트의 크기와 아이템이 둘 다 변경
        rv_messages.scrollToPosition(messageList.size - 1)
    }

    // initiates the dialogflow
    private fun setUpBot() {
        try {
            val stream = this.resources.openRawResource(R.raw.credential)
            val credentials: GoogleCredentials = GoogleCredentials.fromStream(stream)
                .createScoped("https://www.googleapis.com/auth/cloud-platform")
            val projectId: String = (credentials as ServiceAccountCredentials).projectId
            val settingsBuilder: SessionsSettings.Builder = SessionsSettings.newBuilder()
            val sessionsSettings: SessionsSettings = settingsBuilder.setCredentialsProvider(
                FixedCredentialsProvider.create(credentials)
            ).build()
            sessionsClient = SessionsClient.create(sessionsSettings)
            sessionName = SessionName.of(projectId, uuid)
            Log.d(TAG, "projectId : $projectId")
        } catch (e: Exception) {
            Log.d(TAG, "setUpBot: " + e.message)
        }
    }

    // user가 bot에게 메시지를 보냄
    private fun sendMessageToBot(message: String) {
        val input = QueryInput.newBuilder().setText(TextInput.newBuilder().setText(message).setLanguageCode("ko-KR")).build()
        GlobalScope.launch {
            sendMessageInBg(input)
        }
    }

    // dialogflow에게 메시지를 보냄
    private suspend fun sendMessageInBg(
        queryInput: QueryInput
    ) {
        withContext(Dispatchers.Default) {
            try {
                val detectIntentRequest = DetectIntentRequest.newBuilder()
                    .setSession(sessionName.toString())
                    .setQueryInput(queryInput)
                    .build()
                val result = sessionsClient?.detectIntent(detectIntentRequest)
                if (result != null) {
                    runOnUiThread {
                        updateUI(result)
                    }
                }
            } catch (e: java.lang.Exception) {
                Log.d(TAG, "doInBackground: " + e.message)
                e.printStackTrace()
            }
        }
    }

    private fun updateUI(response: DetectIntentResponse) {
        val botReply: String = response.queryResult.fulfillmentText
        if (botReply.isNotEmpty()) {
            addMessageToList(botReply, true)
            speakOut(botReply)  // bot의 응답 TTS
        } else {
            Toast.makeText(this, "something went wrong", Toast.LENGTH_SHORT).show()
        }
    }

    // TTS
    override  fun onInit(status:Int){
        if(status == TextToSpeech.SUCCESS){
            // TTS 언어 설정
            val result = tts!!.setLanguage(Locale.KOREA)
            if(result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED){
                Log.e("TTS", "지정한 언어가 지원되지 않습니다.")
            }
        }
        else{
            Log.e("TTS", "초기화 실패")
        }
    }
    // TTS
    private fun speakOut(text: String) {
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
    }

}