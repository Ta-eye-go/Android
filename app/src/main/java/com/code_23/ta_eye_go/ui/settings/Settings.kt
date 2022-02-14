package com.code_23.ta_eye_go.ui.settings

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.code_23.ta_eye_go.DB.User
import com.code_23.ta_eye_go.DB.UserDB
import com.code_23.ta_eye_go.R
import com.code_23.ta_eye_go.ui.bookbus.AfterReservation
import com.code_23.ta_eye_go.ui.bookbus.InBus
import com.code_23.ta_eye_go.ui.login.LoginMain
import com.code_23.ta_eye_go.ui.main.MainActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.kakao.sdk.user.UserApiClient
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.activity_settings.menu
import kotlinx.android.synthetic.main.menu_bar.*
import kotlinx.android.synthetic.main.menu_bar.view.*

class Settings : AppCompatActivity(){

    // 로그아웃 구현을 위한 변수
    var auth : FirebaseAuth ?= null
    var googleSignInClient : GoogleSignInClient?= null
    // UserDB
    private var userDB : UserDB? = null
    private var userList = listOf<User>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        menu.menu_text.text = "설정"

        // Room DB
        userDB = UserDB.getInstance(this)

        // 구글 로그아웃을 위해 로그인 세션 가져오기
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // firebaseauth를 사용하기 위한 인스턴스 get
        auth = FirebaseAuth.getInstance()

        text_logout.setOnClickListener {

            UserApiClient.instance.accessTokenInfo { tokenInfo, error ->
                if (error != null) {
                    // 구글 로그아웃
                    FirebaseAuth.getInstance().signOut()
                    googleSignInClient?.signOut()

                    var logoutIntent = Intent(this, LoginMain::class.java)
                    logoutIntent.flags =
                        Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(logoutIntent)
                    finish()
                }
                else if (tokenInfo != null) {
                    // 카카오 로그아웃, 로그아웃 눌렀을때 로그아웃+팝업, 로그인 화면으로 전환
                    UserApiClient.instance.logout { error ->
                        if (error != null) {
                            Toast.makeText(this, "로그아웃 실패 $error", Toast.LENGTH_SHORT).show()
                        }else {
                            Toast.makeText(this, "로그아웃 성공", Toast.LENGTH_SHORT).show()
                        }
                        val intent = Intent(this, LoginMain::class.java)
                        startActivity(intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP))
                        finish()
                    }
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }

        //간단 사용 설명서
        text_guide.setOnClickListener {
                val intent = Intent(this, Guide1::class.java)
                startActivity(intent)
        }

        // 예약 테스트 용 입니다. 무시해주세요!!
        btn_yellow.setOnClickListener {
            val intent = Intent(this, AfterReservation::class.java)
            startActivity(intent)
        }
        btn_blue.setOnClickListener {
            val intent = Intent(this, InBus::class.java)
            startActivity(intent)
        }
        switch_dog.setOnClickListener{
            val r = kotlinx.coroutines.Runnable {
                try {
                    userList = userDB?.userDao()?.getAll()!!

                    val user = Firebase.auth.currentUser
                    var email = ""
                    email = Firebase.auth.currentUser?.email.toString()
                    var users = User(email, 1)
                    userDB?.userDao()?.updateUser(users)
                } catch (e: Exception) {
                    Log.d("tag", "Error - $e")
                }
            }
            val thread = Thread(r)
            thread.start()
        }
    }
    override fun onDestroy() {
        UserDB.destroyInstance()
        userDB = null
        super.onDestroy()
    }
}
