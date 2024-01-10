package com.example.madcamp_week2

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Query

interface getallbusking {
    @GET("/get_all_busking") // Replace with your actual endpoint
    fun getAllBusking(@Query("id") id: String): Call<BuskingResponse?>
}
data class Busking(
    val id: Int,
    val title: String,
    val team: String,
    val image_url: String,
    val date: String,
    val location: String,
    val setlist: String,
    val start_time: String,
    val end_time: String
)
data class BuskingResponse(
    val busking_list: List<Busking>,
    val position_list: List<Int>,
    val header_list: List<Int>,
    val beforeheader_list: List<Int>,
    val like_list: List<Int>,
    val listvalue: List<Int>
)
class Schedule : BaseActivity() {
    private lateinit var id : String
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule)
        setupBottomNavigation(R.id.page_2)
        sharedPreferences = getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE)
        id = sharedPreferences.getString("ID", "No ID").toString()
        val retrofit = Retrofit.Builder()
            .baseUrl("http://172.10.7.78:80/") // Replace with your API base URL
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(getallbusking::class.java)

        val call = apiService.getAllBusking(id)
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
                        val listValue = buskingResponse.listvalue
                        val like_list = buskingResponse.like_list
                        Log.d("BuskingList", buskingList.toString())

                        val recyclerView = findViewById<View>(R.id.recyclerViewSchedule) as RecyclerView
                        val adapter = BuskingAdapter(buskingList,positionList,headerList,beforeHeaderList,listValue,like_list,this@Schedule){ clickedItem ->
                            val fragmentTransaction = supportFragmentManager.beginTransaction()
                            val itemFragment = itemFragment()
                            fragmentTransaction.replace(R.id.infoConcert, itemFragment)
                            fragmentTransaction.addToBackStack(null)
                            fragmentTransaction.commit()
                        }
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

            override fun onFailure(call: Call<BuskingResponse?>, t: Throwable) {
                println("Failed to fetch busking data: ${t.message}")
            }
//            private fun showFragmentItemDialog(clickedItem: Busking) {
//                val fragmentManager = supportFragmentManager
//                val fragmentTransaction = fragmentManager.beginTransaction()
//
//                // Create a new instance of your fragment and pass the clicked item data
//                val fragment = itemFragment.newInstance(clickedItem)
//
//                // Add fragment_item.xml to your activity
//                fragmentTransaction.replace(R.id.fragmentContainer, fragment)
//                fragmentTransaction.addToBackStack(null)
//                fragmentTransaction.commit()
//            }
        })
    }
}