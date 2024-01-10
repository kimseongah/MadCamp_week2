package com.example.madcamp_week2

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.madcamp_week2.BuskingViewHolder
import com.example.madcamp_week2.R
import com.google.android.ads.mediationtestsuite.adapters.ItemsListRecyclerViewAdapter
import com.google.android.ads.mediationtestsuite.viewmodels.HeaderViewHolder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import java.net.URL
import java.net.URLConnection


class BuskingAdapter(
    private val buskingList: List<Busking>,
    private val positionList: List<Int>,
    private val headerList: List<Int>,
    private val beforeHeaderList: List<Int>,
    private val listValue: List<Int>,
    private val like_list: List<Int>,
    private val context: Context,
    private val onItemClick: (Busking) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val like_list1: MutableList<Int> = like_list.toMutableList()

    private val VIEW_TYPE_HEADER = 0
    private val VIEW_TYPE_ITEM = 1
    private val VIEW_TYPE_ITEM_LIKE = 2
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var id : String

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_HEADER -> {
                val headerView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.sticky_header_layout, parent, false)
                HeaderViewHolder(headerView)
            }
            VIEW_TYPE_ITEM -> {
                val itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.schedule_card_layout, parent, false)
                BuskingViewHolder(itemView)
            }
            else -> {
                val itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.schedule_card_layout_like, parent, false)
                BuskingViewHolder(itemView)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is HeaderViewHolder) {
            val currentItem = buskingList[listValue[position]]
            holder.textViewStickyHeader.text = currentItem.date
        } else if (holder is BuskingViewHolder) {
            val currentItem = buskingList[listValue[position]]// Bind regular list item data here
//                val currentItem = buskingList[position]

            // Set data to views
            Log.d("currentItem","${currentItem}")
            holder.eventTitle.text = currentItem.title
            holder.eventLocation.text = currentItem.location
            holder.eventTime.text = currentItem.date+" "+currentItem.start_time.substring(0,5) + "~"+ currentItem.end_time.substring(0,5)
//        holder.eventTime.text = currentItem.time
            Glide.with(holder.itemView.context)
                .load(currentItem.image_url)
//            .apply(RequestOptions().placeholder(R.drawable.placeholder)) // Placeholder image while loading (optional)
                .into(holder.eventImage)
            holder.addButton.setOnClickListener {
                val context = holder.itemView.context

                val apiService = BuskingApiService()

                sharedPreferences = context.getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE)
                id = sharedPreferences.getString("ID", "No ID").toString()


                if (getItemViewType(position) == VIEW_TYPE_ITEM_LIKE) {
                    apiService.removeFromFavorites(id, currentItem.title, currentItem.team) { success ->
                        if (success) {
                            // Handle successful removal from favorites
                            updateViewType(position, 0)
                        } else {
                            // Handle failure
                        }
                    }
                } else {
                    apiService.addToFavorites(id, currentItem.title, currentItem.team) { success ->
                        if (success) {
                            // Handle successful addition to favorites
                            updateViewType(position, 1)
                        } else {
                            // Handle failure
                        }
                    }
                }
            }
//        // Handle click events or any other operations as needed
            holder.eventcard.setOnClickListener {
                // Handle click action for the add button
//                onItemClick(currentItem)
                val context = holder.itemView.context
                val currentItem = buskingList[listValue[position]]

                // Creating an Intent to start the buskingInfo activity
                val intent = Intent(context, BuskingInfo::class.java)

                // Pass any necessary data to the next activity using intent extras
                intent.putExtra("date", currentItem.date)
                intent.putExtra("title",currentItem.title)
                intent.putExtra("team", currentItem.team)
                intent.putExtra("location",currentItem.location)
                intent.putExtra("start_time", currentItem.start_time)
                intent.putExtra("end_time",currentItem.end_time)
                intent.putExtra("setlist", currentItem.setlist)
                intent.putExtra("image_url",currentItem.image_url)
                // For example, passing the busking ID

                // Start the buskingInfo activity
                context.startActivity(intent)
            }

        }


    }
    override fun getItemViewType(position: Int): Int {
        return if (headerList[position]==1) {
            VIEW_TYPE_HEADER
        } else if(like_list1[position]==1) {
            VIEW_TYPE_ITEM_LIKE
        }else{
            VIEW_TYPE_ITEM
        }

    }
    override fun getItemCount(): Int {
        // Return the total number of items in the RecyclerView
        return positionList.size
    }


    interface BuskingAPI {

        // Endpoint to add to favorites
        @POST("/add_to_favorites")
        fun addToFavorites(@Body data: Map<String, String>): Call<Void>

        // Endpoint to remove from favorites
        @POST("/remove_from_favorites")
        fun removeFromFavorites(@Body data: Map<String, String>): Call<Void>
    }
    class BuskingApiService {

        private val retrofit = Retrofit.Builder()
            .baseUrl("http://172.10.7.78:80/") // Replace with your base URL
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        private val buskingAPI = retrofit.create(BuskingAPI::class.java)

        fun addToFavorites(id: String, title: String, team: String, callback: (Boolean) -> Unit) {
            val data = mapOf("id" to id, "title" to title, "team" to team)

            buskingAPI.addToFavorites(data).enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        callback(true) // Success
                    } else {
                        callback(false) // Failed
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    callback(false) // Failed due to exception
                }
            })
        }

        fun removeFromFavorites(id: String, title: String, team: String, callback: (Boolean) -> Unit) {
            val data = mapOf("id" to id, "title" to title, "team" to team)

            buskingAPI.removeFromFavorites(data).enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        callback(true) // Success
                    } else {
                        callback(false) // Failed
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    callback(false) // Failed due to exception
                }
            })
        }
    }
    fun updateViewType(position: Int, newViewType: Int) {

        Log.d("Adapter1", "Updated like_list1: $like_list1")
        if (like_list1[position] != newViewType) {
            like_list1[position] = newViewType
//            notifyItemChanged(position)
//            notifyDataSetChanged()
            Log.d("Adapter", "Updated like_list1: $like_list1")
            notifyDataSetChanged()
        }
    }

    class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewStickyHeader: TextView = itemView.findViewById(R.id.textViewStickyHeader)
    }

}
