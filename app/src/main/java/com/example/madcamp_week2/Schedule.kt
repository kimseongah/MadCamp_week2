package com.example.madcamp_week2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.madcamp_week2.databinding.ActivityNoneBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.GET

interface ApiService {
    @GET("/get_all_busking") // Replace with your actual endpoint
    fun getAllBusking(): Call<List<Busking>>
}
data class Busking(
    val id: Int,
    val title: String,
    val team: String,
    val image_url: String,
    val date: String,
    val location: String,
    val setlist: String,
    val startTime: String,
    val endTime: String
)
class Schedule : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule)
        setupBottomNavigation(R.id.page_2)
        val retrofit = Retrofit.Builder()
            .baseUrl("http://172.10.7.78:80/") // Replace with your API base URL
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiService::class.java)

        val call = apiService.getAllBusking()
        Log.d("Response", "test")
        call.enqueue(object : Callback<List<Busking>> {
            override fun onResponse(call: Call<List<Busking>>, response: Response<List<Busking>>) {
                if (response.isSuccessful) {
                    val buskingList = response.body()
                    Log.d("BuskingList", buskingList.toString())
                    if (buskingList != null && buskingList.isNotEmpty()) {
                        val recyclerView = findViewById<View>(R.id.recyclerViewSchedule) as RecyclerView
                        val adapter = BuskingAdapter(buskingList)
                        recyclerView.adapter = adapter
                        recyclerView.layoutManager = LinearLayoutManager(this@Schedule)
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

            override fun onFailure(call: Call<List<Busking>>, t: Throwable) {
                println("Failed to fetch busking data: ${t.message}")
            }
        })
    }
}