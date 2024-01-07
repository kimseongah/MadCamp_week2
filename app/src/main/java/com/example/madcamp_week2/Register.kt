package com.example.madcamp_week2

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ImageView
import com.example.madcamp_week2.R

class Register : BaseActivity() {

    private val PICK_IMAGE = 100
    private var imageUri: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        setupBottomNavigation(R.id.page_4)

        val concertImage = findViewById<ImageView>(R.id.concertimage)
        concertImage.setOnClickListener {
            val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(gallery, PICK_IMAGE)
        }


    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            imageUri = data?.data
            findViewById<ImageView>(R.id.concertimage)?.setImageURI(imageUri)
        }
    }
}