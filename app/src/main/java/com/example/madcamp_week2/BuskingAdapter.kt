package com.example.madcamp_week2

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.ads.mediationtestsuite.viewmodels.HeaderViewHolder
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

class BuskingAdapter(
    private val buskingList: List<Busking>,
    private val positionList: List<Int>,
    private val headerList: List<Int>,
    private val beforeHeaderList: List<Int>,

    private val listValue: List<Int>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_HEADER = 0
    private val VIEW_TYPE_ITEM = 1
    private lateinit var id:String

    val client = OkHttpClient()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_HEADER -> {
                val headerView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.sticky_header_layout, parent, false)
                HeaderViewHolder(headerView)
            }
            else -> {
                val itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.schedule_card_layout, parent, false)
                BuskingViewHolder(itemView)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val sharedPreferences = holder.itemView.context.getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE)
        id = sharedPreferences.getString("ID", "No ID").toString()

        if (holder is HeaderViewHolder) {
            val currentItem = buskingList[listValue[position]]
            holder.textViewStickyHeader.text = currentItem.date
        } else if (holder is BuskingViewHolder) {
            val currentItem = buskingList[listValue[position]]// Bind regular list item data here
            // Set data to views
            Log.d("imageurl","${currentItem}")
            holder.eventTitle.text = currentItem.title
            holder.eventLocation.text = currentItem.location
            holder.eventTime.text = currentItem.date+" "+currentItem.start_time.substring(0,5) + "~"+ currentItem.end_time.substring(0,5)
            Glide.with(holder.itemView.context)
                .load(currentItem.image_url)
                .into(holder.eventImage)

            checkFavoriteStatus(currentItem, holder)

            holder.addButton.setOnClickListener {
//                if (isFavorite) {
//                    removeFavorite(currentItem)
//                } else {
                    addFavorite(currentItem)
//                }
            }
        }


    }
    private fun checkFavoriteStatus(buskingItem: Busking, holder: BuskingViewHolder) {
        val url = "http://172.10.7.78/isitfavorite"
        val jsonObject = JSONObject()
        jsonObject.put("id", id)
        jsonObject.put("title", buskingItem.title)
        jsonObject.put("team", buskingItem.team)

        val jsonString = jsonObject.toString()

        val mediaType = "application/json; charset=utf-8".toMediaType()
        val requestBody = jsonString.toRequestBody(mediaType)

        val request = Request.Builder()
            .url(url) // Replace with your server's URL
            .post(requestBody)
            .build()
        CoroutineScope(Dispatchers.IO).launch {
            val message = fetchMessage(request)
            setFavoriteButtonImage(message, holder.addButton, holder.itemView.context)
            }
        }

    private suspend fun fetchMessage(request: Request): String {
        return withContext(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val jsonResponse = JSONObject(response.body?.string())
                    jsonResponse.getString("message")
                } else {
                    Log.d("message", "Response not successful")
                    "not successful"
                }
            } catch (e: IOException) {
                Log.d("message", "onFailure: ${e.message}")
                "onFailure"
            }
        }
    }



    private suspend fun setFavoriteButtonImage(isFavorite: String, button: ImageView, context: Context) {
        if (isFavorite == "True") {
            Log.d("Thread Check", "Current thread: ${Thread.currentThread().name}")

            withContext(Dispatchers.Main) {
            Log.d("Thread Check", "Current thread: ${Thread.currentThread().name}")
                Glide.with(context)
                    .load(R.drawable.baseline_favorite_24)
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(button)
            }
        } else if(isFavorite == "False") {
            button.setBackgroundResource(R.drawable.ic_favorite) // 즐겨찾기 되지 않은 상태 이미지
        }
    }
    private fun addFavorite(buskingItem: Busking) {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://172.10.7.78:80/") // Replace with your API base URL
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val apiService = retrofit.create(BuskingApiService::class.java)
            apiService.addToFavorites(buskingItem).enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        buskingItem.isFavorite = "True"
                        notifyDataSetChanged()
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    // 실패 처리
                }
            })
    }

    private fun removeFavorite(buskingItem: Busking) {
        // 즐겨찾기 삭제 API 호출
        // 성공 시 buskingItem.isFavorite 업데이트 및 UI 변경
    }

    override fun getItemViewType(position: Int): Int {
        return if (headerList[position]==1) {
            VIEW_TYPE_HEADER
        } else {
            VIEW_TYPE_ITEM
        }
    }
    override fun getItemCount(): Int {
        return positionList.size
    }
    class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewStickyHeader: TextView = itemView.findViewById(R.id.textViewStickyHeader)
    }
}