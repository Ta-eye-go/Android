package com.code_23.ta_eye_go.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.code_23.ta_eye_go.R
import com.code_23.ta_eye_go.databinding.ActivityLoginEmailBinding
import com.code_23.ta_eye_go.ui.main.MainActivity
import kotlinx.android.synthetic.main.activity_login_email.*

class LoginEmail : AppCompatActivity() {

    lateinit var binding : ActivityLoginEmailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginEmailBinding.inflate(layoutInflater)
        //setContentView(R.layout.activity_login_email)
        setContentView(binding.root)

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
}


////로그인하기 -> 메인화면 이동
//login_btn.setOnClickListener{
//    val intent = Intent(this, MainActivity::class.java)
//    startActivity(intent)
//}
//
//member_btn.setOnClickListener {
//    val intent = Intent(this, LoginMember::class.java)
//    startActivity(intent)
//}