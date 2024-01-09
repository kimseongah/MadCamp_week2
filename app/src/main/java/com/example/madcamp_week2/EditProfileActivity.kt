package com.example.madcamp_week2

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.InputType
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.textfield.TextInputEditText
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.internal.notify
import okio.BufferedSink
import okio.source
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream
import java.security.DigestException
import java.security.MessageDigest

class EditProfileActivity : AppCompatActivity() {
    private val client = OkHttpClient()

    private val PICK_IMAGE = 1
    private var imageUri = Uri.EMPTY
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        val sharedPreferences = getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE)
        val id = sharedPreferences.getString("ID", "No ID")!!

        loadUserProfile(id)

        val backImageView = findViewById<ImageView>(R.id.back_image_view) // 이미지뷰의 ID를 맞게 설정하세요.
        backImageView.setOnClickListener {
            setResult(Activity.RESULT_OK)
            finish() // 현재 액티비티 종료
        }

        val imageView = findViewById<ImageView>(R.id.editImage)
        imageView.setOnClickListener {
            val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(galleryIntent, PICK_IMAGE)
        }


        val confirmButton = findViewById<CardView>(R.id.confirmButton)
        confirmButton.setOnClickListener {
            val name = findViewById<TextInputEditText>(R.id.editName).text.toString().takeIf { it.isNotEmpty() }
            val currentPassword = findViewById<TextInputEditText>(R.id.currentPassword).text.toString().takeIf { it.isNotEmpty() }
            val newPassword = findViewById<TextInputEditText>(R.id.newPassword).text.toString().takeIf { it.isNotEmpty() }
            val checkNewPassword = findViewById<TextInputEditText>(R.id.checkNewPassword).text.toString().takeIf { it.isNotEmpty() }

            val isPasswordChangeRequested = currentPassword != null && newPassword != null && checkNewPassword != null

            if (isPasswordChangeRequested && newPassword != checkNewPassword) {
                runOnUiThread {
                    Toast.makeText(this, "새 비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
                }
                return@setOnClickListener
            }

            val hashedCurrentPassword = if (isPasswordChangeRequested) hashSHA256(currentPassword!!) else null
            val hashedNewPassword = if (isPasswordChangeRequested) hashSHA256(newPassword!!) else null

            // 프로필 업데이트 함수를 호출합니다.
            updateProfile(id, name, hashedCurrentPassword, hashedNewPassword, imageUri)
        }

        val logoutButton = findViewById<CardView>(R.id.logout)
        logoutButton.setOnClickListener {
            // SharedPreferences에서 사용자 정보 삭제
            sharedPreferences.edit().clear().apply()

            // 로그인 액티비티로 이동
            val intent = Intent(this, login::class.java)
            startActivity(intent)
            finish() // 현재 액티비티 종료
        }

        val withdrawButton = findViewById<CardView>(R.id.withdraw)
        withdrawButton.setOnClickListener {
            showPasswordDialog(id)

            sharedPreferences.edit().clear().apply()
            val intent = Intent(this, login::class.java)
            startActivity(intent)
            finish()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            imageUri = data.data
            findViewById<ImageView>(R.id.editImage)?.setImageURI(imageUri)
        }
    }
    private fun showPasswordDialog(id: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("비밀번호 입력")

        // 비밀번호 입력 필드 추가
        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        builder.setView(input)

        // 확인 버튼 처리
        builder.setPositiveButton("확인") { dialog, which ->
            val password = input.text.toString()
            sendWithdrawRequest(id, hashSHA256(password))
        }
        builder.setNegativeButton("취소", { dialog, which -> dialog.cancel() })

        builder.show()
    }

    private fun sendWithdrawRequest(id: String, password: String) {
        val url = "http://172.10.7.78/withdraw"

        val jsonObject = JSONObject()
        jsonObject.put("id", id)
        jsonObject.put("password", password)

        val jsonString = jsonObject.toString()
        val client = OkHttpClient()

        val mediaType = "application/json; charset=utf-8".toMediaType()
        val requestBody = jsonString.toRequestBody(mediaType)

        val request = Request.Builder()
            .url(url) // Replace with your server's URL
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    runOnUiThread {
                        Toast.makeText(this@EditProfileActivity, "탈퇴가 완료 되었습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(this@EditProfileActivity, "프로필 로드 실패", Toast.LENGTH_SHORT).show()
                }
            }
        })

    }

    private fun loadUserProfile(userId: String) {
        // 서버 URL
        val url = "http://172.10.7.78/getuserprofile"

        val jsonObject = JSONObject()
        jsonObject.put("id", userId)

        val jsonString = jsonObject.toString()
        val client = OkHttpClient()

        val mediaType = "application/json; charset=utf-8".toMediaType()
        val requestBody = jsonString.toRequestBody(mediaType)

        val request = Request.Builder()
            .url(url) // Replace with your server's URL
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    // 서버로부터 받은 응답 처리
                    val jsonResponse = JSONObject(response.body?.string())
                    val name = jsonResponse.getString("name")
                    val imageUrl = jsonResponse.getString("image")

                    runOnUiThread {
                        findViewById<TextInputEditText>(R.id.editName).setText(name)
                        if (imageUrl.isNotEmpty()) {
                            imageUri = Uri.parse(imageUrl)
                            val profileImageView = findViewById<ImageView>(R.id.editImage)
                            Glide.with(this@EditProfileActivity)
                                .load(imageUri)
                                .skipMemoryCache(true)
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .into(profileImageView)

                        }
                    }
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(this@EditProfileActivity, "프로필 로드 실패", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun updateProfile(id:String, name: String?, currentPassword: String?, newPassword: String?, imageUri:Uri) {
        val url = "http://172.10.7.78/updateprofile"

        val multipartBodyBuilder = MultipartBody.Builder().setType(MultipartBody.FORM)
        multipartBodyBuilder.addFormDataPart("id", id)

        name?.let { multipartBodyBuilder.addFormDataPart("name", it) }
        currentPassword?.let { multipartBodyBuilder.addFormDataPart("currentPassword", it) }
        newPassword?.let { multipartBodyBuilder.addFormDataPart("newPassword", it) }

        if(imageUri != Uri.EMPTY){
            val inputStream = contentResolver.openInputStream(imageUri)
            val mediaType = "image/*".toMediaTypeOrNull()
            val requestBody = object : RequestBody() {
                override fun contentType(): MediaType? {
                    return mediaType
                }

                override fun contentLength(): Long {
                    return -1 // Unknown content length
                }

                override fun writeTo(sink: BufferedSink) {
                    inputStream?.use { input ->
                        sink.writeAll(input.source())
                    }
                }
            }
            multipartBodyBuilder.addFormDataPart("image", "image.jpg", requestBody)
        }



        val request = Request.Builder()
            .url(url)
            .post(multipartBodyBuilder.build())
            .build()

        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                // Handle the server response here if needed
                if (response.code == 200) {
                    // Response code is 200 - Start a new activity
                    val intent = Intent(this@EditProfileActivity, MypageActivity::class.java)
                    startActivity(intent)
                    // Finish the current activity if needed
//                            finish()
                } else {
                    // 서버로부터 오류 메시지 받기
                    val errorMsg = response.body?.string()?.let {
                        JSONObject(it).getString("message")
                    } ?: "알 수 없는 오류 발생"

                    // 오류 메시지를 사용자에게 표시
                    AlertDialog.Builder(this@EditProfileActivity).apply {
                        setTitle("오류")
                        setMessage(errorMsg)
                        setPositiveButton("확인", null)
                        create().show()
                    }
                }
                // Handle response
            }

            override fun onFailure(call: okhttp3.Call, e: IOException) {
                // Handle failure
                runOnUiThread {
                    Toast.makeText(this@EditProfileActivity, "네트워크 오류", Toast.LENGTH_SHORT).show()
                }
                e.printStackTrace()
            }
        })
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
