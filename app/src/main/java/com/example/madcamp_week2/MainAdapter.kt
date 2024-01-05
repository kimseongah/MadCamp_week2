package com.example.madcamp_week2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class MainAdapter(private val events: List<Event>) : RecyclerView.Adapter<MainAdapter.EventViewHolder>(){
    class EventViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.eventImage)
        val titleView: TextView = view.findViewById(R.id.eventTitle)
        val timeView: TextView = view.findViewById(R.id.eventTime)
        val locationView: TextView = view.findViewById(R.id.eventLocation)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_layout, parent, false)
        return EventViewHolder(view)
    }
    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = events[position]
        Glide.with(holder.itemView.context)
            .load(event.image)
            .into(holder.imageView)
        holder.titleView.text = event.title
        holder.timeView.text = event.time
        holder.locationView.text = event.location
    }
    override fun getItemCount(): Int {
        return events.size
    }

}