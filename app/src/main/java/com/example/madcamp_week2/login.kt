package com.example.madcamp_week2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.kakao.sdk.common.util.Utility
import com.example.madcamp_week2.databinding.ActivityLoginBinding
import com.example.madcamp_week2.databinding.ActivityMainBinding
class login : AppCompatActivity() {

    private lateinit var editTextUsername: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var buttonLogin: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        // XML에서 정의한 뷰들을 참조합니다.
        editTextUsername = findViewById(R.id.editTextUsername)
        editTextPassword = findViewById(R.id.editTextPassword)
        buttonLogin = findViewById(R.id.buttonLogin)

        // 로그인 버튼 클릭 시 이벤트 처리
        buttonLogin.setOnClickListener {
            val username = editTextUsername.text.toString()
            val password = editTextPassword.text.toString()

            // 여기에 로그인 처리 로직을 추가할 수 있습니다.
            // 예시로 간단한 로그인 유효성 검사를 수행합니다.
            if (username == "user" && password == "password") {
                // 로그인 성공 시
                showToast("Login successful")
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
                // 여기에 로그인 성공 후의 작업을 추가할 수 있습니다.
            } else {
                // 로그인 실패 시
                showToast("Invalid credentials")
            }
        }
    }
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}