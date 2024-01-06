package com.example.madcamp_week2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.madcamp_week2.databinding.ActivityLoginBinding
import com.example.madcamp_week2.databinding.ActivityMainBinding
import java.security.DigestException
import java.security.MessageDigest

class login : AppCompatActivity() {

    private lateinit var editTextUsername: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var buttonLogin: Button
    private lateinit var buttonkakaoLogin: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        Log.d("hash_test",hashSHA256("pw1"))
        // XML에서 정의한 뷰들을 참조합니다.
        editTextUsername = findViewById(R.id.editTextUsername)
        editTextPassword = findViewById(R.id.editTextPassword)
        buttonLogin = findViewById(R.id.buttonLogin)
        buttonkakaoLogin = findViewById(R.id.buttonkakaoLogin)

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
        buttonkakaoLogin.setOnClickListener {  }
    }
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
    // sha-256 hashing

    fun hashSHA256(msg: String):String {
        val hash: ByteArray
        try {
            val md = MessageDigest.getInstance("SHA-256")
            md.update(msg.toByteArray())
            hash = md.digest()
        } catch (e: CloneNotSupportedException) {
            throw DigestException("couldn't make digest of partial content");
        }

        return toHexString(hash)
    }
    private val digits = "0123456789ABCDEF"
    fun toHexString(byteArray: ByteArray): String {

        val hexChars = CharArray(byteArray.size * 2)
        for (i in byteArray.indices) {
            val v = byteArray[i].toInt() and 0xff
            hexChars[i * 2] = digits[v shr 4]
            hexChars[i * 2 + 1] = digits[v and 0xf]
        }
        return String(hexChars)
    }
}