package com.code_23.ta_eye_go.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.code_23.ta_eye_go.R
import com.code_23.ta_eye_go.databinding.ActivityLoginEmailBinding
import com.code_23.ta_eye_go.ui.main.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_login_email.*

class LoginEmail : AppCompatActivity() {

    private var mAuth: FirebaseAuth? = null
    lateinit var binding : ActivityLoginEmailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginEmailBinding.inflate(layoutInflater)
        //setContentView(R.layout.activity_login_email)
        setContentView(binding.root)

        mAuth = FirebaseAuth.getInstance()

        // 로그인 버튼
        login_btn.setOnClickListener {
            signIn(email.text.toString(),password.text.toString())
        }
    }

    // 로그아웃하지 않을 시 자동 로그인 , 회원가입시 바로 로그인 됨
    public override fun onStart() {
        super.onStart()
        moveMainPage(mAuth?.currentUser)
    }


    fun onClick(view: View) {
        when(view){
            binding.memberBtn -> {
                val intent: Intent = Intent(this, LoginMember::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    // 로그인
    private fun signIn(email: String, password: String) {

        if (email.isNotEmpty() && password.isNotEmpty()) {
            mAuth?.signInWithEmailAndPassword(email, password)
                ?.addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                            baseContext, "로그인에 성공 하였습니다.",
                            Toast.LENGTH_SHORT
                        ).show()
                        moveMainPage(mAuth?.currentUser)
                    } else {
                        Toast.makeText(
                            baseContext, "로그인에 실패 하였습니다.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }

    // 유저정보 넘겨주고 메인 액티비티 호출
    fun moveMainPage(user: FirebaseUser?){
        if( user!= null){
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }
    }
}
