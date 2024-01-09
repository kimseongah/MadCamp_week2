package com.example.madcamp_week2

import android.graphics.Bitmap
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.madcamp_week2.BuskingViewHolder
import com.example.madcamp_week2.R
import com.google.android.ads.mediationtestsuite.viewmodels.HeaderViewHolder
import java.net.URL
import java.net.URLConnection

class BuskingAdapter(
    private val buskingList: List<Busking>,
    private val positionList: List<Int>,
    private val headerList: List<Int>,
    private val beforeHeaderList: List<Int>,
    private val listValue: List<Int>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_HEADER = 0
    private val VIEW_TYPE_ITEM = 1
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
        if (holder is HeaderViewHolder) {
            val currentItem = buskingList[listValue[position]]
            holder.textViewStickyHeader.text = currentItem.date
        } else if (holder is BuskingViewHolder) {
            val currentItem = buskingList[listValue[position]]// Bind regular list item data here
//                val currentItem = buskingList[position]

            // Set data to views
            Log.d("imageurl","${currentItem}")
            holder.eventTitle.text = currentItem.title
            holder.eventLocation.text = currentItem.location
            holder.eventTime.text = currentItem.date+" "+currentItem.start_time.substring(0,5) + "~"+ currentItem.end_time.substring(0,5)
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


    }
    override fun getItemViewType(position: Int): Int {
        return if (headerList[position]==1) {
            VIEW_TYPE_HEADER
        } else {
            VIEW_TYPE_ITEM
        }
    }
    override fun getItemCount(): Int {
        // Return the total number of items in the RecyclerView
        return positionList.size
    }



    class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewStickyHeader: TextView = itemView.findViewById(R.id.textViewStickyHeader)
    }


}
