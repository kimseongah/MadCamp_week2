package com.example.madcamp_week2

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.madcamp_week2.databinding.ActivityNoneBinding

class NoneActivity : BaseActivity() {

    private lateinit var binding: ActivityNoneBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNoneBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupBottomNavigation(R.id.page_3)

    }

}