package com.example.madcamp_week2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import org.json.JSONObject
import java.io.IOException
import java.security.DigestException
import java.security.MessageDigest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
class SignIn : AppCompatActivity() {

    private lateinit var buttonback: ImageButton
    private lateinit var buttonSignin: Button
    private lateinit var editTextemail: EditText
    private lateinit var editTextUsername: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var editTextname: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        buttonback = findViewById(R.id.buttonback)
        buttonSignin = findViewById(R.id.buttonSignin)
        editTextemail = findViewById(R.id.editTextemail)
        editTextUsername = findViewById(R.id.editTextUsername)
        editTextPassword = findViewById(R.id.editTextPassword)
        editTextname = findViewById(R.id.editTextname)

        var email = editTextemail.text.toString().trim()
        var username = editTextUsername.text.toString().trim()
        var password = hashSHA256(editTextPassword.text.toString().trim())
        var name = editTextname.text.toString().trim()


        buttonback.setOnClickListener {
            this@SignIn.finish()
        }
        buttonSignin.setOnClickListener {
            var email = editTextemail.text.toString().trim()
            var username = editTextUsername.text.toString().trim()
            var password = hashSHA256(editTextPassword.text.toString().trim())
            var name = editTextname.text.toString().trim()

            if(email.length == 0){
                showToast("please enter email")
            }
            else if(username.length == 0){
                showToast("please enter id")
            }
            else if(password.length == 0){
                showToast("please enter password")
            }
            else if(name.length == 0){
                showToast("please enter name")
            }
            else{
                val jsonObject = JSONObject()
                jsonObject.put("email", email)
                jsonObject.put("id", username)
                jsonObject.put("password", password)
                jsonObject.put("name", name)

                val jsonString = jsonObject.toString()
                val client = OkHttpClient()

                val mediaType = "application/json; charset=utf-8".toMediaType()
                val requestBody = jsonString.toRequestBody(mediaType)

                val request = Request.Builder()
                    .url("http://172.10.7.78:80/store_data") // Replace with your server's URL
                    .post(requestBody)
                    .build()

                client.newCall(request).enqueue(object : okhttp3.Callback {
                    override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                        // Handle the server response here if needed
                        val responseBody = response.body?.string()
                        // Handle response
                    }

                    override fun onFailure(call: okhttp3.Call, e: IOException) {
                        // Handle failure
                        e.printStackTrace()
                    }
                })
            }
        }


    }
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
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