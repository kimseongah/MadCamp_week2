package com.example.madcamp_week2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

class BuskingInfo : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_busking_info)

        var dialog_title : TextView= findViewById(R.id.dialog_title)
        var dialog_team : TextView= findViewById(R.id.dialog_team)
        var dialog_date : TextView= findViewById(R.id.dialog_date)
        var dialog_location : TextView= findViewById(R.id.dialog_location)
        var dialog_time : TextView= findViewById(R.id.dialog_time)
        var dialog_set_list : TextView= findViewById(R.id.dialog_set_list)
        var dialog_cover : ImageView=findViewById(R.id.dialog_top_image)

        dialog_title.setText(intent.getStringExtra("title"))
        dialog_team.setText(intent.getStringExtra("team"))
        dialog_date.setText(intent.getStringExtra("date"))
        dialog_location.setText(intent.getStringExtra("location"))
        dialog_time.setText(intent.getStringExtra("start_time") + "~" +intent.getStringExtra("end_time"))
        dialog_set_list.setText(intent.getStringExtra("setlist"))

        val imageUrl = intent.getStringExtra("image_url")
        // Load the image into the ImageView using Glide
        imageUrl?.let {
            Glide.with(this@BuskingInfo)
                .load(it)
                .apply(RequestOptions().centerCrop()) // Apply transformations if needed
                .into(dialog_cover)
        }


    }
}