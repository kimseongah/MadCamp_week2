package com.example.madcamp_week2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class CheckRegister : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check_register)
        val receivedTitle = intent.getStringExtra("title")
        val receivedLocation = intent.getStringExtra("location")

        val titleTextView = findViewById<TextView>(R.id.titletext)
        val customButton = findViewById<Button>(R.id.customButton)

        // Set the received location and title to the respective TextViews
        titleTextView.text = "$receivedTitle 공연을"
        customButton.setOnClickListener {
            // Start MainActivity
            val intent = Intent(this@CheckRegister, MainActivity::class.java)
            startActivity(intent)

            // Finish the current activity
            finish()
        }
    }
}