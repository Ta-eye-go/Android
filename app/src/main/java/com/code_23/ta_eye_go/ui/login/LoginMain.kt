package com.code_23.ta_eye_go.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.code_23.ta_eye_go.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_login_main.*

class LoginMain : AppCompatActivity() {

    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_main)

        mAuth = FirebaseAuth.getInstance()
    }

    override fun onStart(){
        super.onStart()
        //check if user is signed in (non-null) and update UI
        var currentUser = mAuth!!.currentUser
        updateUI(currentUser)
    }

    private fun updateUI(currentUser: FirebaseUser?) {
        if (currentUser == null) {
            val intent: Intent = Intent(this, LoginEmail::class.java)
            startActivity(intent)
            finish()
        }
    }
}

////이메일로 로그인하기
//email_btn.setOnClickListener {
//    val intent = Intent(this, LoginEmail::class.java)
//    startActivity(intent)
//}