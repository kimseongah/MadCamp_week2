package com.example.madcamp_week2

import android.graphics.Bitmap
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.madcamp_week2.BuskingViewHolder
import com.example.madcamp_week2.R
import java.net.URL
import java.net.URLConnection

class BuskingAdapter(private val buskingList: List<Busking>) : RecyclerView.Adapter<BuskingViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BuskingViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.schedule_card_layout, parent, false)
        return BuskingViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: BuskingViewHolder, position: Int) {
        val currentItem = buskingList[position]

        // Set data to views
        Log.d("imageurl","${currentItem.image_url}")
        holder.eventTitle.text = currentItem.title
        holder.eventLocation.text = currentItem.location
//        holder.eventTime.text = currentItem.time
        Glide.with(holder.itemView.context)
            .load(currentItem.image_url)
//            .apply(RequestOptions().placeholder(R.drawable.placeholder)) // Placeholder image while loading (optional)
            .into(holder.eventImage)

//        // Handle click events or any other operations as needed
        holder.addButton.setOnClickListener {
            // Handle click action for the add button
        }
    }

    override fun getItemCount() = buskingList.size
}
