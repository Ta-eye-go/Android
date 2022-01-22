package com.code_23.ta_eye_go.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.code_23.ta_eye_go.R
import com.code_23.ta_eye_go.databinding.ActivityLoginMemberBinding
import com.code_23.ta_eye_go.ui.main.MainActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_login_member.*

class LoginMember : AppCompatActivity() {

    lateinit var binding: ActivityLoginMemberBinding
    lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginMemberBinding.inflate(layoutInflater)
        //setContentView(R.layout.activity_login_member)
        setContentView(binding.root)

        mAuth = FirebaseAuth.getInstance()


        //이전 페이지로 돌아가기
        before_btn.setOnClickListener {
            val intent = Intent(this, LoginEmail::class.java)
            startActivity(intent)
        }

    }

    fun onClick(view: View) {
        when (view) {
            binding.memberBtn -> {
                val email = binding.email.text.toString()
                val password = binding.password.text.toString()

                mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this,
                        OnCompleteListener<AuthResult?> { task ->
                            if (task.isSuccessful) {
                                Log.d("LoginMemberActivity", "createUserWithEmail:success")
                                val user: FirebaseUser? = mAuth.getCurrentUser()
                                updateUI(user)
                                finish()
                            } else {
                                Log.w(
                                    "LoginMemberActivity",
                                    "createUserWithEmail:failure",
                                    task.exception
                                )
                                Toast.makeText(
                                    this, "Authentication failed.",
                                    Toast.LENGTH_SHORT
                                ).show()
                                updateUI(null)
                            }
                        })
            }
        }
    }

    //정보 업데이트 되면 main화면 호출
    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            val intent: Intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }


    //필요할때 쓸 수 있을 것 같아서 남겨둠 (로그인 되면 ui 새로고침)
//    override fun onStart() {
//        super.onStart()
//        //check if user is signed in (non-null) and update UI
//        var currentUser = mAuth!!.currentUser
//        updateUI(currentUser)
//    }
//
//    private fun updateUI(currentUser: FirebaseUser?) {
//        if (currentUser == null) {
//            val intent: Intent = Intent(this, MainActivity::class.java)
//            startActivity(intent)
//            finish()
//        }
//    }
}

