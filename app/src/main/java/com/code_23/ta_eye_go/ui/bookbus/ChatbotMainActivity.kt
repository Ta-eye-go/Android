package com.code_23.ta_eye_go.ui.bookbus

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.code_23.ta_eye_go.DB.*
import com.code_23.ta_eye_go.R
import com.code_23.ta_eye_go.data.ChatMessage
import com.code_23.ta_eye_go.ui.bookmark.BookmarkMain
import com.code_23.ta_eye_go.ui.bookmark.BookmarkNew
import com.code_23.ta_eye_go.ui.main.MainActivity
import com.code_23.ta_eye_go.ui.settings.Settings
import com.google.api.gax.core.FixedCredentialsProvider
import com.google.auth.oauth2.GoogleCredentials
import com.google.auth.oauth2.ServiceAccountCredentials
import com.google.cloud.dialogflow.v2.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_after_reservation.*
import kotlinx.android.synthetic.main.activity_bookbus.*
import kotlinx.android.synthetic.main.activity_bookmark.*
import kotlinx.android.synthetic.main.activity_driver_main.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.menu_bar.*
import kotlinx.android.synthetic.main.menu_bar.view.*
import kotlinx.coroutines.*
import java.util.*

class ChatbotMainActivity : AppCompatActivity(), TextToSpeech.OnInitListener {
    private val RQ_SPEECH_REC = 102 // 음성 디코드?

    private var tts: TextToSpeech? = null    // Variable for TextToSpeech
    private var messageList = mutableListOf<ChatMessage>()

    //dialogFlow
    private var sessionsClient: SessionsClient? = null
    private var sessionName: SessionName? = null
    private val uuid = UUID.randomUUID().toString()
    private val TAG = "mainactivity"
    private lateinit var chatAdapter: ChatAdapter

    var list = mutableListOf("A", "B", "C")
    // Room DB
    private var recordDB : RecordDB? = null
    private var datamodelDB : DataModelDB? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bookbus)
        chat_menu.menu_text.text = "예약 하기"

        recordDB = RecordDB.getInstance(this)
        datamodelDB = DataModelDB.getInstance(this)
        list.clear()

        //setting adapter to recyclerview
        chatAdapter = ChatAdapter(this)
        rv_messages.adapter = chatAdapter
        rv_messages.layoutManager = LinearLayoutManager(applicationContext)

        //onclick listener to update the list and call dialogflow
        // 음성데이터 입력 (이것만 실행!)
        mic_btn.setOnClickListener {
            askSpeechInput()
        }

        back_btn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Initialize the Text To Speech
        tts = TextToSpeech(this, this)

        // 현재 정류장 확인하기
        val currentStation = intent.getStringExtra("currentStation")
        GlobalScope.launch {
            val currentSttnChecker = "현재 정류장은 $currentStation 입니다."
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
                //delay(30000)
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
//            val v : View? = rv_messages.findViewHolderForAdapterPosition(rv_messages.size-1)?.itemView
//            v?.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_FOCUSED)
            speakOut(botReply)  // bot의 응답 TTS
            if (botReply.contains("예약이 확정")){    // 예약이 확정, 챗봇으로 예약확정시 예약 데이터를 서버에서 가져옴
                val database = Firebase.database
                val bookdata = database.getReference("data").child(Firebase.auth.currentUser!!.uid)
//                datamodelDB?.datamodelDao()?.deleteAll()
                // realtime database 해당 유저 예약 기록 가져오기
                bookdata.addValueEventListener(object: ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        Log.d("gogo", "realtime db 값 오류")
                        for (item in snapshot.children){
                            Log.d("예약확정_data1", item.toString())
                            val values = item.value.toString()
                            // realtime db 값 필터링
                            val str1_values = values.replace("[","")
                            val str2_values = str1_values.replace("]","")
                            list.add(str2_values)
                            Log.d("예약확정_data2", str2_values)
                        }
                        Log.d("예약확정_data3", list.toString())
                        val recordlist = Record(list[1],list[2],list[5],list[6],list[7],list[8])
                        val datamodellist = DataModel(list[1],list[2],list[5],list[6],list[7],list[8])    // 예약 후 화면에서 사용할 변수
                        // 기사용 서버에 데이터 전송
                        //val driverdata = database.getReference("Driver")
                        val driverdata = database.getReference("Driver").child(list[6])
                        val Todriver = booklist(Firebase.auth.currentUser!!.uid,list[8],list[2],list[3])    // 현재정류장, 도착정류장, 안내견유무
                        driverdata.setValue(Todriver)
                        // 로그인한 유저 DB등록
                        val r = Runnable {
                            try {
                                recordDB?.recordDao()?.insert(recordlist)
                                datamodelDB?.datamodelDao()?.insert(datamodellist)
                            } catch (e: Exception) {
                                Log.d("tag", "Error - $e")
                            }
                        }
                        val thread = Thread(r)
                        thread.start()
                        Log.d("예약확정_data4", recordlist.toString())
                        Log.d("예약확정_data5", recordDB?.recordDao()?.getAll().toString())
                        Log.d("예약확정_data6", datamodelDB?.datamodelDao()?.getAll().toString())

                    }
                    override fun onCancelled(error: DatabaseError) {
                        Log.w(ContentValues.TAG, "Failed to read value.", error.toException())
                    }
                })
                // 예약 후 화면 이동
                val intent = Intent(this, AfterReservation::class.java)
                startActivity(intent)
                finish()
            } else if (botReply.contains("설정창")){
                // 설정 화면 이동
                val intent = Intent(this, Settings::class.java)
                startActivity(intent)
                finish()
            } else if (botReply.contains("즐겨찾기 리스트 화면")){
                // 즐겨찾기 화면 이동
                val intent = Intent(this, BookmarkMain::class.java)
                startActivity(intent)
                finish()
            } else if (botReply.contains("최근 경로 리스트 화면")){
                // 즐겨찾기 화면 이동
                val intent = Intent(this, BookmarkNew::class.java)
                startActivity(intent)
                finish()
            }
        } else {
            Toast.makeText(this, "something went wrong", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        RecordDB.destroyInstance()
        recordDB = null
        DataModelDB.destroyInstance()
        datamodelDB = null
        super.onDestroy()
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
    override fun onBackPressed() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}