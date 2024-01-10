package com.example.madcamp_week2

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class MainAdapter(private val todayBuskingList: List<Busking>) : RecyclerView.Adapter<MainAdapter.EventViewHolder>(){
    class EventViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val eventImage: ImageView = view.findViewById(R.id.eventImage)
        val eventTitle: TextView = view.findViewById(R.id.eventTitle)
        val eventTime: TextView = view.findViewById(R.id.eventTime)
        val eventLocation: TextView = view.findViewById(R.id.eventLocation)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_layout, parent, false)
        return EventViewHolder(view)
    }
    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val currentItem = todayBuskingList[position]

        // Set data to views
        Log.d("imageurl","${currentItem}")
        holder.eventTitle.text = currentItem.title
        holder.eventLocation.text = currentItem.location
        holder.eventTime.text = currentItem.start_time.substring(0,5) + "~"+ currentItem.end_time.substring(0,5)
//        holder.eventTime.text = currentItem.time
        Glide.with(holder.itemView.context)
            .load(currentItem.image_url)
//            .apply(RequestOptions().placeholder(R.drawable.placeholder)) // Placeholder image while loading (optional)
            .into(holder.eventImage)

//        // Handle click events or any other operations as needed
//        holder.addButton.setOnClickListener {
//            // Handle click action for the add button
//        }
    }
    override fun getItemCount() = todayBuskingList.size

}