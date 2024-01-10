package com.example.madcamp_week2

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.gson.Gson
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

class MypageActivity: BaseActivity() {

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var id : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mypage)
        setupBottomNavigation(R.id.page_4)
        sharedPreferences = getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE)
        id = sharedPreferences.getString("ID", "No ID").toString()

        val retrofit = Retrofit.Builder()
            .baseUrl("http://172.10.7.78:80/") // Replace with your API base URL
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(getlikebusking::class.java)

        val call = apiService.getLikeBusking(id)
        Log.d("Response", "test")
        call.enqueue(object : Callback<BuskingResponse?> {
            override fun onResponse(call: Call<BuskingResponse?>, response: Response<BuskingResponse?>) {
                if (response.isSuccessful) {
                    val buskingResponse:BuskingResponse? = response.body()
                    if (buskingResponse != null) {
                        val buskingList = buskingResponse.busking_list
                        val positionList = buskingResponse.position_list
                        val headerList = buskingResponse.header_list
                        val beforeHeaderList = buskingResponse.beforeheader_list
                        val like_list = buskingResponse.like_list
                        val listValue = buskingResponse.listvalue
                        Log.d("BuskingList", buskingList.toString())

                        val recyclerView = findViewById<View>(R.id.recyclerView) as RecyclerView
                        val adapter = BuskingAdapter(buskingList,positionList,headerList,beforeHeaderList,listValue,like_list,this@MypageActivity){ clickedItem ->
                            val fragmentTransaction = supportFragmentManager.beginTransaction()
                            val itemFragment = itemFragment()
                            fragmentTransaction.replace(R.id.infoConcert, itemFragment)
                            fragmentTransaction.addToBackStack(null)
                            fragmentTransaction.commit()
                        }
                        recyclerView.adapter = adapter
                        recyclerView.layoutManager = LinearLayoutManager(this@MypageActivity)
                        adapter.notifyDataSetChanged()
                    } else {
                        // Handle the case when the buskingList is null or empty
                        // For instance, you can show a message or handle this scenario accordingly
                        Log.d("Response", "Empty or null buskingList")
                    }
                }else {
                    Log.d("Failed to fetch busking data:", "${response.message()}")
                }
            }

            override fun onFailure(call: Call<BuskingResponse?>, t: Throwable) {
                println("Failed to fetch busking data: ${t.message}")
            }
        })

        val editInfo = findViewById<CardView>(R.id.card_edittext)
        editInfo.setOnClickListener{
            startEditProfileActivity()
        }

        refreshUserProfile()
    }

    fun startEditProfileActivity() {
        val intent = Intent(this, EditProfileActivity::class.java)
        startActivityForResult(intent, 1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            refreshUserProfile()
        }
    }

    private fun refreshUserProfile() {
        // 사용자 프로필 데이터를 새로고침하는 로직
        CoroutineScope(Dispatchers.Main).launch {
            // Fetch and update UI
            val userInfo = fetchUserInfo(id ?: "")
            updateUI(userInfo)
        }
    }


    private suspend fun fetchUserInfo(id: String): UserInfo {
        // Switch to IO thread for network call
        return withContext(Dispatchers.IO) {
            val url = "http://172.10.7.78/mypageprofile"

            var jsonObject = JSONObject()
            jsonObject.put("id", id)

            val jsonString = jsonObject.toString()
            val client = OkHttpClient()

            val requestBody = jsonString.toRequestBody(jsonMediaType)
            val request = Request.Builder()
                .url(url = url)
                .post(requestBody)
                .build()

            client.newCall(request).execute().use { response ->
                val responseString = response.body?.string()
                return@withContext (if (response.isSuccessful && responseString != null) {
                    jsonObject = JSONObject(responseString)
                    val name = jsonObject.optString("name")
                    val id = jsonObject.optString("id")
                    val image = jsonObject.optString("image")
                    UserInfo(name,id,image)
                } else {
                    UserInfo()
                })!!
            }
        }
    }

    private fun updateUI(userInfo: UserInfo?) {
        userInfo?.let {
            val profileImageView = findViewById<ImageView>(R.id.profile_image)
            val nameTextView = findViewById<TextView>(R.id.name)
            val idTextView = findViewById<TextView>(R.id.user_id)

            // Use Glide or another library to load the image from the URL
            Glide.with(this)
                .load(it.profilePictureUri)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(profileImageView)

            nameTextView.text = it.name
            idTextView.text = it.id
        }
    }

    data class UserInfo(
        val name: String = "",
        val id: String = "",
        val profilePictureUri: String = ""
    )
}
interface getlikebusking {
    @GET("/get_like_busking") // Replace with your actual endpoint
    fun getLikeBusking(@Query("id") id: String): Call<BuskingResponse>
}