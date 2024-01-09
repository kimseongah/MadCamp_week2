package com.example.madcamp_week2

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.madcamp_week2.R

class BuskingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val eventImage: ImageView = itemView.findViewById(R.id.eventImage)
    val eventTitle: TextView = itemView.findViewById(R.id.eventTitle)
    val eventLocation: TextView = itemView.findViewById(R.id.eventLocation)
    val eventTime: TextView = itemView.findViewById(R.id.eventTime)
    val addButton: ImageView = itemView.findViewById(R.id.addButton)
}
