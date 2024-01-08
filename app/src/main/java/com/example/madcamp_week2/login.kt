package com.example.madcamp_week2

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.android.volley.Response
import com.example.madcamp_week2.databinding.ActivityLoginBinding
import com.example.madcamp_week2.databinding.ActivityMainBinding
import com.google.firebase.auth.UserInfo
import com.google.gson.Gson
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.user.UserApiClient
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import java.security.DigestException
import java.security.MessageDigest

class login : AppCompatActivity() {

    private lateinit var editTextUsername: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var buttonLogin: Button
    private lateinit var buttonSignin: Button
    private lateinit var buttonkakaoLogin: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPreferences = getSharedPreferences("MyAppPreferences", MODE_PRIVATE)
        val username = sharedPreferences.getString("USERNAME", "")
        if(!username.isNullOrEmpty()){
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        setContentView(R.layout.activity_login)

        Log.d("hash_test",hashSHA256("pw1"))
        // XML에서 정의한 뷰들을 참조합니다.
        editTextUsername = findViewById(R.id.editTextUsername)
        editTextPassword = findViewById(R.id.editTextPassword)
        buttonLogin = findViewById(R.id.buttonLogin)
        buttonkakaoLogin = findViewById(R.id.buttonkakaoLogin)
        buttonSignin = findViewById(R.id.buttonSignin)
        // 로그인 버튼 클릭 시 이벤트 처리
        buttonLogin.setOnClickListener {
            val username = editTextUsername.text.toString()
            val password = editTextPassword.text.toString()
            loginWithServer(username, password)

        }
        buttonSignin.setOnClickListener {
            val intent = Intent(this, SignIn::class.java)
            startActivity(intent)
        }
        buttonkakaoLogin.setOnClickListener {
            loginWithKakao()
        }
    }

    private fun loginWithServer(id: String, password: String) {
        val url = "http://172.10.7.78/logininserver"

        val jsonObject = JSONObject()
        jsonObject.put("id", id)
        jsonObject.put("password", hashSHA256(password))

        val jsonString = jsonObject.toString()
        val client = OkHttpClient()

        val mediaType = "application/json; charset=utf-8".toMediaType()
        val requestBody = jsonString.toRequestBody(mediaType)

        val request = Request.Builder()
            .url(url) // Replace with your server's URL
            .post(requestBody)
            .build()
        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                if (response.isSuccessful) { // HTTP 응답 코드가 200인 경우
                    val responseBodyString = response.body?.string()
                    val jsonObject = JSONObject(responseBodyString)
                    val nameValue = jsonObject.optString("name")

                    val sharedPreferences = getSharedPreferences("MyAppPreferences", MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.putString("USERNAME", nameValue)
                    editor.apply()
                    val intent = Intent(this@login, MainActivity::class.java)
                    startActivity(intent)
                } else {
                }
                response.body?.close()
            }

            override fun onFailure(call: okhttp3.Call, e: IOException) {
                // Handle failure
                e.printStackTrace()
            }
        })
    }

    private fun loginWithKakao() {
        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                Log.e(TAG, "카카오계정으로 로그인 실패", error)
            } else if (token != null) {
                Log.i(TAG, "카카오계정으로 로그인 성공 ${token.accessToken}")
            }
        }

        if (UserApiClient.instance.isKakaoTalkLoginAvailable(this)) {
            UserApiClient.instance.loginWithKakaoTalk(this) { token, error ->
                if (error != null) {
                    Log.e(TAG, "카카오톡으로 로그인 실패", error)
                    if (error is ClientError) {
                        return@loginWithKakaoTalk
                    }
                    UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
                } else if (token != null) {
                    Log.i(TAG, "카카오톡으로 로그인 성공 ${token.accessToken}")
                }
            }
        } else {
            UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
        }
        UserApiClient.instance.me { user, error ->
            if (error != null) {
                Log.e(TAG, "사용자 정보 요청 실패", error)
            } else if (user != null) {
                val id = user.kakaoAccount?.email
                val password = user.id.toString()
                val name = user.kakaoAccount?.profile?.nickname
                // 서버로 사용자 정보 전송
                sendUserInfoToServer(id, password, name)
            }
        }
    }
    private fun sendUserInfoToServer(id: String?, password: String?, name: String?): Boolean {
        val url = "http://172.10.7.78/loginwkakao" // 서버의 API 엔드포인트

        val jsonObject = JSONObject()
        jsonObject.put("id", id)
        jsonObject.put("password", hashSHA256(password))
        jsonObject.put("name", name)

        val jsonString = jsonObject.toString()
        val client = OkHttpClient()

        val mediaType = "application/json; charset=utf-8".toMediaType()
        val requestBody = jsonString.toRequestBody(mediaType)

        val request = Request.Builder()
            .url(url) // Replace with your server's URL
            .post(requestBody)
            .build()
        var result = false
        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                if (response.isSuccessful) { // HTTP 응답 코드가 200인 경우
                    val sharedPreferences = getSharedPreferences("MyAppPreferences", MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.putString("USERNAME", name)
                    editor.apply()
                    result = true
                    val intent = Intent(this@login, MainActivity::class.java)
                    startActivity(intent)
                } else {
                    result = false
                }
                response.body?.close()
            }

            override fun onFailure(call: okhttp3.Call, e: IOException) {
                // Handle failure
                e.printStackTrace()
                result = false
            }
        })
        return result
    }


    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
    // sha-256 hashing

    fun hashSHA256(msg: String?):String {
        val hash: ByteArray
        try {
            val md = MessageDigest.getInstance("SHA-256")
            md.update(msg!!.toByteArray())
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